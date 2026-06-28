package com.triscal.timesheet.api.dto;

/** Resultado da aprovação por quinzena: cada dia foi aprovado individualmente. */
public record AprovacaoResumo(int diasAprovados, int diasIgnorados) {}
