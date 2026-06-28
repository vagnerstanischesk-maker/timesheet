package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vertical")
public class Vertical {
    @Id @Column(name="cd_vertical") private Short id;
    @Column(name="ds_vertical", nullable=false) private String descricao;
    @Column(name="st_vertical", nullable=false) private String status = "AT";

    protected Vertical(){}
    public Vertical(Short id){ this.id = id; }
    public Short getId(){return id;}
    public void setId(Short v){this.id=v;}
    public String getDescricao(){return descricao;}
    public void setDescricao(String v){this.descricao=v;}
    public String getStatus(){return status;}
    public void setStatus(String v){this.status=v;}
}
