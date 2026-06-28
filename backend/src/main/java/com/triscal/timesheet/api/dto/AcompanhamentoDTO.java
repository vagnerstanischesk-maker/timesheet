package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record AcompanhamentoDTO(
    Integer cdProjeto, String descricao,
    BigDecimal horasPeriodo, BigDecimal horasAcumuladas, BigDecimal horasPrevistas,
    BigDecimal percExecucaoHoras, BigDecimal ultimoPercInformado,
    List<PontoHistorico> historico) {

    public record PontoHistorico(String dataReferencia, BigDecimal percInformado, BigDecimal percDerivadoHoras) {}
}
