package com.triscal.timesheet.api.dto;

/** Tarefa que o colaborador pode lançar (com nomes de projeto e fase para escolha por nome). */
public record TarefaAlocadaDTO(Integer cdProjeto, String projetoDescricao, Short seqFase, String faseDescricao,
                               Short seqTarefa, String descricaoTarefa, boolean externa) {}
