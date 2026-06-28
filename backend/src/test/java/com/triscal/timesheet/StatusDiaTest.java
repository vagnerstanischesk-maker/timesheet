package com.triscal.timesheet;

import com.triscal.timesheet.domain.model.StatusDia;
import com.triscal.timesheet.domain.model.TipoFuncionario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusDiaTest {
    @Test void mapeiaCodigosDeStatus() {
        assertEquals(StatusDia.FECHADO, StatusDia.of('F'));
        assertEquals('P', StatusDia.APROVADO.codigo());
    }
    @Test void mapeiaTipoFuncionario() {
        assertEquals(TipoFuncionario.PJ, TipoFuncionario.of('E'));
        assertEquals(TipoFuncionario.CLT, TipoFuncionario.of('F'));
    }
}
