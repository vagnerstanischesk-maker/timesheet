package com.triscal.timesheet.api.dto;
/** Projeto para seleção (cliente -> projeto) no acompanhamento. */
public record ProjetoListaDTO(Integer cdProjeto, String descricao, Integer cdClifor, String nomeCliente, boolean interno) {}
