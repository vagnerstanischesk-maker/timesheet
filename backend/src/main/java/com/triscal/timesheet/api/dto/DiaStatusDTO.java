package com.triscal.timesheet.api.dto;
/** Projeção: status de um dia de log (para relatórios). */
public record DiaStatusDTO(Integer cdFuncionario, String nome, Short ano, Short mes, Short dia, Character status) {}
