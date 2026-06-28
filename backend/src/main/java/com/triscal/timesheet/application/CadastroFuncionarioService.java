package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.FuncionarioDTO;
import com.triscal.timesheet.domain.model.Funcionario;
import com.triscal.timesheet.domain.model.Perfil;
import com.triscal.timesheet.infrastructure.persistence.FuncionarioRepository;
import com.triscal.timesheet.infrastructure.persistence.PerfilRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CadastroFuncionarioService {
    private static final Set<String> ESTADOS_CIVIS = Set.of("SOLTEIRO","CASADO","DIVORCIADO","VIUVO");

    private final FuncionarioRepository funcionarios;
    private final PerfilRepository perfis;
    public CadastroFuncionarioService(FuncionarioRepository f, PerfilRepository p){ this.funcionarios=f; this.perfis=p; }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listar(){
        var todos = funcionarios.findAll();
        Map<Integer,String> nomes = todos.stream().collect(java.util.stream.Collectors.toMap(Funcionario::getId, Funcionario::getNome));
        return todos.stream().map(f -> toDTO(f, nomes.get(f.getAprovadorId()))).toList();
    }

    @Transactional(readOnly = true)
    public List<Perfil> perfisDisponiveis(){ return perfis.findAll(); }

    @Transactional
    public FuncionarioDTO salvar(FuncionarioDTO d){
        if (d.nome()==null||d.nome().isBlank()) throw erro("Nome é obrigatório.");
        if (d.tipo()==null||!(d.tipo().equals("E")||d.tipo().equals("F"))) throw erro("Tipo deve ser E (PJ) ou F (CLT).");
        if (d.estadoCivil()!=null && !d.estadoCivil().isBlank() && !ESTADOS_CIVIS.contains(d.estadoCivil()))
            throw erro("Estado civil inválido.");
        String cpfDigitos = d.cpf()==null?null:d.cpf().replaceAll("\\D","");
        if (d.tipo().equals("F") && (cpfDigitos==null || cpfDigitos.isBlank()))
            throw erro("CPF é obrigatório para funcionário CLT.");
        if (cpfDigitos!=null && !cpfDigitos.isBlank() && !cpfValido(cpfDigitos))
            throw erro("CPF inválido (não passou na validação da Receita Federal).");

        Integer id = d.cdFuncionario();
        var f = (id==null)
            ? new Funcionario(funcionarios.maxId() + 1)
            : funcionarios.findById(id).orElseGet(() -> new Funcionario(id));

        f.setNome(d.nome().trim()); f.setTipo(d.tipo().charAt(0)); f.setEmail(trim(d.email()));
        f.setCdgUsur(trim(d.cdgUsur())); f.setCdEmpresa(d.cdEmpresa()); f.setAprovadorId(d.aprovadorId());
        f.setPossuiBancoHoras(d.possuiBancoHoras()?'S':'N'); f.setStatus(d.status()==null?"AT":d.status());
        if (d.perfis()!=null) f.setPerfis(new HashSet<>(perfis.findAllById(d.perfis())));

        f.setCpf(cpfDigitos); f.setRg(trim(d.rg())); f.setOrgaoEmissor(trim(d.orgaoEmissor()));
        f.setTituloEleitor(trim(d.tituloEleitor())); f.setZonaEleitoral(trim(d.zonaEleitoral())); f.setSecaoEleitoral(trim(d.secaoEleitoral()));
        f.setNacionalidade(trim(d.nacionalidade())); f.setNomePai(trim(d.nomePai())); f.setNomeMae(trim(d.nomeMae()));
        f.setEstadoCivil(vazioNull(d.estadoCivil()));
        f.setDataNascimento(data(d.dataNascimento())); f.setDataAdmissao(data(d.dataAdmissao())); f.setDataRescisao(data(d.dataRescisao()));
        f.setLogradouro(trim(d.logradouro())); f.setNumero(trim(d.numero())); f.setComplemento(trim(d.complemento()));
        f.setBairro(trim(d.bairro())); f.setCidade(trim(d.cidade())); f.setEstado(d.estado()==null||d.estado().isBlank()?null:d.estado().trim().toUpperCase());
        f.setCep(trim(d.cep())); f.setTelResidencial(trim(d.telResidencial())); f.setTelCelular(trim(d.telCelular()));
        f.setBanco(trim(d.banco())); f.setAgencia(trim(d.agencia())); f.setConta(trim(d.conta()));
        f.setUltimoPagamento(d.ultimoPagamento()); f.setPis(trim(d.pis()));
        f.setCarteiraTrabalho(trim(d.carteiraTrabalho())); f.setSerieCarteira(trim(d.serieCarteira()));
        f.setContrato(trim(d.contrato())); f.setDescricaoContrato(trim(d.descricaoContrato()));
        if (d.saldoBhe50()!=null) f.setSaldoBhe50(d.saldoBhe50());
        if (d.saldoBhe100()!=null) f.setSaldoBhe100(d.saldoBhe100());
        f.setValeTransporte(d.valeTransporte()?'S':'N'); f.setPlanoSaude(d.planoSaude()?'S':'N'); f.setRequerAprovAdm(d.requerAprovAdm()?'S':'N');

        var salvo = funcionarios.save(f);
        String nomeApr = salvo.getAprovadorId()==null?null:
            funcionarios.findById(salvo.getAprovadorId()).map(Funcionario::getNome).orElse(null);
        return toDTO(salvo, nomeApr);
    }

    private static String trim(String s){ return s==null?null:(s.isBlank()?null:s.trim()); }
    private static String vazioNull(String s){ return (s==null||s.isBlank())?null:s; }
    private static LocalDate data(String s){
        if (s==null||s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e){ throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Data inválida: "+s); }
    }
    private ResponseStatusException erro(String m){ return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, m); }

    /** Validação de CPF (dígitos verificadores — algoritmo da Receita Federal). */
    static boolean cpfValido(String d){
        if (d==null || d.length()!=11) return false;
        if (d.chars().distinct().count()==1) return false;
        int[] n = new int[11];
        for (int i=0;i<11;i++) n[i]=d.charAt(i)-'0';
        int s=0; for (int i=0;i<9;i++) s+=n[i]*(10-i);
        int r=s%11; int dv1=(r<2)?0:11-r;
        if (dv1!=n[9]) return false;
        s=0; for (int i=0;i<10;i++) s+=n[i]*(11-i);
        r=s%11; int dv2=(r<2)?0:11-r;
        return dv2==n[10];
    }

    private FuncionarioDTO toDTO(Funcionario f, String nomeAprovador){
        var ids = f.getPerfis()==null? List.<Short>of() : f.getPerfis().stream().map(Perfil::getId).sorted().toList();
        return new FuncionarioDTO(f.getId(), f.getNome(), String.valueOf(f.getTipo()), f.getEmail(), f.getCdgUsur(),
            f.getCdEmpresa(), f.getAprovadorId(), nomeAprovador, f.possuiBancoHoras(), f.getStatus(), ids,
            f.getCpf(), f.getRg(), f.getOrgaoEmissor(), f.getTituloEleitor(), f.getZonaEleitoral(), f.getSecaoEleitoral(),
            f.getNacionalidade(), f.getNomePai(), f.getNomeMae(), f.getEstadoCivil(),
            str(f.getDataNascimento()), str(f.getDataAdmissao()), str(f.getDataRescisao()),
            f.getLogradouro(), f.getNumero(), f.getComplemento(), f.getBairro(), f.getCidade(), f.getEstado(), f.getCep(),
            f.getTelResidencial(), f.getTelCelular(),
            f.getBanco(), f.getAgencia(), f.getConta(), f.getUltimoPagamento(), f.getPis(),
            f.getCarteiraTrabalho(), f.getSerieCarteira(), f.getContrato(), f.getDescricaoContrato(),
            f.getSaldoBhe50(), f.getSaldoBhe100(),
            f.recebeValeTransporte(), f.possuiPlanoSaude(), f.requerAprovAdm());
    }
    private static String str(LocalDate d){ return d==null?null:d.toString(); }
}
