package com.triscal.timesheet.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Período de log (quinzena). Cortes parametrizáveis. Dois modelos suportados:
 *  - Dentro do mês (corte1 = 1, ex.: 1/16): Período 1 = 1..(corte2-1); Período 2 = corte2..fim do mês.
 *    O mês de referência é o próprio mês do dia.
 *  - Cruzando o mês (corte1 > 1, ex.: 11/26 — legado): Período 1 = corte1..(corte2-1);
 *    Período 2 = corte2 (mês ref) .. (corte1-1) do mês seguinte.
 * O "mês de referência" (anoRef/mesRef) é sempre o mês onde o período começa.
 */
public final class Quinzena {

    private Quinzena() {}

    public static List<LocalDate> datasDoPeriodo(int anoRef, int mesRef, int periodo, int corte1, int corte2) {
        if (periodo != 1 && periodo != 2)
            throw new IllegalArgumentException("Período deve ser 1 ou 2.");
        LocalDate inicio, fim;
        if (periodo == 1) {
            inicio = LocalDate.of(anoRef, mesRef, corte1);
            fim = LocalDate.of(anoRef, mesRef, corte2 - 1);
        } else {
            inicio = LocalDate.of(anoRef, mesRef, corte2);
            if (corte1 <= 1) {
                // período termina no fim do próprio mês (ex.: 16..30/31)
                fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
            } else {
                // período cruza para o mês seguinte (legado, ex.: 26..10)
                LocalDate proximoMes = inicio.plusMonths(1).withDayOfMonth(1);
                fim = LocalDate.of(proximoMes.getYear(), proximoMes.getMonthValue(), corte1 - 1);
            }
        }
        List<LocalDate> dias = new ArrayList<>();
        for (LocalDate d = inicio; !d.isAfter(fim); d = d.plusDays(1)) dias.add(d);
        return dias;
    }

    /** Dado um dia qualquer, devolve [anoRef, mesRef, periodo] a que ele pertence. */
    public static int[] referenciaDe(LocalDate dia, int corte1, int corte2) {
        int d = dia.getDayOfMonth();
        if (corte1 <= 1) {
            // modelo dentro do mês: o período pertence sempre ao próprio mês
            return new int[]{dia.getYear(), dia.getMonthValue(), d < corte2 ? 1 : 2};
        }
        if (d >= corte1 && d < corte2) {
            return new int[]{dia.getYear(), dia.getMonthValue(), 1};
        }
        if (d >= corte2) { // pertence ao período 2 do próprio mês
            return new int[]{dia.getYear(), dia.getMonthValue(), 2};
        }
        // d < corte1: pertence ao período 2 do mês anterior
        LocalDate anterior = dia.minusMonths(1);
        return new int[]{anterior.getYear(), anterior.getMonthValue(), 2};
    }

    /** Referência [anoRef, mesRef, periodo] do período imediatamente anterior. */
    public static int[] periodoAnterior(int anoRef, int mesRef, int periodo) {
        if (periodo == 2) return new int[]{anoRef, mesRef, 1};
        LocalDate mesAnterior = LocalDate.of(anoRef, mesRef, 1).minusMonths(1);
        return new int[]{mesAnterior.getYear(), mesAnterior.getMonthValue(), 2};
    }

    /** Referência [anoRef, mesRef, periodo] do período imediatamente seguinte. */
    public static int[] periodoSeguinte(int anoRef, int mesRef, int periodo) {
        if (periodo == 1) return new int[]{anoRef, mesRef, 2};
        LocalDate mesSeguinte = LocalDate.of(anoRef, mesRef, 1).plusMonths(1);
        return new int[]{mesSeguinte.getYear(), mesSeguinte.getMonthValue(), 1};
    }
}
