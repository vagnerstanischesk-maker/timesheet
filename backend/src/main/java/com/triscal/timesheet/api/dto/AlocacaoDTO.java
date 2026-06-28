package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;

/** Alocação de funcionário na tarefa. nomeFuncionario é só leitura. Status: INI/ENC/NIN. */
public record AlocacaoDTO(Short seqFase, Short seqTarefa, Integer cdFuncionario, String nomeFuncionario,
                          String status, BigDecimal taxaNormal, BigDecimal taxaExtra) {}
