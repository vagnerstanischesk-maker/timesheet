package com.triscal.timesheet.api.dto;

/** Cliente (CLIFOR) — todos os campos são obrigatórios no cadastro (validado no serviço). */
public record ClienteDTO(
        Integer cdClifor, String razaoSocial, String fantasia, String cnpj, String status,
        Integer filialOrigem,
        String contatoFinanceiro, String emailFinanceiro, String site,
        String logradouro, String numero, String complemento,
        String cidade, String estado, String cep) {}
