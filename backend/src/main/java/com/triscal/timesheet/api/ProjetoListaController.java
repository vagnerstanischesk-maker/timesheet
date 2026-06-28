package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.ProjetoListaDTO;
import com.triscal.timesheet.application.UsuarioService;
import com.triscal.timesheet.infrastructure.persistence.CliforRepository;
import com.triscal.timesheet.infrastructure.persistence.ProjetoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Lista de projetos para seleção por cliente -> projeto (Gestor de Projeto). */
@RestController
@RequestMapping("/api/v1/projetos")
public class ProjetoListaController {
    private final ProjetoRepository projetos;
    private final CliforRepository clientes;
    private final UsuarioService usuarios;
    public ProjetoListaController(ProjetoRepository p, CliforRepository c, UsuarioService u){ this.projetos=p; this.clientes=c; this.usuarios=u; }

    private void exigirGestorProjeto(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        boolean ok = f.getPerfis()!=null && f.getPerfis().stream().anyMatch(p ->
            "GESTOR_PROJETO".equals(p.getDescricao()) || "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao()));
        if (!ok) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Requer papel Gestor de Projeto, Admin/RH ou Diretoria.");
    }

    @GetMapping("/lista")
    public List<ProjetoListaDTO> lista(@AuthenticationPrincipal Jwt jwt){
        exigirGestorProjeto(jwt);
        Map<Integer,String> nomes = clientes.findAll().stream().collect(Collectors.toMap(c -> c.getId(), c -> c.getRazaoSocial()));
        return projetos.findAll().stream()
            .map(p -> new ProjetoListaDTO(p.getId(), p.getDescricao(), p.getCdClifor(),
                p.getCdClifor()==null?null:nomes.get(p.getCdClifor()), p.getInterno()=='S'))
            .sorted((a,b) -> a.descricao().compareToIgnoreCase(b.descricao()))
            .toList();
    }
}
