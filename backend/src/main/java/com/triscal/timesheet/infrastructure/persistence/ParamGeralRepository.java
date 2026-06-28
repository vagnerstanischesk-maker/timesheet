package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.ParamGeral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParamGeralRepository extends JpaRepository<ParamGeral, String> {}
