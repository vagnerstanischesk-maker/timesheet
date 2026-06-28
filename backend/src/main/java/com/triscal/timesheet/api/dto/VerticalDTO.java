package com.triscal.timesheet.api.dto;

/** Vertical = linha de negócio da empresa (cadastro interno gerenciável). */
public record VerticalDTO(Short cdVertical, String descricao, String status) {}
