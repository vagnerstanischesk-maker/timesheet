package com.triscal.timesheet;

import com.triscal.timesheet.domain.service.CalculadoraBhe;
import com.triscal.timesheet.domain.service.CalculadoraBhe.ResultadoBhe;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraBheTest {

    private static final BigDecimal Z = BigDecimal.ZERO;
    private static BigDecimal h(String v) { return new BigDecimal(v); }

    @Test void semBancoNaoGeraMovimento() {
        var r = CalculadoraBhe.calcular('F', false, h("3"), Z, false);
        assertTrue(r.vazio());
    }

    @Test void pjSempre50MesmoEmDomingo() {
        var r = CalculadoraBhe.calcular('E', true, h("2"), Z, true);
        assertEquals(0, r.bhe50().compareTo(h("2")));
        assertEquals(0, r.bhe100().compareTo(Z));
    }

    @Test void cltDiaUtil50() {
        var r = CalculadoraBhe.calcular('F', true, h("2"), Z, false);
        assertEquals(0, r.bhe50().compareTo(h("2")));
        assertEquals(0, r.bhe100().compareTo(Z));
    }

    @Test void cltDomingoOuFeriado100() {
        ResultadoBhe r = CalculadoraBhe.calcular('F', true, h("2"), Z, true);
        assertEquals(0, r.bhe100().compareTo(h("2")));
        assertEquals(0, r.bhe50().compareTo(Z));
    }

    @Test void ausenciaBheReduzExcedente() {
        var r = CalculadoraBhe.calcular('F', true, h("3"), h("1"), false);
        assertEquals(0, r.bhe50().compareTo(h("2")));
    }

    @Test void semExcedenteNaoGeraMovimento() {
        var r = CalculadoraBhe.calcular('F', true, h("2"), h("2"), false);
        assertTrue(r.vazio());
    }
}
