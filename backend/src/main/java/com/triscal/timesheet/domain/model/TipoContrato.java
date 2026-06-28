package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_contrato")
public class TipoContrato {
    @Id @Column(name="cd_tipo_contrato") private Short id;
    @Column(name="ds_tipo_contrato", nullable=false) private String descricao;
    @Column(name="st_tipo_contrato", nullable=false) private String status = "AT";

    protected TipoContrato(){}
    public Short getId(){return id;}
    public String getDescricao(){return descricao;}
    public String getStatus(){return status;}
}
