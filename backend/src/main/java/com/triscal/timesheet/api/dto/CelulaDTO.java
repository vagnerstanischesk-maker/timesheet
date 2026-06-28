package com.triscal.timesheet.api.dto;
import java.math.BigDecimal;
/** Horas de uma tarefa num dia (alinhado com a lista de dias). */
public record CelulaDTO(BigDecimal nc, BigDecimal co, BigDecimal ncExtra, BigDecimal coExtra) {}
