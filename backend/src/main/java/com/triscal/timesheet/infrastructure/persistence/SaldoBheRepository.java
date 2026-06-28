package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

/** Consulta agregada de saldos de BHE (separada para não inflar o FuncionarioRepository). */
public interface SaldoBheRepository extends JpaRepository<Funcionario, Integer> {
    @Query("select coalesce(sum(f.saldoBhe50 + f.saldoBhe100), 0) from Funcionario f")
    BigDecimal saldoTotalEquipe();
}
