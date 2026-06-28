package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.api.dto.TarefaAlocadaDTO;
import com.triscal.timesheet.domain.model.AlocacaoTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AlocacaoTarefaRepository extends JpaRepository<AlocacaoTarefa, AlocacaoTarefa.PK> {

    // Tarefas em que o funcionário está alocado e ativo (alocação Iniciada e tarefa não Encerrada),
    // dentro da data de validade da tarefa (início/fim), com nomes de projeto e fase para escolha por nome.
    @Query("""
        select new com.triscal.timesheet.api.dto.TarefaAlocadaDTO(
            t.cdProjeto, p.descricao, t.seqFase, pf.descricao, t.seqTarefa, t.descricao, t.externa = 'S')
        from AlocacaoTarefa a, ProjetoTarefa t, Projeto p, ProjetoFase pf
        where a.cdFuncionario = :func and a.status = 'INI'
          and t.cdProjeto = a.cdProjeto and t.seqFase = a.seqFase and t.seqTarefa = a.seqTarefa
          and p.id = t.cdProjeto
          and pf.cdProjeto = t.cdProjeto and pf.seqFase = t.seqFase
          and (t.status is null or t.status <> 'ENC')
          and (t.dataInicio is null or t.dataInicio <= :data)
          and (t.dataFim is null or t.dataFim >= :data)
        order by p.descricao, t.seqFase, t.seqTarefa""")
    List<TarefaAlocadaDTO> tarefasDoFuncionario(@Param("func") Integer func, @Param("data") LocalDate data);

    boolean existsByCdProjetoAndSeqFaseAndSeqTarefaAndCdFuncionarioAndStatus(
        Integer cdProjeto, Short seqFase, Short seqTarefa, Integer cdFuncionario, String status);

    List<AlocacaoTarefa> findByCdProjetoAndSeqFaseAndSeqTarefa(Integer cdProjeto, Short seqFase, Short seqTarefa);
}
