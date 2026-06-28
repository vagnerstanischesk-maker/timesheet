package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.DiaStatusDTO;
import com.triscal.timesheet.api.dto.RelatorioAcompanhamentoDTO;
import com.triscal.timesheet.api.dto.RelatorioBheDTO;
import com.triscal.timesheet.api.dto.RelatorioLogDTO;
import com.triscal.timesheet.domain.service.CalculadoraExecucao;
import com.triscal.timesheet.domain.service.Quinzena;
import com.triscal.timesheet.infrastructure.persistence.AndamentoProjetoHistRepository;
import com.triscal.timesheet.infrastructure.persistence.CliforRepository;
import com.triscal.timesheet.infrastructure.persistence.MovimentacaoSaldoBheRepository;
import com.triscal.timesheet.infrastructure.persistence.ParamGeralRepository;
import com.triscal.timesheet.infrastructure.persistence.ProjetoRepository;
import com.triscal.timesheet.infrastructure.persistence.TimesheetDiaRepository;
import com.triscal.timesheet.infrastructure.persistence.TimesheetDiaTarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final TimesheetDiaRepository dias;
    private final ParamGeralRepository parametros;
    private final ProjetoRepository projetos;
    private final CliforRepository clientes;
    private final TimesheetDiaTarefaRepository tarefas;
    private final AndamentoProjetoHistRepository andamentos;
    private final MovimentacaoSaldoBheRepository movimentos;

    public RelatorioService(TimesheetDiaRepository d, ParamGeralRepository p,
                            ProjetoRepository projetos, CliforRepository clientes,
                            TimesheetDiaTarefaRepository tarefas, AndamentoProjetoHistRepository andamentos,
                            MovimentacaoSaldoBheRepository movimentos){
        this.dias=d; this.parametros=p; this.projetos=projetos; this.clientes=clientes;
        this.tarefas=tarefas; this.andamentos=andamentos; this.movimentos=movimentos;
    }

    /** Movimentação de BHE (créditos por aprovação e lançamentos manuais) no período. */
    @Transactional(readOnly = true)
    public List<RelatorioBheDTO> movimentacoesBhe(LocalDate inicio, LocalDate fim) {
        List<RelatorioBheDTO> out = new ArrayList<>();
        for (Object[] r : movimentos.movimentacoesPeriodo(inicio, fim)) {
            Character manual = (Character) r[5];
            String origem = (manual != null && manual == 'S') ? "Manual" : "Aprovação";
            out.add(new RelatorioBheDTO(
                (Integer) r[0], (String) r[1],
                r[2] == null ? null : r[2].toString(),
                (BigDecimal) r[3], (BigDecimal) r[4], origem, (String) r[6]));
        }
        return out;
    }

    /**
     * Relatório de acompanhamento — visão consolidada de TODOS os projetos somando todos os
     * períodos de log. Lista cliente, projeto, horas vendidas (previstas), horas executadas,
     * % por horas (executadas/vendidas), % informado pelo gestor e a diferença previsto × realizado (h).
     * Ordenado por cliente e por projeto.
     */
    @Transactional(readOnly = true)
    public List<RelatorioAcompanhamentoDTO> acompanhamentoProjetos() {
        Map<Integer,String> nomeCliente = clientes.findAll().stream()
            .collect(Collectors.toMap(c -> c.getId(), c -> c.getRazaoSocial()));
        Map<Integer,BigDecimal> executadas = new HashMap<>();
        for (Object[] r : tarefas.somaHorasPorProjeto())
            executadas.put((Integer) r[0], (BigDecimal) r[1]);
        Map<Integer,BigDecimal> informado = new HashMap<>();
        for (Object[] r : andamentos.ultimoPercInformadoPorProjeto())
            informado.put((Integer) r[0], (BigDecimal) r[1]);

        List<RelatorioAcompanhamentoDTO> out = new ArrayList<>();
        for (var p : projetos.findAll()) {
            BigDecimal vendidas = nz(p.getHorasPrevistas());
            BigDecimal exec = executadas.getOrDefault(p.getId(), BigDecimal.ZERO);
            BigDecimal percHoras = CalculadoraExecucao.percExecucaoHoras(exec, p.getHorasPrevistas());
            BigDecimal perc = informado.getOrDefault(p.getId(), BigDecimal.ZERO);
            // diferença % = % por horas − % informado pelo gestor; diferença em horas = diferença % × horas vendidas
            BigDecimal percDif = percHoras.subtract(perc).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal diffHoras = percDif.multiply(vendidas)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            String nome = p.getInterno() == 'S' ? "Projetos internos"
                : (p.getCdClifor() == null ? null : nomeCliente.get(p.getCdClifor()));
            out.add(new RelatorioAcompanhamentoDTO(p.getId(), p.getDescricao(),
                p.getCdClifor(), nome, vendidas, exec, percHoras, perc, percDif, diffHoras));
        }
        out.sort(Comparator.comparing((RelatorioAcompanhamentoDTO r) -> r.nomeCliente() == null ? "~" : r.nomeCliente(),
                String.CASE_INSENSITIVE_ORDER)
            .thenComparing(r -> r.descricao() == null ? "" : r.descricao(), String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    private static BigDecimal nz(BigDecimal v){ return v == null ? BigDecimal.ZERO : v; }

    private static final class Agg { Integer func; String nome; int anoRef, mesRef, periodo; char status = 'P'; }
    // Prioridade do status do período (mais "em aberto" primeiro): A > R > P
    private static int prio(char s){ return switch (s) { case 'A' -> 3; case 'R' -> 2; default -> 1; }; }
    private static String label(char s){ return switch (s) { case 'A' -> "Aberto"; case 'R' -> "Rejeitado"; case 'P' -> "Aprovado"; default -> String.valueOf(s); }; }

    /** Períodos de log (colaborador × quinzena) com status diferente de 'Fechado', agrupados/ordenados por colaborador e período. */
    @Transactional(readOnly = true)
    public List<RelatorioLogDTO> logsNaoFechados() {
        int corte1 = paramInt("QUINZENA_CORTE_1", 1);
        int corte2 = paramInt("QUINZENA_CORTE_2", 16);
        int[] atual = Quinzena.referenciaDe(LocalDate.now(), corte1, corte2);
        long chaveAtual = chave(atual[0], atual[1], atual[2]);
        Map<String, Agg> mapa = new LinkedHashMap<>();
        for (DiaStatusDTO d : dias.diasNaoFechados()) {
            int[] ref = Quinzena.referenciaDe(LocalDate.of(d.ano(), d.mes(), d.dia()), corte1, corte2);
            String key = d.cdFuncionario() + "-" + ref[0] + "-" + ref[1] + "-" + ref[2];
            Agg a = mapa.computeIfAbsent(key, x -> {
                Agg n = new Agg(); n.func = d.cdFuncionario(); n.nome = d.nome();
                n.anoRef = ref[0]; n.mesRef = ref[1]; n.periodo = ref[2]; n.status = 'P'; return n;
            });
            if (prio(d.status()) > prio(a.status)) a.status = d.status();
        }
        List<RelatorioLogDTO> out = new ArrayList<>();
        for (Agg a : mapa.values()) {
            if (chave(a.anoRef, a.mesRef, a.periodo) >= chaveAtual) continue; // ignora a quinzena atual (e futuras)
            out.add(new RelatorioLogDTO(a.func, a.nome, a.anoRef, a.mesRef, a.periodo, label(a.status)));
        }
        out.sort(Comparator.comparing(RelatorioLogDTO::nome, String.CASE_INSENSITIVE_ORDER)
            .thenComparingInt(RelatorioLogDTO::anoRef).thenComparingInt(RelatorioLogDTO::mesRef).thenComparingInt(RelatorioLogDTO::periodo));
        return out;
    }

    private static long chave(int ano, int mes, int periodo){ return ((long) ano * 100 + mes) * 10 + periodo; }

    private int paramInt(String nome, int padrao) {
        return parametros.findById(nome)
            .map(p -> { try { return Integer.parseInt(p.getValor().trim()); } catch (Exception e) { return padrao; } })
            .orElse(padrao);
    }
}
