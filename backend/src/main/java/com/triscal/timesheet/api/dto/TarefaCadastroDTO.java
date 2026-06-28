package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/** Tarefa do projeto. Datas em ISO (yyyy-MM-dd). Status: INI/ENC/NIN. */
public record TarefaCadastroDTO(Short seqFase, Short seqTarefa, String descricao, String status, boolean externa,
                                String dataInicio, String dataFim,
                                BigDecimal horasPrevistas, BigDecimal horasRealizadas, BigDecimal percConclusao,
                                BigDecimal valorPrevisto, BigDecimal valorRealizado,
                                BigDecimal custoPrevisto, BigDecimal custoRealizado) {}
