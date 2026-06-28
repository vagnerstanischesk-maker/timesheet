package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.*;
import com.triscal.timesheet.domain.model.*;
import com.triscal.timesheet.domain.service.Quinzena;
import com.triscal.timesheet.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuinzenaService {

    private static final String[] DIAS_SEMANA = {"seg","ter","qua","qui","sex","sáb","dom"};

    private final TimesheetDiaRepository dias;
    private final TimesheetDiaTarefaRepository tarefas;
    private final FuncionarioRepository funcionarios;
    private final FeriadoRepository feriados;
    private final ParamGeralRepository parametros;
    private final ProjetoRepository projetos;
    private final ProjetoFaseRepository fases;
    private final ProjetoTarefaRepository projTarefas;
    private final AlocacaoTarefaRepository alocacoes;

    public QuinzenaService(TimesheetDiaRepository dias, TimesheetDiaTarefaRepository tarefas,
                           FuncionarioRepository funcionarios, FeriadoRepository feriados,
                           ParamGeralRepository parametros, ProjetoRepository projetos,
                           ProjetoFaseRepository fases, ProjetoTarefaRepository projTarefas,
                           AlocacaoTarefaRepository alocacoes) {
        this.dias = dias; this.tarefas = tarefas; this.funcionarios = funcionarios;
        this.feriados = feriados; this.parametros = parametros;
        this.projetos = projetos; this.fases = fases; this.projTarefas = projTarefas;
        this.alocacoes = alocacoes;
    }

    private static final class Acc {
        Integer proj; Short fase; Short tarefa;
        BigDecimal co = BigDecimal.ZERO, nc = BigDecimal.ZERO, coEx = BigDecimal.ZERO, ncEx = BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public QuinzenaDTO montar(int funcionarioId, int anoRef, int mesRef, int periodo) {
        var func = funcionarios.findById(funcionarioId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));
        int corte1 = paramInt("QUINZENA_CORTE_1", 1);
        int corte2 = paramInt("QUINZENA_CORTE_2", 16);

        List<LocalDate> datas = Quinzena.datasDoPeriodo(anoRef, mesRef, periodo, corte1, corte2);
        int n = datas.size();
        List<QuinzenaDiaDTO> linha = new ArrayList<>();
        Map<String,Acc> porTarefa = new LinkedHashMap<>();
        Map<Integer,BigDecimal[]> gradeMap = new LinkedHashMap<>();   // cdProjeto -> horas por dia

        for (int i = 0; i < n; i++) {
            LocalDate data = datas.get(i);
            short ano = (short) data.getYear(), mes = (short) data.getMonthValue(), dia = (short) data.getDayOfMonth();
            var d = dias.findById(new TimesheetDiaId(funcionarioId, ano, mes, dia)).orElse(null);
            BigDecimal total = BigDecimal.ZERO;
            for (TimesheetDiaTarefa t : tarefas.doDia(funcionarioId, ano, mes, dia)) {
                BigDecimal h = t.getHorasNc().add(t.getHorasCo()).add(t.getHorasNcExtra()).add(t.getHorasCoExtra());
                total = total.add(h);
                var k = t.getId();
                BigDecimal[] arr = gradeMap.computeIfAbsent(k.getCdProjeto(), x -> novoArray(n));
                arr[i] = arr[i].add(h);
                String key = k.getCdProjeto()+"-"+k.getSeqProjetoFase()+"-"+k.getSeqProjetoTarefa();
                Acc acc = porTarefa.computeIfAbsent(key, x -> {
                    Acc a = new Acc(); a.proj = k.getCdProjeto(); a.fase = k.getSeqProjetoFase(); a.tarefa = k.getSeqProjetoTarefa(); return a;
                });
                acc.co = acc.co.add(t.getHorasCo()); acc.nc = acc.nc.add(t.getHorasNc());
                acc.coEx = acc.coEx.add(t.getHorasCoExtra()); acc.ncEx = acc.ncEx.add(t.getHorasNcExtra());
            }
            if (d != null) total = total.add(d.getAusenciaBhe()).add(d.getAusenciaAbono());
            boolean feriado = feriados.contarFeriado(ano, mes, dia, func.getCdEmpresa()) > 0;
            char status = d != null ? d.getStatus() : 'A';
            linha.add(new QuinzenaDiaDTO(data.getYear(), data.getMonthValue(), data.getDayOfMonth(),
                DIAS_SEMANA[data.getDayOfWeek().getValue() - 1], feriado, status, total));
        }

        List<GradeProjetoDTO> grade = new ArrayList<>();
        for (Map.Entry<Integer,BigDecimal[]> e : gradeMap.entrySet()) {
            BigDecimal[] arr = e.getValue();
            BigDecimal tot = BigDecimal.ZERO;
            for (BigDecimal v : arr) tot = tot.add(v);
            grade.add(new GradeProjetoDTO(e.getKey(), descProjeto(e.getKey()), Arrays.asList(arr), tot));
        }

        List<QuinzenaTarefaDTO> detalhe = new ArrayList<>();
        for (Acc a : porTarefa.values()) {
            BigDecimal tot = a.co.add(a.nc).add(a.coEx).add(a.ncEx);
            detalhe.add(new QuinzenaTarefaDTO(a.proj, descProjeto(a.proj), a.fase, descFase(a.proj, a.fase),
                a.tarefa, descTarefa(a.proj, a.fase, a.tarefa), a.co, a.nc, a.coEx, a.ncEx, tot));
        }

        return new QuinzenaDTO(func.getNome(), anoRef, mesRef, periodo, func.getSaldoBhe50(), func.getSaldoBhe100(), linha, grade, detalhe);
    }

    private static BigDecimal[] novoArray(int n){ BigDecimal[] a = new BigDecimal[n]; Arrays.fill(a, BigDecimal.ZERO); return a; }
    private String descProjeto(Integer p){ return projetos.findById(p).map(Projeto::getDescricao).orElse("Projeto "+p); }
    private String descFase(Integer p, Short f){ return fases.findById(new ProjetoFase.PK(p, f)).map(ProjetoFase::getDescricao).orElse("Fase "+f); }
    private String descTarefa(Integer p, Short f, Short t){ return projTarefas.findById(new ProjetoTarefa.PK(p, f, t)).map(ProjetoTarefa::getDescricao).orElse("Tarefa "+t); }


    /** Visão de aprovação (sem semear tarefas do período anterior). */
    @Transactional(readOnly = true)
    public GradeEdicaoDTO gradeEdicao(int funcionarioId, int anoRef, int mesRef, int periodo) {
        return gradeEdicao(funcionarioId, anoRef, mesRef, periodo, false);
    }

    /**
     * Grade editável do período. Se {@code seedAnterior} e o período não tiver lançamentos,
     * semeia linhas (vazias) com as tarefas do período anterior — apenas as ainda alocadas,
     * não externas e dentro da validade — para facilitar o colaborador.
     */
    @Transactional(readOnly = true)
    public GradeEdicaoDTO gradeEdicao(int funcionarioId, int anoRef, int mesRef, int periodo, boolean seedAnterior) {
        var func = funcionarios.findById(funcionarioId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));
        int corte1 = paramInt("QUINZENA_CORTE_1", 1);
        int corte2 = paramInt("QUINZENA_CORTE_2", 16);
        List<LocalDate> datas = Quinzena.datasDoPeriodo(anoRef, mesRef, periodo, corte1, corte2);
        int n = datas.size();

        List<DiaMetaDTO> diasMeta = new ArrayList<>();
        Map<String, CelulaDTO[]> linhasMap = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            LocalDate data = datas.get(i);
            short ano = (short) data.getYear(), mes = (short) data.getMonthValue(), dia = (short) data.getDayOfMonth();
            var d = dias.findById(new TimesheetDiaId(funcionarioId, ano, mes, dia)).orElse(null);
            boolean fimDeSemana = data.getDayOfWeek().getValue() >= 6;
            boolean feriado = feriados.contarFeriado(ano, mes, dia, func.getCdEmpresa()) > 0;
            BigDecimal esperado;
            if (fimDeSemana) esperado = BigDecimal.ZERO;
            else if (feriado) { BigDecimal hf = feriados.horasFeriado(ano, mes, dia, func.getCdEmpresa()); esperado = hf == null ? BigDecimal.ZERO : hf; }
            else esperado = jornadaPadrao();
            char status = d != null ? d.getStatus() : 'A';
            BigDecimal ausBhe = d != null ? d.getAusenciaBhe() : BigDecimal.ZERO;
            BigDecimal ausAbn = d != null ? d.getAusenciaAbono() : BigDecimal.ZERO;
            diasMeta.add(new DiaMetaDTO(data.getYear(), data.getMonthValue(), data.getDayOfMonth(),
                DIAS_SEMANA[data.getDayOfWeek().getValue() - 1], feriado, status, esperado, ausBhe, ausAbn));
            for (TimesheetDiaTarefa t : tarefas.doDia(funcionarioId, ano, mes, dia)) {
                var k = t.getId();
                String key = k.getCdProjeto() + "_" + k.getSeqProjetoFase() + "_" + k.getSeqProjetoTarefa();
                CelulaDTO[] arr = linhasMap.computeIfAbsent(key, x -> novaCelArray(n));
                arr[i] = new CelulaDTO(t.getHorasNc(), t.getHorasCo(), t.getHorasNcExtra(), t.getHorasCoExtra());
            }
        }

        // Novo período sem lançamentos: semeia as tarefas do período anterior (linhas vazias).
        if (seedAnterior && linhasMap.isEmpty()) {
            int[] ant = Quinzena.periodoAnterior(anoRef, mesRef, periodo);
            LocalDate ref = datas.get(0);
            java.util.LinkedHashSet<String> chaves = new java.util.LinkedHashSet<>();
            for (LocalDate da : Quinzena.datasDoPeriodo(ant[0], ant[1], ant[2], corte1, corte2)) {
                for (TimesheetDiaTarefa t : tarefas.doDia(funcionarioId,
                        (short) da.getYear(), (short) da.getMonthValue(), (short) da.getDayOfMonth())) {
                    var k = t.getId();
                    chaves.add(k.getCdProjeto() + "_" + k.getSeqProjetoFase() + "_" + k.getSeqProjetoTarefa());
                }
            }
            for (String key : chaves) {
                String[] p = key.split("_");
                Integer cp = Integer.valueOf(p[0]); Short sf = Short.valueOf(p[1]); Short st = Short.valueOf(p[2]);
                var pt = projTarefas.findById(new ProjetoTarefa.PK(cp, sf, st)).orElse(null);
                if (pt == null || pt.isExterna()) continue;                              // externas não são lançadas pelo colaborador
                if (pt.getDataInicio() != null && ref.isBefore(pt.getDataInicio())) continue;
                if (pt.getDataFim() != null && ref.isAfter(pt.getDataFim())) continue;   // fora da validade
                if (!alocacoes.existsByCdProjetoAndSeqFaseAndSeqTarefaAndCdFuncionarioAndStatus(cp, sf, st, funcionarioId, "INI"))
                    continue;                                                            // não mais alocado
                linhasMap.put(key, novaCelArray(n));
            }
        }

        List<TarefaLinhaDTO> linhas = new ArrayList<>();
        for (Map.Entry<String, CelulaDTO[]> e : linhasMap.entrySet()) {
            String[] p = e.getKey().split("_");
            Integer cp = Integer.valueOf(p[0]); Short sf = Short.valueOf(p[1]); Short st = Short.valueOf(p[2]);
            boolean ext = projTarefas.findById(new ProjetoTarefa.PK(cp, sf, st)).map(ProjetoTarefa::isExterna).orElse(false);
            linhas.add(new TarefaLinhaDTO(cp, descProjeto(cp), sf, descFase(cp, sf), st, descTarefa(cp, sf, st), ext,
                Arrays.asList(e.getValue())));
        }
        return new GradeEdicaoDTO(func.getNome(), anoRef, mesRef, periodo, func.getSaldoBhe50(), func.getSaldoBhe100(), diasMeta, linhas);
    }

    private static CelulaDTO[] novaCelArray(int n){
        CelulaDTO[] a = new CelulaDTO[n];
        for (int i = 0; i < n; i++) a[i] = new CelulaDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        return a;
    }
    private BigDecimal jornadaPadrao(){
        return parametros.findById("JORNADA_PADRAO_HORAS")
            .map(p -> { try { return new BigDecimal(p.getValor().trim()); } catch (Exception e) { return new BigDecimal("8"); } })
            .orElse(new BigDecimal("8"));
    }

    /** Fecha a quinzena: cada dia 'A' (ou 'R') com lançamentos passa a 'F'. */
    @Transactional
    public int fecharQuinzena(int funcionarioId, int anoRef, int mesRef, int periodo, String usuario) {
        int corte1 = paramInt("QUINZENA_CORTE_1", 1);
        int corte2 = paramInt("QUINZENA_CORTE_2", 16);
        int fechados = 0;
        for (LocalDate data : Quinzena.datasDoPeriodo(anoRef, mesRef, periodo, corte1, corte2)) {
            var id = new TimesheetDiaId(funcionarioId,
                (short) data.getYear(), (short) data.getMonthValue(), (short) data.getDayOfMonth());
            var d = dias.findById(id).orElse(null);
            if (d != null && (d.getStatus() == 'A' || d.getStatus() == 'R')) {
                d.setStatus('F'); d.setUsuario(usuario); d.setDtUltAlt(OffsetDateTime.now());
                dias.save(d); fechados++;
            }
        }
        return fechados;
    }

    private int paramInt(String nome, int padrao) {
        return parametros.findById(nome)
            .map(p -> { try { return Integer.parseInt(p.getValor().trim()); } catch (Exception e) { return padrao; } })
            .orElse(padrao);
    }
}
