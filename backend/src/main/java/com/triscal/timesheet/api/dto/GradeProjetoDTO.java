package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;
import java.util.List;

/** Horas de um projeto por dia do período (horasPorDia alinhado com a lista de dias). */
public record GradeProjetoDTO(Integer cdProjeto, String projetoDescricao,
                              List<BigDecimal> horasPorDia, BigDecimal total) {}
