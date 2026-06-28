package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/** Fase do projeto. Datas em ISO (yyyy-MM-dd). Status: INI/ENC/NIN. */
public record FaseDTO(Short seqFase, String descricao, String status,
                      String dataInicio, String dataFim,
                      BigDecimal horasPrevistas, BigDecimal horasRealizadas, BigDecimal percConclusao,
                      BigDecimal valorPrevisto, BigDecimal valorRealizado,
                      BigDecimal custoPrevisto, BigDecimal custoRealizado) {}
