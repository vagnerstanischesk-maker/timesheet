package com.triscal.timesheet.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Cálculo do % de execução por horas e do desvio em relação ao % informado pelo gestor. */
public final class CalculadoraExecucao {

    private CalculadoraExecucao() {}

    /** % de execução por horas = realizado / previsto * 100 (0 se previsto nulo/zero). */
    public static BigDecimal percExecucaoHoras(BigDecimal realizado, BigDecimal previsto) {
        if (previsto == null || previsto.signum() == 0) return BigDecimal.ZERO;
        return nz(realizado).multiply(BigDecimal.valueOf(100))
            .divide(previsto, 2, RoundingMode.HALF_UP);
    }

    /** Desvio = % informado − % por horas (positivo = gestor reporta adiante das horas). */
    public static BigDecimal desvio(BigDecimal percInformado, BigDecimal percHoras) {
        return nz(percInformado).subtract(nz(percHoras)).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
