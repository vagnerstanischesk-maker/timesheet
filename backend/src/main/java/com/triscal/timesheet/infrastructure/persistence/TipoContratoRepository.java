package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.TipoContrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoContratoRepository extends JpaRepository<TipoContrato, Short> {
    List<TipoContrato> findByStatusOrderByDescricao(String status);
}
