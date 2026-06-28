package com.triscal.timesheet;

import com.triscal.timesheet.domain.service.CalculadoraExecucao;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraExecucaoTest {
    private static BigDecimal n(String v){return new BigDecimal(v);}

    @Test void percentualPorHoras() {
        assertEquals(0, CalculadoraExecucao.percExecucaoHoras(n("840"), n("1200")).compareTo(n("70.00")));
    }
    @Test void previstoZeroRetornaZero() {
        assertEquals(0, CalculadoraExecucao.percExecucaoHoras(n("10"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO));
    }
    @Test void desvioInformadoMenorQueHoras() {
        // informado 62, horas 70 -> desvio -8
        assertEquals(0, CalculadoraExecucao.desvio(n("62"), n("70")).compareTo(n("-8.00")));
    }
}
