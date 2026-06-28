package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/**
 * Linha do relatório de acompanhamento de projetos (visão consolidada de todo o
 * projeto, somando todos os períodos de log). Horas vendidas = previstas do projeto;
 * horas executadas = soma de todos os lançamentos; % por horas = executadas/vendidas;
 * % informado = último % registrado pelo gestor.
 * diferença % = (% por horas − % informado); diferença em horas = diferença % × horas vendidas
 * (representa o atraso/estouro do projeto: positivo = horas consumidas além do andamento informado).
 */
public record RelatorioAcompanhamentoDTO(
        Integer cdProjeto, String descricao,
        Integer cdClifor, String nomeCliente,
        BigDecimal horasVendidas, BigDecimal horasExecutadas,
        BigDecimal percExecHoras, BigDecimal percInformado,
        BigDecimal percDiferenca, BigDecimal diferencaHoras) {}
