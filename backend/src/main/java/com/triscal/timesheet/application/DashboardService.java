package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.ResumoDashboardDTO;
import com.triscal.timesheet.domain.model.Projeto;
import com.triscal.timesheet.domain.service.CalculadoraExecucao;
import com.triscal.timesheet.infrastructure.persistence.ProjetoRepository;
import com.triscal.timesheet.infrastructure.persistence.SaldoBheRepository;
import com.triscal.timesheet.infrastructure.persistence.TimesheetDiaTarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Service
public class DashboardService {

    private final TimesheetDiaTarefaRepository tarefas;
    private final ProjetoRepository projetos;
    private final SaldoBheRepository saldos;

    public DashboardService(TimesheetDiaTarefaRepository tarefas, ProjetoRepository projetos,
                            SaldoBheRepository saldos) {
        this.tarefas = tarefas; this.projetos = projetos; this.saldos = saldos;
    }

    @Transactional(readOnly = true)
    public ResumoDashboardDTO resumo(int ano, int mes) {
        BigDecimal horasMes = tarefas.somaHorasMes((short) ano, (short) mes);
        BigDecimal faturavel = tarefas.somaFaturavelMes((short) ano, (short) mes);
        BigDecimal percFat = horasMes.signum() == 0 ? BigDecimal.ZERO
            : faturavel.multiply(BigDecimal.valueOf(100)).divide(horasMes, 2, RoundingMode.HALF_UP);
        BigDecimal saldoEquipe = saldos.saldoTotalEquipe();

        var lista = new ArrayList<ResumoDashboardDTO.ProjetoExecucao>();
        for (Projeto p : projetos.findAll()) {
            BigDecimal realizado = tarefas.somaHorasProjetoTotal(p.getId());
            BigDecimal perc = CalculadoraExecucao.percExecucaoHoras(realizado, p.getHorasPrevistas());
            lista.add(new ResumoDashboardDTO.ProjetoExecucao(
                p.getId(), p.getDescricao(), realizado, p.getHorasPrevistas(), perc));
        }
        return new ResumoDashboardDTO(ano, mes, horasMes, percFat, saldoEquipe, lista);
    }
}
