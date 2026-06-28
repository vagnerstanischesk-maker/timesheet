package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/** Resumo de um dia dentro da quinzena (para a grade do colaborador). */
public record QuinzenaDiaDTO(
    int ano, int mes, int dia, String diaSemana, boolean feriado,
    char status, BigDecimal totalHoras) {}
