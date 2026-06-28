package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/** Total de horas no período por projeto/fase/tarefa. */
public record QuinzenaTarefaDTO(
    Integer cdProjeto, String projetoDescricao, Short seqFase, String faseDescricao,
    Short seqTarefa, String tarefaDescricao,
    BigDecimal horasCo, BigDecimal horasNc, BigDecimal horasCoExtra, BigDecimal horasNcExtra,
    BigDecimal total) {}
