package com.triscal.timesheet.api.dto;

import java.util.List;

/** Identidade + papéis resolvidos pela aplicação (não vêm do token). */
public record MeResponse(Integer id, String nome, String email, List<String> papeis, boolean cadastrado) {}
