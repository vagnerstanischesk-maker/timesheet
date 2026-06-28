package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.TimesheetDiaTarefa;
import com.triscal.timesheet.domain.model.TimesheetDiaTarefaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TimesheetDiaTarefaRepository extends JpaRepository<TimesheetDiaTarefa, TimesheetDiaTarefaId> {

    @Query("""
        select t from TimesheetDiaTarefa t
        where t.id.cdFuncionario = :func and t.id.lopeNrAno = :ano
          and t.id.lopeNrMes = :mes and t.id.logNrDia = :dia""")
    List<TimesheetDiaTarefa> doDia(@Param("func") Integer func, @Param("ano") Short ano,
                                   @Param("mes") Short mes, @Param("dia") Short dia);

    @Query("""
        select coalesce(sum(t.horasNc + t.horasCo + t.horasNcExtra + t.horasCoExtra), 0)
        from TimesheetDiaTarefa t
        where t.id.cdProjeto = :proj and t.id.lopeNrAno = :ano
          and t.id.lopeNrMes = :mes and t.id.logNrDia = :dia""")
    BigDecimal somaHorasProjetoDia(@Param("proj") Integer proj, @Param("ano") Short ano,
                                   @Param("mes") Short mes, @Param("dia") Short dia);

    @Query("""
        select coalesce(sum(t.horasNc + t.horasCo + t.horasNcExtra + t.horasCoExtra), 0)
        from TimesheetDiaTarefa t where t.id.cdProjeto = :proj""")
    BigDecimal somaHorasProjetoTotal(@Param("proj") Integer proj);

    /** Horas executadas (todos os períodos) agrupadas por projeto. [cdProjeto, horas]. */
    @Query("""
        select t.id.cdProjeto, coalesce(sum(t.horasNc + t.horasCo + t.horasNcExtra + t.horasCoExtra), 0)
        from TimesheetDiaTarefa t group by t.id.cdProjeto""")
    List<Object[]> somaHorasPorProjeto();

    @Query("""
        select coalesce(sum(t.horasNc + t.horasCo + t.horasNcExtra + t.horasCoExtra), 0)
        from TimesheetDiaTarefa t where t.id.lopeNrAno = :ano and t.id.lopeNrMes = :mes""")
    BigDecimal somaHorasMes(@Param("ano") Short ano, @Param("mes") Short mes);

    @Query("""
        select coalesce(sum(t.horasCo + t.horasCoExtra), 0)
        from TimesheetDiaTarefa t where t.id.lopeNrAno = :ano and t.id.lopeNrMes = :mes""")
    BigDecimal somaFaturavelMes(@Param("ano") Short ano, @Param("mes") Short mes);

    /**
     * Custo realizado do projeto: soma, por lançamento, das horas multiplicadas pela
     * taxa horária do recurso na alocação (normal x extra). Considera todos os recursos
     * com horas lançadas no projeto. Query nativa (junção pela chave da alocação).
     */
    @Query(value = """
        SELECT COALESCE(SUM(
            (t.horas_nr_nc + t.horas_nr_co) * COALESCE(a.taxa_horaria_normal, 0)
          + (t.horas_nr_ncextra + t.horas_nr_coextra) * COALESCE(a.taxa_horaria_extra, 0)
        ), 0)
        FROM timesheet_dia_tarefa t
        JOIN projeto_tarefa_funcionario a
          ON a.cd_projeto = t.cd_projeto
         AND a.seq_projeto_fase = t.seq_projeto_fase
         AND a.seq_projeto_tarefa = t.seq_projeto_tarefa
         AND a.cd_funcionario = t.cd_funcionario
        WHERE t.cd_projeto = :proj""", nativeQuery = true)
    BigDecimal custoRealizadoProjeto(@Param("proj") Integer proj);
}
