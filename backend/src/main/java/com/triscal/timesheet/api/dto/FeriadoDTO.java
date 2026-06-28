package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

public record FeriadoDTO(Long id, String nome, Short ano, Short mes, Short dia, String tipo,
                         Integer filial, BigDecimal horasATrabalhar, boolean inativo) {}
