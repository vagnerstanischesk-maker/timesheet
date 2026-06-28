package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "projeto_tarefa_funcionario")
@IdClass(AlocacaoTarefa.PK.class)
public class AlocacaoTarefa {
    @Id @Column(name="cd_projeto") private Integer cdProjeto;
    @Id @Column(name="seq_projeto_fase") private Short seqFase;
    @Id @Column(name="seq_projeto_tarefa") private Short seqTarefa;
    @Id @Column(name="cd_funcionario") private Integer cdFuncionario;
    @Column(name="st_alocacao") private String status = "INI";   // INI / ENC / NIN
    @Column(name="taxa_horaria_normal") private BigDecimal taxaNormal;
    @Column(name="taxa_horaria_extra") private BigDecimal taxaExtra;

    protected AlocacaoTarefa(){}
    public AlocacaoTarefa(Integer p, Short f, Short t, Integer func){ cdProjeto=p;seqFase=f;seqTarefa=t;cdFuncionario=func; }
    public Integer getCdProjeto(){return cdProjeto;}
    public Short getSeqFase(){return seqFase;}
    public Short getSeqTarefa(){return seqTarefa;}
    public Integer getCdFuncionario(){return cdFuncionario;}
    public String getStatus(){return status;}
    public void setStatus(String s){this.status=s;}
    public BigDecimal getTaxaNormal(){return taxaNormal;} public void setTaxaNormal(BigDecimal v){this.taxaNormal=v;}
    public BigDecimal getTaxaExtra(){return taxaExtra;} public void setTaxaExtra(BigDecimal v){this.taxaExtra=v;}

    public static class PK implements java.io.Serializable {
        private Integer cdProjeto; private Short seqFase; private Short seqTarefa; private Integer cdFuncionario;
        public PK(){} public PK(Integer p, Short f, Short t, Integer fu){cdProjeto=p;seqFase=f;seqTarefa=t;cdFuncionario=fu;}
        @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof PK k)) return false;
            return java.util.Objects.equals(cdProjeto,k.cdProjeto)&&java.util.Objects.equals(seqFase,k.seqFase)&&java.util.Objects.equals(seqTarefa,k.seqTarefa)&&java.util.Objects.equals(cdFuncionario,k.cdFuncionario);}
        @Override public int hashCode(){return java.util.Objects.hash(cdProjeto,seqFase,seqTarefa,cdFuncionario);}
    }
}
