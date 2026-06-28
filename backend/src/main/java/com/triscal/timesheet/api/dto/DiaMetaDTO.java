package com.triscal.timesheet.api.dto;
import java.math.BigDecimal;
/** Metadados de um dia do período para o grid editável. */
public record DiaMetaDTO(int ano, int mes, int dia, String diaSemana, boolean feriado, char status,
                         BigDecimal horasNormaisEsperadas, BigDecimal ausenciaBhe, BigDecimal ausenciaAbono) {}
