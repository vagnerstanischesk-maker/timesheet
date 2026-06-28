package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Vertical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VerticalRepository extends JpaRepository<Vertical, Short> {
    List<Vertical> findByStatusOrderByDescricao(String status);
    List<Vertical> findAllByOrderByDescricao();

    @Query("select coalesce(max(v.id), 0) from Vertical v")
    short maxId();
}
