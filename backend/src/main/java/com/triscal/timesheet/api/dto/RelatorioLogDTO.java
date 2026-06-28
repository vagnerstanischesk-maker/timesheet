package com.triscal.timesheet.api.dto;
/** Linha do relatório de logs não fechados: colaborador, período (quinzena) e status consolidado. */
public record RelatorioLogDTO(Integer cdFuncionario, String nome, int anoRef, int mesRef, int periodo, String status) {}
