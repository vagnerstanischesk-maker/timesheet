package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;
import java.util.List;

/** Estado do dia para a tela de lançamento. horasNormaisEsperadas = jornada do dia (8h, ou horas a trabalhar do feriado). */
public record DiaDTO(
    int ano, int mes, int dia, char status,
    BigDecimal ausenciaBhe, BigDecimal ausenciaAbono,
    boolean feriado, BigDecimal horasNormaisEsperadas,
    List<LancamentoTarefaDTO> tarefas) {}
