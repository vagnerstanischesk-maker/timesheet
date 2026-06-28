package com.triscal.timesheet;

import com.triscal.timesheet.api.dto.DiaDTO;
import com.triscal.timesheet.api.dto.LancamentoTarefaDTO;
import com.triscal.timesheet.domain.service.LancamentoValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LancamentoValidatorTest {

    private DiaDTO dia(BigDecimal co) {
        return new DiaDTO(2026, 6, 1, 'A', BigDecimal.ZERO, BigDecimal.ZERO, false, new BigDecimal("8"),
            List.of(new LancamentoTarefaDTO(100, (short)1, (short)1, "Backend",
                BigDecimal.ZERO, co, BigDecimal.ZERO, BigDecimal.ZERO, false, null)));
    }

    @Test void aceitaMultiploDeMeiaHora() {
        assertTrue(LancamentoValidator.validar(dia(new BigDecimal("6.5"))).isEmpty());
    }

    @Test void rejeitaValorForaDoPasso() {
        assertFalse(LancamentoValidator.validar(dia(new BigDecimal("6.3"))).isEmpty());
    }

    @Test void rejeitaAcimaDe24h() {
        var d = new DiaDTO(2026, 6, 1, 'A', BigDecimal.ZERO, BigDecimal.ZERO, false, new BigDecimal("8"),
            List.of(new LancamentoTarefaDTO(100,(short)1,(short)1,"x",
                BigDecimal.ZERO, new BigDecimal("25"), BigDecimal.ZERO, BigDecimal.ZERO, false, null)));
        assertFalse(LancamentoValidator.validar(d).isEmpty());
    }

    @Test void multiploDeMeiaHoraDireto() {
        assertTrue(LancamentoValidator.multiploDeMeia(new BigDecimal("0.5")));
        assertFalse(LancamentoValidator.multiploDeMeia(new BigDecimal("0.25")));
    }
}
