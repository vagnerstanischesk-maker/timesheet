package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Short> {}
