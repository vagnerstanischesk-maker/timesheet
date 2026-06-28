package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/**
 * Linha do relatório de movimentação de BHE no período. Uma linha por movimento
 * (crédito por aprovação de horas extras ou lançamento manual / débito).
 * data em ISO (yyyy-MM-dd); origem = "Manual" ou "Aprovação".
 */
public record RelatorioBheDTO(
        Integer cdFuncionario, String nome, String data,
        BigDecimal valor50, BigDecimal valor100,
        String origem, String usuario) {}
