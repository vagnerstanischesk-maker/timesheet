package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.AndamentoProjetoHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AndamentoProjetoHistRepository extends JpaRepository<AndamentoProjetoHist, Long> {
    List<AndamentoProjetoHist> findByCdProjetoOrderByDtReferenciaAsc(Integer cdProjeto);

    /** Último % informado pelo gestor por projeto (registro mais recente). [cdProjeto, percInformado]. */
    @Query("""
        select a.cdProjeto, a.percInformado from AndamentoProjetoHist a
        where a.id = (select max(b.id) from AndamentoProjetoHist b where b.cdProjeto = a.cdProjeto)""")
    List<Object[]> ultimoPercInformadoPorProjeto();
}
