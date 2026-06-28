package com.triscal.timesheet.infrastructure.persistence;
import com.triscal.timesheet.domain.model.ProjetoTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProjetoTarefaRepository extends JpaRepository<ProjetoTarefa, ProjetoTarefa.PK> {
    List<ProjetoTarefa> findByCdProjetoAndSeqFaseOrderBySeqTarefa(Integer cdProjeto, Short seqFase);
}
