package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;
import java.util.List;

/** Visão da quinzena: colaborador, período, dias, grade projeto × dia, detalhe por tarefa e saldo BHE. */
public record QuinzenaDTO(
    String nome,
    int anoRef, int mesRef, int periodo,
    BigDecimal saldoBhe50, BigDecimal saldoBhe100,
    List<QuinzenaDiaDTO> dias,
    List<GradeProjetoDTO> grade,
    List<QuinzenaTarefaDTO> porTarefa) {}
