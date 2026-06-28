package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.*;
import com.triscal.timesheet.domain.model.*;
import com.triscal.timesheet.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EstruturaProjetoService {
    private static final Set<String> STATUS = Set.of("INI","ENC","NIN");
    private final ProjetoRepository projetos;
    private final ProjetoFaseRepository fases;
    private final ProjetoTarefaRepository tarefas;
    private final AlocacaoTarefaRepository alocacoes;
    private final FuncionarioRepository funcionarios;

    public EstruturaProjetoService(ProjetoRepository projetos, ProjetoFaseRepository fases, ProjetoTarefaRepository tarefas,
                                   AlocacaoTarefaRepository alocacoes, FuncionarioRepository funcionarios) {
        this.projetos=projetos; this.fases=fases; this.tarefas=tarefas; this.alocacoes=alocacoes; this.funcionarios=funcionarios;
    }

    // ----- Fases -----
    @Transactional(readOnly=true)
    public List<FaseDTO> listarFases(int cdProjeto){
        return fases.findByCdProjetoOrderBySeqFase(cdProjeto).stream().map(this::faseDTO).toList();
    }
    @Transactional
    public FaseDTO salvarFase(int cdProjeto, FaseDTO d){
        if (d.descricao()==null||d.descricao().isBlank()) throw erro("Descrição da fase é obrigatória.");
        short seq = d.seqFase()!=null ? d.seqFase() : proximaFase(cdProjeto);
        var f = fases.findById(new ProjetoFase.PK(cdProjeto, seq)).orElseGet(() -> new ProjetoFase(cdProjeto, seq));
        f.setDescricao(d.descricao()); f.setStatus(status(d.status()));
        f.setDataInicio(data(d.dataInicio())); f.setDataFim(data(d.dataFim()));
        f.setHorasPrevistas(d.horasPrevistas()); f.setHorasRealizadas(d.horasRealizadas()); f.setPercConclusao(d.percConclusao());
        f.setValorPrevisto(d.valorPrevisto()); f.setValorRealizado(d.valorRealizado());
        f.setCustoPrevisto(d.custoPrevisto()); f.setCustoRealizado(d.custoRealizado());
        f.setDtUltAlt(OffsetDateTime.now());
        var salvo = fases.save(f);
        atualizarTotaisProjeto(cdProjeto);
        return faseDTO(salvo);
    }
    private short proximaFase(int cdProjeto){
        return (short)(fases.findByCdProjetoOrderBySeqFase(cdProjeto).stream().mapToInt(ProjetoFase::getSeqFase).max().orElse(0)+1);
    }
    private FaseDTO faseDTO(ProjetoFase f){
        return new FaseDTO(f.getSeqFase(), f.getDescricao(), f.getStatus(), str(f.getDataInicio()), str(f.getDataFim()),
            f.getHorasPrevistas(), f.getHorasRealizadas(), f.getPercConclusao(),
            f.getValorPrevisto(), f.getValorRealizado(), f.getCustoPrevisto(), f.getCustoRealizado());
    }

    /** Recalcula os totais do projeto como soma das fases (mantém o cadastro só-leitura coerente). */
    private void atualizarTotaisProjeto(int cdProjeto){
        var lista = fases.findByCdProjetoOrderBySeqFase(cdProjeto);
        var p = projetos.findById(cdProjeto).orElse(null);
        if (p == null) return;
        p.setHorasPrevistas(soma(lista, ProjetoFase::getHorasPrevistas));
        p.setHorasRealizadas(soma(lista, ProjetoFase::getHorasRealizadas));
        p.setValorPrevisto(soma(lista, ProjetoFase::getValorPrevisto));
        p.setValorRealizado(soma(lista, ProjetoFase::getValorRealizado));
        p.setCustoPrevisto(soma(lista, ProjetoFase::getCustoPrevisto));
        p.setCustoRealizado(soma(lista, ProjetoFase::getCustoRealizado));
        p.setDtUltAlt(OffsetDateTime.now());
        projetos.save(p);
    }
    private static BigDecimal soma(List<ProjetoFase> l, java.util.function.Function<ProjetoFase,BigDecimal> g){
        return l.stream().map(g).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ----- Tarefas -----
    @Transactional(readOnly=true)
    public List<TarefaCadastroDTO> listarTarefas(int cdProjeto, short seqFase){
        return tarefas.findByCdProjetoAndSeqFaseOrderBySeqTarefa(cdProjeto, seqFase).stream().map(this::tarefaDTO).toList();
    }
    @Transactional
    public TarefaCadastroDTO salvarTarefa(int cdProjeto, TarefaCadastroDTO d){
        if (d.seqFase()==null) throw erro("Fase é obrigatória.");
        if (d.descricao()==null||d.descricao().isBlank()) throw erro("Descrição da tarefa é obrigatória.");
        short seq = d.seqTarefa()!=null ? d.seqTarefa() : proximaTarefa(cdProjeto, d.seqFase());
        var t = tarefas.findById(new ProjetoTarefa.PK(cdProjeto, d.seqFase(), seq))
            .orElseGet(() -> new ProjetoTarefa(cdProjeto, d.seqFase(), seq));
        t.setDescricao(d.descricao()); t.setStatus(status(d.status())); t.setExterna(d.externa()?'S':'N');
        t.setDataInicio(data(d.dataInicio())); t.setDataFim(data(d.dataFim()));
        t.setHorasPrevistas(d.horasPrevistas()); t.setHorasRealizadas(d.horasRealizadas()); t.setPercConclusao(d.percConclusao());
        t.setValorPrevisto(d.valorPrevisto()); t.setValorRealizado(d.valorRealizado());
        t.setCustoPrevisto(d.custoPrevisto()); t.setCustoRealizado(d.custoRealizado());
        t.setDtUltAlt(OffsetDateTime.now());
        return tarefaDTO(tarefas.save(t));
    }
    private short proximaTarefa(int cdProjeto, short seqFase){
        return (short)(tarefas.findByCdProjetoAndSeqFaseOrderBySeqTarefa(cdProjeto, seqFase).stream()
            .mapToInt(ProjetoTarefa::getSeqTarefa).max().orElse(0)+1);
    }
    private TarefaCadastroDTO tarefaDTO(ProjetoTarefa t){
        return new TarefaCadastroDTO(t.getSeqFase(), t.getSeqTarefa(), t.getDescricao(), t.getStatus(), t.isExterna(),
            str(t.getDataInicio()), str(t.getDataFim()),
            t.getHorasPrevistas(), t.getHorasRealizadas(), t.getPercConclusao(),
            t.getValorPrevisto(), t.getValorRealizado(), t.getCustoPrevisto(), t.getCustoRealizado());
    }

    // ----- Alocação -----
    @Transactional(readOnly=true)
    public List<AlocacaoDTO> listarAlocacoes(int cdProjeto, short seqFase, short seqTarefa){
        var lista = alocacoes.findByCdProjetoAndSeqFaseAndSeqTarefa(cdProjeto, seqFase, seqTarefa);
        Map<Integer,String> nomes = funcionarios.findAll().stream()
            .collect(java.util.stream.Collectors.toMap(Funcionario::getId, Funcionario::getNome));
        return lista.stream().map(a -> new AlocacaoDTO(a.getSeqFase(), a.getSeqTarefa(), a.getCdFuncionario(),
            nomes.get(a.getCdFuncionario()), a.getStatus(), a.getTaxaNormal(), a.getTaxaExtra())).toList();
    }
    @Transactional
    public void alocar(int cdProjeto, AlocacaoDTO d){
        if (d.seqFase()==null||d.seqTarefa()==null||d.cdFuncionario()==null) throw erro("Fase, tarefa e funcionário são obrigatórios.");
        var a = alocacoes.findById(new AlocacaoTarefa.PK(cdProjeto, d.seqFase(), d.seqTarefa(), d.cdFuncionario()))
            .orElseGet(() -> new AlocacaoTarefa(cdProjeto, d.seqFase(), d.seqTarefa(), d.cdFuncionario()));
        a.setStatus(status(d.status())); a.setTaxaNormal(d.taxaNormal()); a.setTaxaExtra(d.taxaExtra());
        alocacoes.save(a);
    }
    @Transactional
    public void desalocar(int cdProjeto, short seqFase, short seqTarefa, int cdFuncionario){
        alocacoes.deleteById(new AlocacaoTarefa.PK(cdProjeto, seqFase, seqTarefa, cdFuncionario));
    }

    // ----- helpers -----
    private static String status(String s){ return (s!=null && STATUS.contains(s)) ? s : "NIN"; }
    private static LocalDate data(String s){
        if (s==null||s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e){ throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Data inválida: "+s); }
    }
    private static String str(LocalDate d){ return d==null?null:d.toString(); }
    private ResponseStatusException erro(String m){ return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, m); }
}
