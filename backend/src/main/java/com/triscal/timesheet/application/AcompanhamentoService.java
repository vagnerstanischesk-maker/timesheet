package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.AcompanhamentoDTO;
import com.triscal.timesheet.domain.model.*;
import com.triscal.timesheet.domain.service.CalculadoraExecucao;
import com.triscal.timesheet.domain.service.Quinzena;
import com.triscal.timesheet.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AcompanhamentoService {

    private final ProjetoRepository projetos;
    private final TimesheetDiaTarefaRepository tarefas;
    private final AndamentoProjetoHistRepository andamentos;
    private final FechamentoQuinzenalRepository fechamentos;
    private final ParamGeralRepository parametros;

    public AcompanhamentoService(ProjetoRepository projetos, TimesheetDiaTarefaRepository tarefas,
                                 AndamentoProjetoHistRepository andamentos,
                                 FechamentoQuinzenalRepository fechamentos, ParamGeralRepository parametros) {
        this.projetos = projetos; this.tarefas = tarefas; this.andamentos = andamentos;
        this.fechamentos = fechamentos; this.parametros = parametros;
    }

    @Transactional(readOnly = true)
    public AcompanhamentoDTO montar(int cdProjeto, int anoRef, int mesRef, int periodo) {
        var proj = projetos.findById(cdProjeto).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado."));
        BigDecimal periodoHoras = horasDoPeriodo(cdProjeto, anoRef, mesRef, periodo);
        BigDecimal acumulado = tarefas.somaHorasProjetoTotal(cdProjeto);
        BigDecimal percHoras = CalculadoraExecucao.percExecucaoHoras(acumulado, proj.getHorasPrevistas());

        var hist = new ArrayList<AcompanhamentoDTO.PontoHistorico>();
        BigDecimal ultimoInformado = BigDecimal.ZERO;
        for (AndamentoProjetoHist h : andamentos.findByCdProjetoOrderByDtReferenciaAsc(cdProjeto)) {
            hist.add(new AcompanhamentoDTO.PontoHistorico(
                h.getDtReferencia().toString(), h.getPercInformado(), h.getPercDerivadoHoras()));
            ultimoInformado = h.getPercInformado();
        }
        return new AcompanhamentoDTO(proj.getId(), proj.getDescricao(),
            periodoHoras, acumulado, proj.getHorasPrevistas(), percHoras, ultimoInformado, hist);
    }

    @Transactional
    public void informarAndamento(int cdProjeto, Short seqFase, Short seqTarefa,
                                  BigDecimal percInformado, String usuario) {
        var proj = projetos.findById(cdProjeto).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado."));
        validarPercentual(percInformado);
        BigDecimal acumulado = tarefas.somaHorasProjetoTotal(cdProjeto);
        BigDecimal percHoras = CalculadoraExecucao.percExecucaoHoras(acumulado, proj.getHorasPrevistas());
        andamentos.save(new AndamentoProjetoHist(cdProjeto, seqFase, seqTarefa,
            LocalDate.now(), percInformado, percHoras, usuario));
    }

    @Transactional
    public void fecharQuinzena(int cdProjeto, int anoRef, int mesRef, int periodo,
                               BigDecimal percInformado, String usuario) {
        var proj = projetos.findById(cdProjeto).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado."));
        validarPercentual(percInformado);
        if (fechamentos.existsByCdProjetoAndAnoAndMesAndPeriodo(cdProjeto,
                (short) anoRef, (short) mesRef, (short) periodo))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Quinzena já fechada para este projeto.");

        BigDecimal exec = horasDoPeriodo(cdProjeto, anoRef, mesRef, periodo);
        BigDecimal acumulado = tarefas.somaHorasProjetoTotal(cdProjeto);
        BigDecimal percHoras = CalculadoraExecucao.percExecucaoHoras(acumulado, proj.getHorasPrevistas());
        BigDecimal desvio = CalculadoraExecucao.desvio(percInformado, percHoras);

        fechamentos.save(new FechamentoQuinzenal(cdProjeto, (short) anoRef, (short) mesRef, (short) periodo,
            exec, proj.getHorasPrevistas(), percHoras, percInformado, desvio, usuario));
        andamentos.save(new AndamentoProjetoHist(cdProjeto, null, null,
            LocalDate.now(), percInformado, percHoras, usuario));
    }

    private BigDecimal horasDoPeriodo(int cdProjeto, int anoRef, int mesRef, int periodo) {
        int c1 = paramInt("QUINZENA_CORTE_1", 1), c2 = paramInt("QUINZENA_CORTE_2", 16);
        BigDecimal soma = BigDecimal.ZERO;
        for (LocalDate d : Quinzena.datasDoPeriodo(anoRef, mesRef, periodo, c1, c2))
            soma = soma.add(tarefas.somaHorasProjetoDia(cdProjeto,
                (short) d.getYear(), (short) d.getMonthValue(), (short) d.getDayOfMonth()));
        return soma;
    }

    private void validarPercentual(BigDecimal p) {
        if (p == null || p.signum() < 0 || p.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "% deve estar entre 0 e 100.");
    }

    private int paramInt(String nome, int padrao) {
        return parametros.findById(nome)
            .map(p -> { try { return Integer.parseInt(p.getValor().trim()); } catch (Exception e) { return padrao; } })
            .orElse(padrao);
    }
}
