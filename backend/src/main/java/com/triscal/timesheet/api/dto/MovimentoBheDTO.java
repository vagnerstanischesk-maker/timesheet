package com.triscal.timesheet.api.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record MovimentoBheDTO(Integer cdFuncionario, LocalDate data, BigDecimal valor50, BigDecimal valor100) {}
