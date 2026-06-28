package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Funcionario;
import com.triscal.timesheet.api.dto.InadimplenteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    Optional<Funcionario> findByCdgUsurIgnoreCase(String cdgUsur);
    Optional<Funcionario> findByEmailIgnoreCase(String email);

    @Query("select coalesce(max(f.id), 0) from Funcionario f")
    int maxId();

    /** Funcionários ativos que possuem algum dos perfis informados (ex.: gerentes técnicos/comerciais). */
    @Query("""
        select distinct f from Funcionario f join f.perfis p
        where (f.status is null or f.status = 'AT')
          and p.descricao in :perfis
        order by f.nome""")
    List<Funcionario> porPerfis(@Param("perfis") List<String> perfis);

    @Query("""
        select f from Funcionario f
        where (f.status is null or f.status = 'AT')
          and f.email is not null
          and not exists (
            select 1 from TimesheetDia d
            where d.id.cdFuncionario = f.id and d.id.lopeNrAno = :ano
              and d.id.lopeNrMes = :mes and d.id.logNrDia = :dia)
        """)
    List<Funcionario> semLancamentoNoDia(@Param("ano") Short ano, @Param("mes") Short mes, @Param("dia") Short dia);

    @Query("""
        select distinct apr from Funcionario apr
        where exists (
            select 1 from Funcionario sub, TimesheetDia d
            where sub.aprovadorId = apr.id and d.id.cdFuncionario = sub.id and d.status = 'F')
          and apr.email is not null
        """)
    List<Funcionario> aprovadoresComPendencias();

    /**
     * Subordinados com dias em aberto ('A') anteriores a :hoje (yyyymmdd), agrupados.
     * Retorna o aprovador (para notificar) e o colaborador atrasado.
     */
    @Query("""
        select new com.triscal.timesheet.api.dto.InadimplenteDTO(
            apr.id, apr.email, apr.nome, sub.nome, count(d))
        from Funcionario apr, Funcionario sub, TimesheetDia d
        where sub.aprovadorId = apr.id
          and d.id.cdFuncionario = sub.id
          and d.status = 'A'
          and (d.id.lopeNrAno * 10000 + d.id.lopeNrMes * 100 + d.id.logNrDia) < :hoje
          and apr.email is not null
        group by apr.id, apr.email, apr.nome, sub.nome
        order by apr.nome, sub.nome""")
    List<InadimplenteDTO> subordinadosAtrasados(@Param("hoje") int hojeYyyymmdd);
}
