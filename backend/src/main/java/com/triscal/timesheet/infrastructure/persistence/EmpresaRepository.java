package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    List<Empresa> findAllByOrderBySigla();
    // Apenas filiais selecionáveis na tela (RJ, SP). As demais existem só para compatibilidade.
    List<Empresa> findBySelecionavelOrderBySigla(String selecionavel);
}
