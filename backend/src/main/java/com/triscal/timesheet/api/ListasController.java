package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.LookupDTO;
import com.triscal.timesheet.application.UsuarioService;
import com.triscal.timesheet.infrastructure.persistence.EmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Listas de valores compartilhadas pelos cadastros (filiais, etc.). */
@RestController
@RequestMapping("/api/v1/cadastros/listas")
public class ListasController {
    private final EmpresaRepository empresas;
    private final UsuarioService usuarios;
    public ListasController(EmpresaRepository e, UsuarioService u){ this.empresas=e; this.usuarios=u; }

    private void admin(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        if (f.getPerfis()==null || f.getPerfis().stream().noneMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    /** Filiais selecionáveis na tela (RJ, SP). As legadas existem só para compatibilidade de dados. */
    @GetMapping("/filiais")
    public List<LookupDTO> filiais(@AuthenticationPrincipal Jwt jwt){
        admin(jwt);
        return empresas.findBySelecionavelOrderBySigla("S").stream()
            .map(e -> new LookupDTO(e.getId(), e.getSigla() != null ? e.getSigla() : e.getNome())).toList();
    }
}
