package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.ClienteDTO;
import com.triscal.timesheet.domain.model.Clifor;
import com.triscal.timesheet.infrastructure.persistence.CliforRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CadastroClienteService {
    private final CliforRepository clientes;
    public CadastroClienteService(CliforRepository c){ this.clientes=c; }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listar(String q){
        var fonte = (q == null || q.isBlank()) ? clientes.findAll() : clientes.pesquisar(q.trim());
        return fonte.stream().map(this::toDTO).toList();
    }

    @Transactional
    public ClienteDTO salvar(ClienteDTO d){
        // Todos os campos do cadastro de cliente são obrigatórios (regra Triscal).
        exigir(preenchido(d.razaoSocial()), "Razão social é obrigatória.");
        exigir(preenchido(d.fantasia()), "Nome fantasia é obrigatório.");
        exigir(preenchido(d.cnpj()), "CNPJ é obrigatório.");
        exigir(d.filialOrigem() != null, "Filial é obrigatória.");
        exigir(preenchido(d.contatoFinanceiro()), "Nome do contato financeiro é obrigatório.");
        exigir(preenchido(d.emailFinanceiro()), "E-mail do contato financeiro é obrigatório.");
        exigir(emailValido(d.emailFinanceiro()), "E-mail do contato financeiro é inválido.");
        exigir(preenchido(d.site()), "Site é obrigatório.");
        exigir(preenchido(d.logradouro()), "Logradouro (rua) é obrigatório.");
        exigir(preenchido(d.numero()), "Número é obrigatório.");
        exigir(preenchido(d.complemento()), "Complemento é obrigatório.");
        exigir(preenchido(d.cidade()), "Cidade é obrigatória.");
        exigir(preenchido(d.estado()) && d.estado().trim().length() == 2, "Estado (UF) é obrigatório (2 letras).");
        exigir(preenchido(d.cep()), "CEP é obrigatório.");

        // Código sequencial automático para novos clientes
        Integer id = (d.cdClifor() == null) ? (clientes.maxId() + 1) : d.cdClifor();
        var c = clientes.findById(id).orElseGet(() -> new Clifor(id));
        c.setRazaoSocial(d.razaoSocial().trim());
        c.setFantasia(d.fantasia().trim());
        c.setCnpj(somenteDigitos(d.cnpj()));
        c.setStatus(d.status()==null?"AT":d.status());
        c.setFilialOrigem(d.filialOrigem());
        c.setContatoFinanceiro(d.contatoFinanceiro().trim());
        c.setEmailFinanceiro(d.emailFinanceiro().trim());
        c.setSite(d.site().trim());
        c.setLogradouro(d.logradouro().trim());
        c.setNumero(d.numero().trim());
        c.setComplemento(d.complemento().trim());
        c.setCidade(d.cidade().trim());
        c.setEstado(d.estado().trim().toUpperCase());
        c.setCep(d.cep().trim());
        c.setDtUltAlt(OffsetDateTime.now());
        return toDTO(clientes.save(c));
    }

    private static boolean preenchido(String s){ return s != null && !s.isBlank(); }
    private static boolean emailValido(String s){ return s != null && s.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"); }
    private static String somenteDigitos(String s){ return s == null ? null : s.replaceAll("\\D", ""); }
    private static void exigir(boolean cond, String msg){
        if (!cond) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, msg);
    }

    private ClienteDTO toDTO(Clifor c){
        return new ClienteDTO(c.getId(), c.getRazaoSocial(), c.getFantasia(), c.getCnpj(), c.getStatus(),
            c.getFilialOrigem(), c.getContatoFinanceiro(), c.getEmailFinanceiro(), c.getSite(),
            c.getLogradouro(), c.getNumero(), c.getComplemento(), c.getCidade(), c.getEstado(), c.getCep());
    }
}
