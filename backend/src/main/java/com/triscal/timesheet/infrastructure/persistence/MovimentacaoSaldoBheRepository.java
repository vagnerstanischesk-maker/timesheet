package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.MovimentacaoSaldoBhe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MovimentacaoSaldoBheRepository extends JpaRepository<MovimentacaoSaldoBhe, Long> {

    /** Movimentos de BHE no período [ini, fim], com o nome do colaborador. Ordenado por nome e data. */
    @Query("""
        select m.cdFuncionario, f.nome, m.data, m.valor50, m.valor100, m.manual, m.usuario
        from MovimentacaoSaldoBhe m, Funcionario f
        where m.cdFuncionario = f.id and m.data between :ini and :fim
        order by f.nome, m.data""")
    List<Object[]> movimentacoesPeriodo(@Param("ini") LocalDate ini, @Param("fim") LocalDate fim);
}
