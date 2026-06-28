package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa")
public class Empresa {
    @Id @Column(name="cd_empresa") private Integer id;
    @Column(name="nm_empresa", nullable=false) private String nome;
    @Column(name="sg_filial") private String sigla;
    @Column(name="in_selecionavel") private String selecionavel = "N";

    protected Empresa(){}
    public Integer getId(){return id;}
    public String getNome(){return nome;}
    public String getSigla(){return sigla;}
    public String getSelecionavel(){return selecionavel;}
}
