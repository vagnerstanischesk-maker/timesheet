package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {

    @Query("select coalesce(max(p.id), 0) from Projeto p")
    int maxId();

    /** Pesquisa por descrição/código (q) e/ou por cliente (cli). Parâmetros nulos são ignorados. */
    @Query("""
        select p from Projeto p
        where (:q is null or :q = ''
               or lower(p.descricao) like lower(concat('%', :q, '%'))
               or cast(p.id as string) like concat('%', :q, '%'))
          and (:cli is null or p.cdClifor = :cli)
        order by p.descricao""")
    List<Projeto> pesquisar(@Param("q") String q, @Param("cli") Integer cli);
}
