package com.triscal.timesheet.api.dto;

/** Subordinado com dias em aberto/atrasados (para alerta ao aprovador). */
public record InadimplenteDTO(Integer aprovadorId, String aprovadorEmail, String aprovadorNome,
                              String colaboradorNome, long diasAtrasados) {}
