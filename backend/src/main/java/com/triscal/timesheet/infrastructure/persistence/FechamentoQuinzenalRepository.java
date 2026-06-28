package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.FechamentoQuinzenal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FechamentoQuinzenalRepository extends JpaRepository<FechamentoQuinzenal, Long> {
    boolean existsByCdProjetoAndAnoAndMesAndPeriodo(Integer cdProjeto, Short ano, Short mes, Short periodo);
}
