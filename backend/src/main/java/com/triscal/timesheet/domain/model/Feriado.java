package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "feriado")
public class Feriado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="feri_cd_feriado") private Long id;
    @Column(name="feri_nm_feriado", nullable=false) private String nome;
    @Column(name="feri_nr_ano") private Short ano;
    @Column(name="feri_nr_mes", nullable=false) private Short mes;
    @Column(name="feri_nr_dia", nullable=false) private Short dia;
    @Column(name="feri_tp_feriado", nullable=false) private String tipo;   // NN/NF/LN/LF
    @Column(name="filial_cd_filial") private Integer filial;
    @Column(name="feri_nr_horas_a_trabalhar") private BigDecimal horasATrabalhar = BigDecimal.ZERO;
    @Column(name="feri_stt_inativo", nullable=false) private char inativo = 'N';
    @Column(name="dta_ult_alt") private OffsetDateTime dtUltAlt;

    public Feriado(){}
    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public String getNome(){return nome;} public void setNome(String v){this.nome=v;}
    public Short getAno(){return ano;} public void setAno(Short v){this.ano=v;}
    public Short getMes(){return mes;} public void setMes(Short v){this.mes=v;}
    public Short getDia(){return dia;} public void setDia(Short v){this.dia=v;}
    public String getTipo(){return tipo;} public void setTipo(String v){this.tipo=v;}
    public Integer getFilial(){return filial;} public void setFilial(Integer v){this.filial=v;}
    public BigDecimal getHorasATrabalhar(){return horasATrabalhar;} public void setHorasATrabalhar(BigDecimal v){this.horasATrabalhar=v;}
    public char getInativo(){return inativo;} public void setInativo(char v){this.inativo=v;}
    public void setDtUltAlt(OffsetDateTime v){this.dtUltAlt=v;}
}
