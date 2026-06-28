package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.LookupDTO;
import com.triscal.timesheet.api.dto.ProjetoDTO;
import com.triscal.timesheet.domain.model.Projeto;
import com.triscal.timesheet.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CadastroProjetoService {

    public static final Set<String> TIPOS_FATURAMENTO =
        Set.of("FECHADO_FASE", "FECHADO_MENSAL", "HH_MENSAL", "CUSTOMIZADO", "NENHUM");

    private final ProjetoRepository projetos;
    private final CliforRepository clientes;
    private final VerticalRepository verticais;
    private final TipoContratoRepository tiposContrato;
    private final FuncionarioRepository funcionarios;

    public CadastroProjetoService(ProjetoRepository projetos, CliforRepository clientes,
                                  VerticalRepository verticais, TipoContratoRepository tiposContrato,
                                  FuncionarioRepository funcionarios) {
        this.projetos = projetos; this.clientes = clientes;
        this.verticais = verticais; this.tiposContrato = tiposContrato; this.funcionarios = funcionarios;
    }

    @Transactional(readOnly = true)
    public List<ProjetoDTO> listar(String q, Integer cliente) {
        boolean semFiltro = (q == null || q.isBlank()) && cliente == null;
        var fonte = semFiltro ? projetos.findAll() : projetos.pesquisar(q == null ? null : q.trim(), cliente);
        Map<Integer,String> nomes = clientes.findAll().stream()
            .collect(Collectors.toMap(c -> c.getId(), c -> c.getRazaoSocial()));
        return fonte.stream().map(p -> toDTO(p, nomes.get(p.getCdClifor()))).toList();
    }

    @Transactional
    public ProjetoDTO salvar(ProjetoDTO dto) {
        exigir(preenchido(dto.descricao()), "Descrição é obrigatória.");
        if (!dto.interno())
            exigir(dto.cdClifor() != null, "Cliente é obrigatório (projeto não interno).");
        if (dto.tipoFaturamento() != null && !dto.tipoFaturamento().isBlank())
            exigir(TIPOS_FATURAMENTO.contains(dto.tipoFaturamento()), "Tipo de faturamento inválido.");
        LocalDate ini = data(dto.dataInicio()), fim = data(dto.dataFim());
        if (ini != null && fim != null) exigir(!fim.isBefore(ini), "Data de fim não pode ser anterior à de início.");

        Integer id = dto.cdProjeto();
        var p = (id == null) ? new Projeto(projetos.maxId() + 1)
                             : projetos.findById(id).orElseGet(() -> new Projeto(id));
        p.setDescricao(dto.descricao().trim());
        p.setInterno(dto.interno() ? 'S' : 'N');
        p.setCdClifor(dto.cdClifor());
        p.setCdEmpresa(dto.cdEmpresa());
        p.setStatus(dto.status() == null ? "AT" : dto.status());
        p.setDataInicio(ini); p.setDataFim(fim);
        p.setAceitaCo(dto.aceitaCo() ? 'S' : 'N');
        p.setAceitaNc(dto.aceitaNc() ? 'S' : 'N');
        p.setTipoFaturamento(dto.tipoFaturamento());
        p.setGerenteTecnico(dto.gerenteTecnico());
        p.setGerenteComercial(dto.gerenteComercial());
        p.setVertical(dto.vertical());
        p.setTipoContrato(dto.tipoContrato());
        p.setComissao(dto.comissao() ? 'S' : 'N');
        p.setObservacao(dto.observacao());
        // Os totais (horas/valor/custo previsto e realizado) NÃO vêm do formulário —
        // são somatórios das fases, atualizados ao salvar cada fase (ver EstruturaProjetoService).
        p.setDtUltAlt(OffsetDateTime.now());
        var salvo = projetos.save(p);
        String nomeCli = salvo.getCdClifor() == null ? null :
            clientes.findById(salvo.getCdClifor()).map(c -> c.getRazaoSocial()).orElse(null);
        return toDTO(salvo, nomeCli);
    }

    @Transactional(readOnly = true)
    public List<LookupDTO> verticais() {
        return verticais.findByStatusOrderByDescricao("AT").stream().map(v -> new LookupDTO(v.getId(), v.getDescricao())).toList();
    }
    @Transactional(readOnly = true)
    public List<LookupDTO> tiposContrato() {
        return tiposContrato.findByStatusOrderByDescricao("AT").stream().map(t -> new LookupDTO(t.getId(), t.getDescricao())).toList();
    }
    @Transactional(readOnly = true)
    public List<LookupDTO> gerentesTecnicos() {
        return funcionarios.porPerfis(List.of("GESTOR", "GESTOR_PROJETO")).stream().map(f -> new LookupDTO(f.getId(), f.getNome())).toList();
    }
    @Transactional(readOnly = true)
    public List<LookupDTO> gerentesComerciais() {
        return funcionarios.porPerfis(List.of("COMERCIAL")).stream().map(f -> new LookupDTO(f.getId(), f.getNome())).toList();
    }
    public List<LookupDTO> tiposFaturamento() {
        return List.of(
            new LookupDTO("FECHADO_FASE", "Fechado por fase"),
            new LookupDTO("FECHADO_MENSAL", "Fechado com faturamento mensal"),
            new LookupDTO("HH_MENSAL", "HH com faturamento mensal"),
            new LookupDTO("CUSTOMIZADO", "Customizado"),
            new LookupDTO("NENHUM", "Nenhum"));
    }

    private static boolean preenchido(String s){ return s != null && !s.isBlank(); }
    private static void exigir(boolean cond, String msg){
        if (!cond) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, msg);
    }
    private static LocalDate data(String s){
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e){ throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Data inválida: "+s); }
    }

    private ProjetoDTO toDTO(Projeto p, String nomeCliente) {
        return new ProjetoDTO(p.getId(), p.getDescricao(), p.getInterno() == 'S',
            p.getCdClifor(), nomeCliente, p.getCdEmpresa(), p.getStatus(),
            p.getDataInicio()==null?null:p.getDataInicio().toString(),
            p.getDataFim()==null?null:p.getDataFim().toString(),
            p.getAceitaCo() == 'S', p.getAceitaNc() == 'S',
            p.getTipoFaturamento(), p.getGerenteTecnico(), p.getGerenteComercial(),
            p.getVertical(), p.getTipoContrato(), p.getComissao() == 'S', p.getObservacao(),
            p.getHorasPrevistas(), p.getHorasRealizadas(), p.getValorPrevisto(), p.getValorRealizado(),
            p.getCustoPrevisto(), p.getCustoRealizado());
    }
}
