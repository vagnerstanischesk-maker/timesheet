package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "andamento_projeto_hist")
public class AndamentoProjetoHist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="cd_projeto", nullable=false) private Integer cdProjeto;
    @Column(name="seq_projeto_fase") private Short seqFase;
    @Column(name="seq_projeto_tarefa") private Short seqTarefa;
    @Column(name="dt_referencia", nullable=false) private LocalDate dtReferencia;
    @Column(name="perc_informado", nullable=false) private BigDecimal percInformado;
    @Column(name="perc_derivado_horas") private BigDecimal percDerivadoHoras;
    @Column(name="usur_cd_usur", nullable=false) private String usuario;
    @Column(name="dt_registro", nullable=false) private OffsetDateTime dtRegistro = OffsetDateTime.now();

    protected AndamentoProjetoHist(){}
    public AndamentoProjetoHist(Integer proj, Short fase, Short tarefa, LocalDate ref,
                                BigDecimal informado, BigDecimal derivado, String usuario){
        this.cdProjeto=proj; this.seqFase=fase; this.seqTarefa=tarefa; this.dtReferencia=ref;
        this.percInformado=informado; this.percDerivadoHoras=derivado; this.usuario=usuario;
    }
    public LocalDate getDtReferencia(){return dtReferencia;}
    public BigDecimal getPercInformado(){return percInformado;}
    public BigDecimal getPercDerivadoHoras(){return percDerivadoHoras;}
}
