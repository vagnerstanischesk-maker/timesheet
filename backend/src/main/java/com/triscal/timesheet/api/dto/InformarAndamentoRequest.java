package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

public record InformarAndamentoRequest(Short seqFase, Short seqTarefa, BigDecimal percInformado) {}
