package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/** Horas de uma tarefa no dia. Campos externos (OTRS) chegam read-only (somenteLeitura=true). */
public record LancamentoTarefaDTO(
    Integer cdProjeto, Short seqFase, Short seqTarefa, String descricaoTarefa,
    BigDecimal horasNc, BigDecimal horasCo, BigDecimal horasNcExtra, BigDecimal horasCoExtra,
    boolean somenteLeitura, String obs) {}
