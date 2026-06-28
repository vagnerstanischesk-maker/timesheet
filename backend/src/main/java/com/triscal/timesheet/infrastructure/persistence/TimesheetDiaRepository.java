package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.TimesheetDia;
import com.triscal.timesheet.domain.model.TimesheetDiaId;
import com.triscal.timesheet.api.dto.PendenteDTO;
import com.triscal.timesheet.api.dto.DiaStatusDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimesheetDiaRepository extends JpaRepository<TimesheetDia, TimesheetDiaId> {

    /** Dias 'F' (pendentes de aprovação) dos subordinados do aprovador informado. */
    @Query("""
        select new com.triscal.timesheet.api.dto.PendenteDTO(
            f.id, f.nome, d.id.lopeNrAno, d.id.lopeNrMes, d.id.logNrDia)
        from TimesheetDia d, Funcionario f
        where d.id.cdFuncionario = f.id
          and f.aprovadorId = :aprovador
          and d.status = 'F'
        order by f.nome, d.id.lopeNrAno, d.id.lopeNrMes, d.id.logNrDia""")
    List<PendenteDTO> pendentesDoAprovador(@Param("aprovador") Integer aprovador);

    /** Dias pendentes (status Aberto 'A' ou Rejeitado 'R'); exclui Fechado 'F' e Aprovado 'P'. */
    @Query("""
        select new com.triscal.timesheet.api.dto.DiaStatusDTO(
            f.id, f.nome, d.id.lopeNrAno, d.id.lopeNrMes, d.id.logNrDia, d.status)
        from TimesheetDia d, Funcionario f
        where d.id.cdFuncionario = f.id and d.status <> 'F' and d.status <> 'P'""")
    List<DiaStatusDTO> diasNaoFechados();
}
