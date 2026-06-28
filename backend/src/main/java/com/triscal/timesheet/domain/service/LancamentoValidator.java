package com.triscal.timesheet.domain.service;

import com.triscal.timesheet.api.dto.DiaDTO;
import com.triscal.timesheet.api.dto.LancamentoTarefaDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras de validação do lançamento do dia (Triscal). Puras e testáveis.
 * - mínimo 0,5h e múltiplos de 0,5h por campo;
 * - total do dia <= 24h (sanidade);
 * - tarefas somente-leitura (externas/OTRS) não podem ser alteradas pelo colaborador.
 */
public final class LancamentoValidator {

    public static final BigDecimal PASSO = new BigDecimal("0.5");
    public static final BigDecimal MAX_DIA = new BigDecimal("24");

    private LancamentoValidator() {}

    public static List<String> validar(DiaDTO dia) {
        List<String> erros = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (LancamentoTarefaDTO t : dia.tarefas()) {
            for (BigDecimal v : List.of(
                    nz(t.horasNc()), nz(t.horasCo()), nz(t.horasNcExtra()), nz(t.horasCoExtra()))) {
                if (v.signum() < 0) erros.add("Horas negativas não são permitidas.");
                if (v.signum() > 0 && !multiploDeMeia(v))
                    erros.add("Lançamento deve ser múltiplo de 0,5h (mínimo 30 min): tarefa "
                        + t.cdProjeto() + "/" + t.seqFase() + "/" + t.seqTarefa() + ".");
                total = total.add(v);
            }
        }
        total = total.add(nz(dia.ausenciaBhe())).add(nz(dia.ausenciaAbono()));
        if (total.compareTo(MAX_DIA) > 0)
            erros.add("Total do dia (" + total + "h) excede o máximo de 24h.");
        return erros;
    }

    public static boolean multiploDeMeia(BigDecimal v) {
        // v / 0,5 deve ser inteiro
        return v.remainder(PASSO).compareTo(BigDecimal.ZERO) == 0;
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
