package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.DiaDTO;
import com.triscal.timesheet.api.dto.LancamentoTarefaDTO;
import com.triscal.timesheet.api.dto.TarefaAlocadaDTO;
import com.triscal.timesheet.domain.model.*;
import com.triscal.timesheet.domain.service.LancamentoValidator;
import com.triscal.timesheet.infrastructure.persistence.AlocacaoTarefaRepository;
import com.triscal.timesheet.infrastructure.persistence.TimesheetDiaRepository;
import com.triscal.timesheet.infrastructure.persistence.TimesheetDiaTarefaRepository;
import com.triscal.timesheet.infrastructure.persistence.ProjetoRepository;
import com.triscal.timesheet.infrastructure.persistence.ProjetoFaseRepository;
import com.triscal.timesheet.infrastructure.persistence.ProjetoTarefaRepository;
import com.triscal.timesheet.infrastructure.persistence.FeriadoRepository;
import com.triscal.timesheet.infrastructure.persistence.FuncionarioRepository;
import com.triscal.timesheet.infrastructure.persistence.ParamGeralRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LancamentoService {

    private final TimesheetDiaRepository dias;
    private final TimesheetDiaTarefaRepository tarefas;
    private final AlocacaoTarefaRepository alocacoes;
    private final ProjetoRepository projetos;
    private final ProjetoFaseRepository fases;
    private final ProjetoTarefaRepository projTarefas;
    private final FeriadoRepository feriados;
    private final FuncionarioRepository funcionarios;
    private final ParamGeralRepository parametros;

    public LancamentoService(TimesheetDiaRepository dias, TimesheetDiaTarefaRepository tarefas,
                             AlocacaoTarefaRepository alocacoes, ProjetoRepository projetos,
                             ProjetoFaseRepository fases, ProjetoTarefaRepository projTarefas,
                             FeriadoRepository feriados, FuncionarioRepository funcionarios,
                             ParamGeralRepository parametros) {
        this.dias = dias; this.tarefas = tarefas; this.alocacoes = alocacoes;
        this.projetos = projetos; this.fases = fases; this.projTarefas = projTarefas;
        this.feriados = feriados; this.funcionarios = funcionarios; this.parametros = parametros;
    }

    /** Tarefas que o colaborador pode lançar (alocação + tarefa ativas e dentro da validade na data). */
    @Transactional(readOnly = true)
    public List<TarefaAlocadaDTO> tarefasAlocadas(int funcionarioId, LocalDate data) {
        return alocacoes.tarefasDoFuncionario(funcionarioId, data == null ? LocalDate.now() : data);
    }

    @Transactional(readOnly = true)
    public DiaDTO montarDia(int func, int ano, int mes, int dia) {
        var id = new TimesheetDiaId(func, (short) ano, (short) mes, (short) dia);
        var d = dias.findById(id).orElse(null);
        var lista = new ArrayList<LancamentoTarefaDTO>();
        for (TimesheetDiaTarefa t : tarefas.doDia(func, (short) ano, (short) mes, (short) dia)) {
            var k = t.getId();
            lista.add(new LancamentoTarefaDTO(k.getCdProjeto(), k.getSeqProjetoFase(), k.getSeqProjetoTarefa(),
                descricaoTarefa(k.getCdProjeto(), k.getSeqProjetoFase(), k.getSeqProjetoTarefa()),
                t.getHorasNc(), t.getHorasCo(), t.getHorasNcExtra(), t.getHorasCoExtra(),
                false, t.getObs()));
        }
        char status = d != null ? d.getStatus() : 'A';
        BigDecimal ausBhe = d != null ? d.getAusenciaBhe() : BigDecimal.ZERO;
        BigDecimal ausAbn = d != null ? d.getAusenciaAbono() : BigDecimal.ZERO;

        // Horas normais esperadas: dia útil = jornada padrão (8h); feriado = horas a trabalhar do feriado; fim de semana = 0.
        Integer filial = funcionarios.findById(func).map(com.triscal.timesheet.domain.model.Funcionario::getCdEmpresa).orElse(null);
        LocalDate data = LocalDate.of(ano, mes, dia);
        boolean fimDeSemana = data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean feriado = feriados.contarFeriado((short) ano, (short) mes, (short) dia, filial) > 0;
        BigDecimal esperado;
        if (fimDeSemana) esperado = BigDecimal.ZERO;
        else if (feriado) {
            BigDecimal hf = feriados.horasFeriado((short) ano, (short) mes, (short) dia, filial);
            esperado = hf == null ? BigDecimal.ZERO : hf;
        } else esperado = jornadaPadrao();

        return new DiaDTO(ano, mes, dia, status, ausBhe, ausAbn, feriado, esperado, lista);
    }

    private BigDecimal jornadaPadrao(){
        return parametros.findById("JORNADA_PADRAO_HORAS")
            .map(p -> { try { return new BigDecimal(p.getValor().trim()); } catch (Exception e) { return new BigDecimal("8"); } })
            .orElse(new BigDecimal("8"));
    }

    /** Rótulo "Projeto · Fase · Tarefa" para exibição. */
    private String descricaoTarefa(Integer cdProjeto, Short seqFase, Short seqTarefa){
        String proj = projetos.findById(cdProjeto).map(com.triscal.timesheet.domain.model.Projeto::getDescricao).orElse("Projeto "+cdProjeto);
        String fase = fases.findById(new com.triscal.timesheet.domain.model.ProjetoFase.PK(cdProjeto, seqFase))
            .map(com.triscal.timesheet.domain.model.ProjetoFase::getDescricao).orElse("Fase "+seqFase);
        String tar = projTarefas.findById(new com.triscal.timesheet.domain.model.ProjetoTarefa.PK(cdProjeto, seqFase, seqTarefa))
            .map(com.triscal.timesheet.domain.model.ProjetoTarefa::getDescricao).orElse("Tarefa "+seqTarefa);
        return proj + " · " + fase + " · " + tar;
    }

    @Transactional
    public void salvarRascunho(int func, DiaDTO dia, String usuario) {
        List<String> erros = LancamentoValidator.validar(dia);
        if (!erros.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.join(" ", erros));

        var diaId = new TimesheetDiaId(func, (short) dia.ano(), (short) dia.mes(), (short) dia.dia());
        var d = dias.findById(diaId).orElseGet(() -> new TimesheetDia(diaId));
        if (d.getStatus() == 'P')
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dia aprovado não pode ser editado.");

        BigDecimal hnormal = BigDecimal.ZERO;
        for (LancamentoTarefaDTO t : dia.tarefas()) {
            if (t.somenteLeitura()) continue; // externas (OTRS) não são alteradas pelo colaborador
            // valida alocação: só lança em tarefa onde está alocado e ativo
            boolean alocado = alocacoes.existsByCdProjetoAndSeqFaseAndSeqTarefaAndCdFuncionarioAndStatus(
                t.cdProjeto(), t.seqFase(), t.seqTarefa(), func, "INI");
            if (!alocado)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não está alocado na tarefa " + t.cdProjeto() + "/" + t.seqFase() + "/" + t.seqTarefa() + ".");

            var k = new TimesheetDiaTarefaId(func, t.cdProjeto(), t.seqFase(), t.seqTarefa(),
                (short) dia.ano(), (short) dia.mes(), (short) dia.dia());
            var e = tarefas.findById(k).orElseGet(() -> new TimesheetDiaTarefa(k));
            e.setHorasNc(nz(t.horasNc())); e.setHorasCo(nz(t.horasCo()));
            e.setHorasNcExtra(nz(t.horasNcExtra())); e.setHorasCoExtra(nz(t.horasCoExtra()));
            e.setObs(t.obs()); e.setUsuario(usuario); e.setDtUltAlt(OffsetDateTime.now());
            tarefas.save(e);
            hnormal = hnormal.add(nz(t.horasNc())).add(nz(t.horasCo()));
        }

        d.setHorasNormais(hnormal);
        d.setAusenciaBhe(nz(dia.ausenciaBhe()));
        d.setAusenciaAbono(nz(dia.ausenciaAbono()));
        d.setStatus('A');
        d.setUsuario(usuario);
        d.setDtUltAlt(OffsetDateTime.now());
        dias.save(d);
    }

    @Transactional
    public void fecharDia(int func, int ano, int mes, int dia, String usuario) {
        var id = new TimesheetDiaId(func, (short) ano, (short) mes, (short) dia);
        var d = dias.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Dia sem lançamentos."));
        if (d.getStatus() == 'A' || d.getStatus() == 'R') {
            d.setStatus('F'); d.setUsuario(usuario); d.setDtUltAlt(OffsetDateTime.now());
            dias.save(d);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Só é possível fechar dias em aberto ou rejeitados.");
        }
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
