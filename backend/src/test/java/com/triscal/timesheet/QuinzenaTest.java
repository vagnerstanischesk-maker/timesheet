package com.triscal.timesheet;

import com.triscal.timesheet.domain.service.Quinzena;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuinzenaTest {

    @Test void periodo1Vai11a25() {
        List<LocalDate> d = Quinzena.datasDoPeriodo(2026, 6, 1, 11, 26);
        assertEquals(LocalDate.of(2026,6,11), d.get(0));
        assertEquals(LocalDate.of(2026,6,25), d.get(d.size()-1));
        assertEquals(15, d.size());
    }

    @Test void periodo2CruzaParaMesSeguinte() {
        List<LocalDate> d = Quinzena.datasDoPeriodo(2026, 6, 2, 11, 26);
        assertEquals(LocalDate.of(2026,6,26), d.get(0));
        assertEquals(LocalDate.of(2026,7,10), d.get(d.size()-1));
    }

    @Test void referenciaDeDiaInicioDoMes() {
        // dia 5/jul pertence ao período 2 com referência junho
        int[] r = Quinzena.referenciaDe(LocalDate.of(2026,7,5), 11, 26);
        assertArrayEquals(new int[]{2026,6,2}, r);
    }

    @Test void referenciaDeDiaMeioDoMes() {
        int[] r = Quinzena.referenciaDe(LocalDate.of(2026,6,15), 11, 26);
        assertArrayEquals(new int[]{2026,6,1}, r);
    }

    // ---- modelo novo: cortes 1/16 (1–15 e 16–fim do mês) ----

    @Test void cortes116Periodo1Vai1a15() {
        List<LocalDate> d = Quinzena.datasDoPeriodo(2026, 6, 1, 1, 16);
        assertEquals(LocalDate.of(2026,6,1), d.get(0));
        assertEquals(LocalDate.of(2026,6,15), d.get(d.size()-1));
        assertEquals(15, d.size());
    }

    @Test void cortes116Periodo2VaiAteFimDoMes() {
        List<LocalDate> d = Quinzena.datasDoPeriodo(2026, 6, 2, 1, 16);
        assertEquals(LocalDate.of(2026,6,16), d.get(0));
        assertEquals(LocalDate.of(2026,6,30), d.get(d.size()-1)); // junho tem 30 dias
        assertEquals(15, d.size());
    }

    @Test void cortes116Periodo2Fevereiro() {
        List<LocalDate> d = Quinzena.datasDoPeriodo(2026, 2, 2, 1, 16);
        assertEquals(LocalDate.of(2026,2,16), d.get(0));
        assertEquals(LocalDate.of(2026,2,28), d.get(d.size()-1)); // 2026 não bissexto
    }

    @Test void cortes116ReferenciaDe() {
        assertArrayEquals(new int[]{2026,6,1}, Quinzena.referenciaDe(LocalDate.of(2026,6,10), 1, 16));
        assertArrayEquals(new int[]{2026,6,2}, Quinzena.referenciaDe(LocalDate.of(2026,6,20), 1, 16));
    }

    @Test void periodoAnterior() {
        assertArrayEquals(new int[]{2026,6,1}, Quinzena.periodoAnterior(2026,6,2));
        assertArrayEquals(new int[]{2026,5,2}, Quinzena.periodoAnterior(2026,6,1));
        assertArrayEquals(new int[]{2025,12,2}, Quinzena.periodoAnterior(2026,1,1));
    }

    @Test void periodoSeguinte() {
        assertArrayEquals(new int[]{2026,6,2}, Quinzena.periodoSeguinte(2026,6,1));
        assertArrayEquals(new int[]{2026,7,1}, Quinzena.periodoSeguinte(2026,6,2));
        assertArrayEquals(new int[]{2027,1,1}, Quinzena.periodoSeguinte(2026,12,2));
    }
}
