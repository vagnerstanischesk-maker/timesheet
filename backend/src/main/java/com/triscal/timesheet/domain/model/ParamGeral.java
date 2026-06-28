package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "param_geral")
public class ParamGeral {
    @Id @Column(name="nm_parametro") private String nome;
    @Column(name="vl_parametro") private String valor;
    protected ParamGeral(){}
    public String getNome(){return nome;}
    public String getValor(){return valor;}
}
