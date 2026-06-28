package com.triscal.timesheet.domain.service;

import java.math.BigDecimal;

/**
 * Cálculo do banco de horas (BHE) ao APROVAR um dia. Regra crítica, pura e testável,
 * replicada do trigger legado TRG_TIMESHEET_DIA_BANCO_HE:
 *  - participa apenas quem possui banco de horas;
 *  - excedente = horas extras do dia (NCEXTRA + COEXTRA) − ausência de BHE do dia;
 *  - PJ (tipo 'E'): todo excedente é 50% (não há 100%);
 *  - CLT (tipo 'F'): 100% se domingo ou feriado; caso contrário 50%.
 * Retorna os créditos (positivos) a somar em cada saldo.
 */
public final class CalculadoraBhe {

    public record ResultadoBhe(BigDecimal bhe50, BigDecimal bhe100) {
        public boolean vazio() {
            return bhe50.compareTo(BigDecimal.ZERO) == 0 && bhe100.compareTo(BigDecimal.ZERO) == 0;
        }
    }

    private CalculadoraBhe() {}

    public static ResultadoBhe calcular(
            char tipoFuncionario, boolean possuiBancoHoras,
            BigDecimal horasExtrasDia, BigDecimal ausenciaBhe, boolean domingoOuFeriado) {

        BigDecimal zero = BigDecimal.ZERO;
        if (!possuiBancoHoras) return new ResultadoBhe(zero, zero);

        BigDecimal excedente = nz(horasExtrasDia).subtract(nz(ausenciaBhe));
        if (excedente.compareTo(zero) == 0) return new ResultadoBhe(zero, zero);

        if (tipoFuncionario == 'E') {                 // PJ: só 50%
            return new ResultadoBhe(excedente, zero);
        }
        // CLT
        if (domingoOuFeriado) return new ResultadoBhe(zero, excedente);
        return new ResultadoBhe(excedente, zero);
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
