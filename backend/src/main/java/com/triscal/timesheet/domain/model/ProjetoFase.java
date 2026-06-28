package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity @Table(name="projeto_fase") @IdClass(ProjetoFase.PK.class)
public class ProjetoFase {
    @Id @Column(name="cd_projeto") private Integer cdProjeto;
    @Id @Column(name="seq_projeto_fase") private Short seqFase;
    @Column(name="ds_fase", nullable=false) private String descricao;
    @Column(name="st_faseproj") private String status="NIN";   // INI / ENC / NIN
    @Column(name="dt_inicio_prev") private LocalDate dataInicio;
    @Column(name="dt_fim_previsto") private LocalDate dataFim;
    @Column(name="nr_horasprevistas") private BigDecimal horasPrevistas;
    @Column(name="nr_horasreal") private BigDecimal horasRealizadas;
    @Column(name="perc_conclusao_fase") private BigDecimal percConclusao;
    @Column(name="vl_previsto") private BigDecimal valorPrevisto;
    @Column(name="vl_realizado") private BigDecimal valorRealizado;
    @Column(name="vl_custo_previsto") private BigDecimal custoPrevisto;
    @Column(name="vl_custo_realizado") private BigDecimal custoRealizado;
    @Column(name="dt_ult_alt") private OffsetDateTime dtUltAlt;

    protected ProjetoFase(){}
    public ProjetoFase(Integer p, Short f){ cdProjeto=p; seqFase=f; }
    public Integer getCdProjeto(){return cdProjeto;} public Short getSeqFase(){return seqFase;}
    public String getDescricao(){return descricao;} public void setDescricao(String v){this.descricao=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public LocalDate getDataInicio(){return dataInicio;} public void setDataInicio(LocalDate v){this.dataInicio=v;}
    public LocalDate getDataFim(){return dataFim;} public void setDataFim(LocalDate v){this.dataFim=v;}
    public BigDecimal getHorasPrevistas(){return horasPrevistas;} public void setHorasPrevistas(BigDecimal v){this.horasPrevistas=v;}
    public BigDecimal getHorasRealizadas(){return horasRealizadas;} public void setHorasRealizadas(BigDecimal v){this.horasRealizadas=v;}
    public BigDecimal getPercConclusao(){return percConclusao;} public void setPercConclusao(BigDecimal v){this.percConclusao=v;}
    public BigDecimal getValorPrevisto(){return valorPrevisto;} public void setValorPrevisto(BigDecimal v){this.valorPrevisto=v;}
    public BigDecimal getValorRealizado(){return valorRealizado;} public void setValorRealizado(BigDecimal v){this.valorRealizado=v;}
    public BigDecimal getCustoPrevisto(){return custoPrevisto;} public void setCustoPrevisto(BigDecimal v){this.custoPrevisto=v;}
    public BigDecimal getCustoRealizado(){return custoRealizado;} public void setCustoRealizado(BigDecimal v){this.custoRealizado=v;}
    public void setDtUltAlt(OffsetDateTime v){this.dtUltAlt=v;}

    public static class PK implements java.io.Serializable {
        private Integer cdProjeto; private Short seqFase;
        public PK(){} public PK(Integer p, Short f){cdProjeto=p;seqFase=f;}
        @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof PK k)) return false;
            return java.util.Objects.equals(cdProjeto,k.cdProjeto)&&java.util.Objects.equals(seqFase,k.seqFase);}
        @Override public int hashCode(){return java.util.Objects.hash(cdProjeto,seqFase);}
    }
}
