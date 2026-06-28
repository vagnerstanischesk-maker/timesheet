package com.triscal.timesheet.api.dto;
import java.math.BigDecimal;
import java.util.List;
/** Dados do período inteiro para o grid editável da quinzena (estilo "Meu dia" para todos os dias). */
public record GradeEdicaoDTO(String nome, int anoRef, int mesRef, int periodo,
                             BigDecimal saldoBhe50, BigDecimal saldoBhe100,
                             List<DiaMetaDTO> dias, List<TarefaLinhaDTO> tarefas) {}
