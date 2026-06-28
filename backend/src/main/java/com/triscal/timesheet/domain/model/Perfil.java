package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perfil")
public class Perfil {
    @Id
    @Column(name = "cd_perfil")
    private Short id;

    @Column(name = "ds_perfil", nullable = false)
    private String descricao;   // COLABORADOR, GESTOR, GESTOR_PROJETO, ADMIN_RH, DIRETORIA

    protected Perfil() {}
    public Short getId() { return id; }
    public String getDescricao() { return descricao; }
}
