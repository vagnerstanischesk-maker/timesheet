package com.triscal.timesheet.api.dto;
import java.util.List;
/** Linha do grid: tarefa + células (horas) por dia, alinhadas com a lista de dias. */
public record TarefaLinhaDTO(Integer cdProjeto, String projetoDescricao, Short seqFase, String faseDescricao,
                             Short seqTarefa, String descricaoTarefa, boolean externa, List<CelulaDTO> celulas) {}
