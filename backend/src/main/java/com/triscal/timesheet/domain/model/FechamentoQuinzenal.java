package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fechamento_quinzenal")
public class FechamentoQuinzenal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="cd_projeto", nullable=false) private Integer cdProjeto;
    @Column(name="lope_nr_ano", nullable=false) private Short ano;
    @Column(name="lope_nr_mes", nullable=false) private Short mes;
    @Column(name="peri_cd_periodo", nullable=false) private Short periodo;
    @Column(name="horas_executadas", nullable=false) private BigDecimal horasExecutadas;
    @Column(name="horas_previstas") private BigDecimal horasPrevistas;
    @Column(name="perc_execucao_horas") private BigDecimal percExecucaoHoras;
    @Column(name="perc_informado") private BigDecimal percInformado;
    @Column(name="desvio_perc") private BigDecimal desvioPerc;
    @Column(name="usur_cd_usur", nullable=false) private String usuario;
    @Column(name="dt_fechamento", nullable=false) private OffsetDateTime dtFechamento = OffsetDateTime.now();

    protected FechamentoQuinzenal(){}
    public FechamentoQuinzenal(Integer proj, Short ano, Short mes, Short periodo, BigDecimal exec,
                               BigDecimal prev, BigDecimal percHoras, BigDecimal informado,
                               BigDecimal desvio, String usuario){
        this.cdProjeto=proj; this.ano=ano; this.mes=mes; this.periodo=periodo; this.horasExecutadas=exec;
        this.horasPrevistas=prev; this.percExecucaoHoras=percHoras; this.percInformado=informado;
        this.desvioPerc=desvio; this.usuario=usuario;
    }
}
