create or replace trigger TRG_TIMESHEET_DIA_BANCO_HE
after update of ST_TIMESHEET_DIA 
 ON TIMESHEET_DIA
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
declare
  vHORAS_NR_NCEXTRA   number;
  vHORAS_NR_COEXTRA   number;
  vTP_FUNCIONARIO     funcionario.TP_FUNCIONARIO%type;
  v_SEMANA            NUMBER(1);
  vContFeriadoDomingo NUMBER(1) := 0;
  vHORASEXTRAS50      NUMBER;
  vHORASEXTRAS100     NUMBER;
begin
  if(:NEW.ST_TIMESHEET_DIA = 'P' /*Aprovado*/ AND :OLD.ST_TIMESHEET_DIA <> 'P') then
    --Acabou de aprovar o dia, devo atualizar o saldo BHE

    select nvl(sum(nvl(HORAS_NR_NCEXTRA,0)),0), nvl(sum(nvl(HORAS_NR_COEXTRA,0)),0)
      into vHORAS_NR_NCEXTRA, vHORAS_NR_COEXTRA
      from TIMESHEET_DIA_TAREFA
     where FUNC_CD_FUNC = :NEW.FUNC_CD_FUNC
       and LOPE_NR_ANO = :NEW.LOPE_NR_ANO
       and LOPE_NR_MES = :NEW.LOPE_NR_MES
       and LOG_NR_DIA = :NEW.LOG_NR_DIA;

    select TP_FUNCIONARIO into vTP_FUNCIONARIO /*E=Empresa, F=CLT*/
      from FUNCIONARIO
     where CD_FUNCIONARIO = :NEW.FUNC_CD_FUNC;    

    if vTP_FUNCIONARIO = 'E' then
      --Pessoa Juridica não possui hora extra 100%, apenas 50%
      vHORASEXTRAS50  := (nvl(vHORAS_NR_NCEXTRA,0) + nvl(vHORAS_NR_COEXTRA,0)) - nvl(:NEW.LOG_QN_AUSBHE,0);
      vHORASEXTRAS100 := 0;

    else
      --CLT possui hora extra 100% caso Domingo ou Feriado

      SELECT TO_NUMBER(TO_CHAR(TO_DATE(:NEW.LOPE_NR_ANO||TO_CHAR(:NEW.LOPE_NR_MES,'09')||TO_CHAR(:NEW.LOG_NR_DIA,'09'),'RRRRMMDD'),'D'))
        INTO v_SEMANA FROM DUAL;

      -- Domingo 
      IF( (v_SEMANA = 1) /*OR (v_SEMANA = 7)*/ ) THEN
        vContFeriadoDomingo := 1;
      ELSE
        -- Verifica se existe feriado para o dia a ser inserido
        SELECT count(*) into vContFeriadoDomingo 
          FROM FERIADO
         WHERE FERI_STT_INATIVO = 'N'
           AND FERI_NR_DIA = :NEW.LOG_NR_DIA
           AND FERI_NR_MES = :NEW.LOPE_NR_MES
           AND ( (FERI_TP_FERIADO = 'LN'
              AND FERI_NR_ANO = :NEW.LOPE_NR_ANO
              AND FILIAL_CD_FILIAL = (SELECT CD_EMPRESA FROM FUNCIONARIO
                                       WHERE CD_FUNCIONARIO = :NEW.FUNC_CD_FUNC))
            OR (FERI_TP_FERIADO = 'NN'
            AND FERI_NR_ANO = :NEW.LOPE_NR_ANO)
            OR (FERI_TP_FERIADO IN ('NF','LF')) );
      END IF;

      if vContFeriadoDomingo > 0 then
        vHORASEXTRAS50  := 0;
        vHORASEXTRAS100 := (nvl(vHORAS_NR_NCEXTRA,0) + nvl(vHORAS_NR_COEXTRA,0)) - nvl(:NEW.LOG_QN_AUSBHE,0);
      else
        vHORASEXTRAS50  := (nvl(vHORAS_NR_NCEXTRA,0) + nvl(vHORAS_NR_COEXTRA,0)) - nvl(:NEW.LOG_QN_AUSBHE,0);
        vHORASEXTRAS100 := 0;
      end if;
    end if;  

--p_debug('vHORAS_NR_NCEXTRA='||nvl(vHORAS_NR_NCEXTRA,-1)||', vHORAS_NR_COEXTRA='||nvl(vHORAS_NR_COEXTRA,-1)||'.');
--p_debug('vHORASEXTRAS50='||nvl(vHORASEXTRAS50,-1)||', vHORASEXTRAS100='||nvl(vHORASEXTRAS100,-1)||', :NEW.LOG_QN_AUSBHE='||nvl(:NEW.LOG_QN_AUSBHE,-1)||'.');

    if nvl(vHORASEXTRAS50,0) <> 0 OR nvl(vHORASEXTRAS100,0) <> 0 then
  
      INSERT INTO MOVIMENTACAO_SALDO_BHE (FUNC_CD_FUNC, MOSA_DT_DATA, MOSA_VL_ABATIMENTO50, MOSA_VL_ABATIMENTO100, USUR_CDG_USUR, MOSA_IN_LANCAMENTO_MANUAL) 
                                  VALUES (:NEW.FUNC_CD_FUNC
                                        , TO_DATE(:NEW.LOPE_NR_ANO||TO_CHAR(:NEW.LOPE_NR_MES,'09')||TO_CHAR(:NEW.LOG_NR_DIA,'09'),'RRRRMMDD')
                                        , nvl(vHORASEXTRAS50,0) * -1
                                        , nvl(vHORASEXTRAS100,0) * -1
                                        , :NEW.USUR_CD_USUR
                                        , 'N');
    
    end if;
  
  end if;

exception
  when others then
    RAISE_APPLICATION_ERROR(-20001, 'Erro no trigger TRG_TIMESHEET_DIA_BANCO_HE  (' || TO_CHAR(SQLCODE) || '). Favor informar ao administrador.') ;
end;
end;
/

select cd_funcionario,  f.func_nr_saldo_bhe50, f.func_nr_saldo_bhe100
from funcionario f where f.cd_funcionario in (47,255);
47	174.5	45.5
255	-55	8

SELECT FUNC_CD_FUNC, MOSA_DT_DATA, MOSA_VL_ABATIMENTO50, MOSA_VL_ABATIMENTO100,
  f.func_nr_saldo_bhe50, f.func_nr_saldo_bhe100,
  USUR_CDG_USUR, MOSA_IN_LANCAMENTO_MANUAL, MOSA_NR_SEQ
FROM MOVIMENTACAO_SALDO_BHE m, funcionario f
where m.func_cd_func = f.cd_funcionario and func_cd_func in ( 47, 255 )
  and MOSA_DT_DATA >= to_date('20131201','YYYYMMDD')
order by 1,2;

dia7=7 sab
dia8=6 dom
dia9=2 seg
dia10=-8 ter
saldo=7
47	174.5	45.5
47	181.5	45.5

dia28=5 sab
dia29=4 dom
dia30=-8 seg
dia31=3 feriado
saldo=4
255	-55	8
255	-58	15

delete from MOVIMENTACAO_SALDO_BHE 
where func_cd_func in ( 47, 255 )
  and MOSA_DT_DATA >= to_date('20131201','YYYYMMDD');
  
update timesheet_dia set st_timesheet_dia = 'F' where func_cd_func = 255 and log_nr_dia in (28,29,30,31) and lope_nr_mes = 12 and lope_nr_ano = 2013;
update timesheet_dia set st_timesheet_dia = 'A' where func_cd_func = 255 and log_nr_dia in (30) and lope_nr_mes = 12 and lope_nr_ano = 2013;
commit;

select * from timesheet_dia where func_cd_func = 255 and log_nr_dia in (28,29,30,31) and lope_nr_mes = 12 and lope_nr_ano = 2013;

update timesheet_dia set st_timesheet_dia = 'P' where func_cd_func = 47 and log_nr_dia in (7,8,9,10) and lope_nr_mes = 12 and lope_nr_ano = 2013;
commit;
