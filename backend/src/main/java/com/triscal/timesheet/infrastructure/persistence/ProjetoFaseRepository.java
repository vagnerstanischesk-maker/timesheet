package com.triscal.timesheet.infrastructure.persistence;
import com.triscal.timesheet.domain.model.ProjetoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProjetoFaseRepository extends JpaRepository<ProjetoFase, ProjetoFase.PK> {
    List<ProjetoFase> findByCdProjetoOrderBySeqFase(Integer cdProjeto);
}
