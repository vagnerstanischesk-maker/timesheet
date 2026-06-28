#!/usr/bin/env python3
"""
ETL de migração Oracle (schema TIMESHEET) -> PostgreSQL (schema novo, Flyway V1..V8).

Status de tarefa/alocação: o app novo usa INI/ENC/NIN. Assumimos que o legado marca o
registro ATIVO como 'AT' — traduzimos 'AT' -> 'INI' (iniciada/lançável) e o restante -> 'ENC'
(encerrada). Ajuste o CASE nos SELECTs se os códigos do legado forem outros.

- Lê do Oracle com python-oracledb em modo "thin" (não exige Oracle Instant Client).
- Escreve no PostgreSQL com psycopg2 (execute_values, em lotes).
- Mapeia colunas legado -> novo via SELECT explícito (renomeia/deriva no próprio SQL).
- Converte charset automaticamente (o driver devolve str unicode; o destino é UTF-8).
- Durante a carga, desativa FKs/triggers no Postgres (session_replication_role=replica)
  para não depender da ordem e evitar problemas com a auto-FK de funcionario(aprovador).

Uso:
  pip install -r requirements.txt
  cp .env.example .env   # preencha as credenciais
  python etl.py            # carrega tudo
  python etl.py --truncate # limpa as tabelas-alvo antes (recomendado em recarga total)

NUNCA versione o .env com credenciais reais.
"""
import os, sys, argparse
import oracledb
import psycopg2
from psycopg2.extras import execute_values

BATCH = 1000

# Cada item: (tabela_pg, [colunas_pg...], SELECT no Oracle na MESMA ordem das colunas_pg)
SPECS = [
    ("empresa", ["cd_empresa","nm_empresa","pr_iss"],
     "SELECT CD_EMPRESA, NM_EMPRESA, PR_ISS FROM TIMESHEET.EMPRESA"),

    ("clifor", ["cd_clifor","nm_razaosocial","nm_fantasia","nr_cnpj","st_clifor","cd_filial_origem"],
     "SELECT CD_CLIFOR, NM_RAZAOSOCIAL, NM_FANTASIA, TO_CHAR(NR_CNPJ), ST_CLIFOR, CD_FILIAL_ORIGEM FROM TIMESHEET.CLIFOR"),

    ("funcionario", ["cd_funcionario","nm_funcionario","tp_funcionario","cd_empresa","func_cd_func_aprovador",
                      "in_possui_banco_horas","func_nr_saldo_bhe50","func_nr_saldo_bhe100","ds_email","cdg_usur",
                      "dt_admissao","dt_rescisao","st_funcionario"],
     """SELECT CD_FUNCIONARIO, NM_FUNCIONARIO, TP_FUNCIONARIO, CD_EMPRESA, FUNC_CD_FUNC_APROVADOR,
               NVL(IN_POSSUI_BANCO_HORAS,'N'), NVL(FUNC_NR_SALDO_BHE50,0), NVL(FUNC_NR_SALDO_BHE100,0),
               DS_EMAIL, CDG_USUR, DT_ADMISSAO, DT_RESCISAO,
               CASE WHEN DT_RESCISAO IS NULL THEN 'AT' ELSE 'IN' END
          FROM TIMESHEET.FUNCIONARIO"""),

    ("projeto", ["cd_projeto","ds_projeto","in_interno","cd_clifor","cd_empresa","st_projeto",
                 "dt_inicio_prev","nr_horasprevistas","aceita_horas_co","aceita_horas_nc"],
     """SELECT CD_PROJETO, DS_PROJETO, NVL(IN_INTERNO,'N'), CD_CLIFOR, CD_EMPRESA, ST_PROJETO,
               DT_INICIO_PREV, NR_HORASPREVISTAS, NVL(ACEITA_HORAS_CO_S_N,'S'), NVL(ACEITA_HORAS_NC_S_N,'S')
          FROM TIMESHEET.PROJETO"""),

    ("projeto_fase", ["cd_projeto","seq_projeto_fase","ds_fase","st_faseproj","dt_inicio_prev",
                      "nr_horasprevistas","perc_conclusao_fase"],
     """SELECT CD_PROJETO, SEQ_PROJETO_FASE, DS_FASE, ST_FASEPROJ, DT_INICIO_PREV,
               NR_HORASPREVISTAS, PERC_CONCLUSAO_FASE FROM TIMESHEET.PROJETO_FASE"""),

    ("projeto_tarefa", ["cd_projeto","seq_projeto_fase","seq_projeto_tarefa","ds_tarefa","st_tarefa",
                        "in_tarefa_externa","dt_inicio_prev","nr_horasprevistas","perc_conclusao_tarefa"],
     """SELECT CD_PROJETO, SEQ_PROJETO_FASE, SEQ_PROJETO_TAREFA, DS_TAREFA,
               CASE WHEN NVL(ST_TAREFA,'AT')='AT' THEN 'INI' ELSE 'ENC' END,
               NVL(IN_TAREFA_EXTERNA,'N'), DT_INICIO_PREV, NR_HORASPREVISTAS, PERC_CONCLUSAO_TAREFA
          FROM TIMESHEET.PROJETO_TAREFA"""),

    ("projeto_tarefa_funcionario", ["cd_projeto","seq_projeto_fase","seq_projeto_tarefa","cd_funcionario",
                                    "st_alocacao","taxa_horaria_normal","taxa_horaria_extra","perc_conclusao_tarefa_func"],
     """SELECT CD_PROJETO, SEQ_PROJETO_FASE, SEQ_PROJETO_TAREFA, FUNC_CD_FUNC,
               CASE WHEN NVL(ST_ALOCACAO,'AT')='AT' THEN 'INI' ELSE 'ENC' END,
               TAXA_HORARIA_NORMAL_FUNC, TAXA_HORARIA_EXTRA_FUNC, PERC_CONCLUSAO_TAREFA_FUNC
          FROM TIMESHEET.PROJETO_TAREFA_FUNCIONARIO"""),

    ("jornada_trabalho", ["cd_funcionario","dt_inicio_vig","dt_fim_vig","horas_dia"],
     """SELECT CD_FUNCIONARIO, DT_INICIO_VIGENCIA, DT_FIM_VIGENCIA,
               NVL(HORA_FIM,8) - NVL(HORA_INICIO,0) FROM TIMESHEET.JORNADA_TRABALHO"""),

    ("feriado", ["feri_nm_feriado","feri_nr_ano","feri_nr_mes","feri_nr_dia","feri_tp_feriado",
                 "filial_cd_filial","feri_nr_horas_a_trabalhar","feri_stt_inativo"],
     """SELECT FERI_NM_FERIADO, FERI_NR_ANO, FERI_NR_MES, FERI_NR_DIA, FERI_TP_FERIADO,
               FILIAL_CD_FILIAL, NVL(FERI_NR_HORAS_A_TRABALHAR,0), NVL(FERI_STT_INATIVO,'N')
          FROM TIMESHEET.FERIADO"""),

    ("log_periodo", ["cd_funcionario","lope_nr_ano","lope_nr_mes","peri_cd_periodo","lope_stt_log"],
     """SELECT FUNC_CD_FUNC, LOPE_NR_ANO, LOPE_NR_MES, PERI_CD_PERIODO, NVL(LOPE_STT_LOG,'A')
          FROM TIMESHEET.LOG_PERIODO"""),

    ("timesheet_dia", ["cd_funcionario","lope_nr_ano","lope_nr_mes","log_nr_dia","log_nr_hnormal",
                       "log_qn_ausbhe","log_qn_ausabn","st_timesheet_dia","obs"],
     """SELECT FUNC_CD_FUNC, LOPE_NR_ANO, LOPE_NR_MES, LOG_NR_DIA, NVL(LOG_NR_HNORMAL,0),
               NVL(LOG_QN_AUSBHE,0), NVL(LOG_QN_AUSABN,0), NVL(ST_TIMESHEET_DIA,'A'), OBS
          FROM TIMESHEET.TIMESHEET_DIA"""),

    ("timesheet_dia_tarefa", ["cd_funcionario","cd_projeto","seq_projeto_fase","seq_projeto_tarefa",
                              "lope_nr_ano","lope_nr_mes","log_nr_dia","horas_nr_nc","horas_nr_co",
                              "horas_nr_ncextra","horas_nr_coextra","horas_tx_obs"],
     """SELECT FUNC_CD_FUNC, CD_PROJETO, SEQ_PROJETO_FASE, SEQ_PROJETO_TAREFA, LOPE_NR_ANO, LOPE_NR_MES,
               LOG_NR_DIA, NVL(HORAS_NR_NC,0), NVL(HORAS_NR_CO,0), NVL(HORAS_NR_NCEXTRA,0),
               NVL(HORAS_NR_COEXTRA,0), HORAS_TX_OBS FROM TIMESHEET.TIMESHEET_DIA_TAREFA"""),

    ("movimentacao_saldo_bhe", ["cd_funcionario","mosa_dt_data","mosa_vl_abatimento50","mosa_vl_abatimento100",
                                "mosa_in_lancamento_manual","lope_nr_ano","lope_nr_mes","log_nr_dia"],
     """SELECT FUNC_CD_FUNC, MOSA_DT_DATA, NVL(MOSA_VL_ABATIMENTO50,0), NVL(MOSA_VL_ABATIMENTO100,0),
               NVL(MOSA_IN_LANCAMENTO_MANUAL,'N'), LOPE_NR_ANO, LOPE_NR_MES, LOG_NR_DIA
          FROM TIMESHEET.MOVIMENTACAO_SALDO_BHE"""),
]

def norm(v):
    # remove espaços à direita de CHAR fixos do Oracle
    return v.rstrip() if isinstance(v, str) else v

def migrar(truncate=False):
    ora = oracledb.connect(
        user=os.environ["ORA_USER"], password=os.environ["ORA_PASSWORD"],
        dsn=os.environ["ORA_DSN"])  # ex.: host:1521/servico
    pg = psycopg2.connect(
        host=os.environ.get("PG_HOST","localhost"), port=os.environ.get("PG_PORT","5432"),
        dbname=os.environ.get("PG_DB","timesheet"), user=os.environ.get("PG_USER","timesheet"),
        password=os.environ.get("PG_PASSWORD","timesheet"))
    pg.autocommit = False
    pgc = pg.cursor()
    pgc.execute("SET session_replication_role = replica;")  # desativa FKs/triggers na carga
    try:
        if truncate:
            for tabela, *_ in reversed(SPECS):
                pgc.execute(f"TRUNCATE TABLE {tabela} CASCADE;")
            print("Tabelas-alvo truncadas.")
        for tabela, cols, sql in SPECS:
            oc = ora.cursor(); oc.execute(sql)
            insert = f"INSERT INTO {tabela} ({','.join(cols)}) VALUES %s"
            total = 0
            while True:
                rows = oc.fetchmany(BATCH)
                if not rows: break
                vals = [tuple(norm(c) for c in r) for r in rows]
                execute_values(pgc, insert, vals)
                total += len(vals)
            print(f"{tabela:32s} {total:>8d} linhas")
            oc.close()
        # ressincroniza as sequências IDENTITY do Postgres (feriado, movimentacao)
        pgc.execute("SELECT setval(pg_get_serial_sequence('feriado','feri_cd_feriado'), "
                    "COALESCE((SELECT MAX(feri_cd_feriado) FROM feriado),1));")
        pgc.execute("SELECT setval(pg_get_serial_sequence('movimentacao_saldo_bhe','mosa_nr_seq'), "
                    "COALESCE((SELECT MAX(mosa_nr_seq) FROM movimentacao_saldo_bhe),1));")
        pgc.execute("SET session_replication_role = DEFAULT;")
        pg.commit()
        print("Migração concluída e commitada.")
    except Exception as e:
        pg.rollback(); print("ERRO, rollback:", e); raise
    finally:
        pgc.close(); pg.close(); ora.close()

if __name__ == "__main__":
    ap = argparse.ArgumentParser()
    ap.add_argument("--truncate", action="store_true", help="limpa as tabelas-alvo antes da carga")
    migrar(**vars(ap.parse_args()))
