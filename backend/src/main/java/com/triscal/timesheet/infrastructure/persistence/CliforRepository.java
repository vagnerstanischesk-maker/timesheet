package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Clifor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CliforRepository extends JpaRepository<Clifor, Integer> {

    @Query("select coalesce(max(c.id), 0) from Clifor c")
    int maxId();

    /** Pesquisa por razão social, fantasia, CNPJ ou cidade (case-insensitive). */
    @Query("""
        select c from Clifor c
        where lower(c.razaoSocial) like lower(concat('%', :q, '%'))
           or lower(coalesce(c.fantasia,'')) like lower(concat('%', :q, '%'))
           or coalesce(c.cnpj,'') like concat('%', :q, '%')
           or lower(coalesce(c.cidade,'')) like lower(concat('%', :q, '%'))
        order by c.razaoSocial""")
    List<Clifor> pesquisar(@Param("q") String q);
}
