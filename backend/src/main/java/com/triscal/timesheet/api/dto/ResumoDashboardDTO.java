package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResumoDashboardDTO(
    int ano, int mes,
    BigDecimal horasMes, BigDecimal percFaturavel, BigDecimal saldoBheEquipe,
    List<ProjetoExecucao> projetos) {

    public record ProjetoExecucao(Integer cdProjeto, String descricao,
                                  BigDecimal realizado, BigDecimal previsto, BigDecimal percExecucao) {}
}
