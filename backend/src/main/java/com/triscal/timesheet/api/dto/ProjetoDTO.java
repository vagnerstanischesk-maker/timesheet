package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/**
 * Projeto para listagem/edição. Datas em ISO (yyyy-MM-dd). Os totais (horas/valor/custo
 * previsto e realizado) são somatórios das fases — somente leitura, ignorados ao salvar.
 */
public record ProjetoDTO(
        Integer cdProjeto, String descricao, boolean interno,
        Integer cdClifor, String nomeCliente,
        Integer cdEmpresa,
        String status, String dataInicio, String dataFim,
        boolean aceitaCo, boolean aceitaNc,
        String tipoFaturamento,
        Integer gerenteTecnico, Integer gerenteComercial,
        Short vertical, Short tipoContrato,
        boolean comissao, String observacao,
        BigDecimal horasPrevistas, BigDecimal horasRealizadas,
        BigDecimal valorPrevisto, BigDecimal valorRealizado,
        BigDecimal custoPrevisto, BigDecimal custoRealizado) {}
