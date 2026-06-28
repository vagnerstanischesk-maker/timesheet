package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.ClienteDTO;
import com.triscal.timesheet.application.CadastroClienteService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cadastros/clientes")
public class CadastroClienteController {
    private final CadastroClienteService service;
    private final UsuarioService usuarios;
    public CadastroClienteController(CadastroClienteService s, UsuarioService u){ this.service=s; this.usuarios=u; }

    private void admin(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        if (f.getPerfis()==null || f.getPerfis().stream().noneMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    @GetMapping
    public List<ClienteDTO> listar(@AuthenticationPrincipal Jwt jwt, @RequestParam(value="q", required=false) String q){
        admin(jwt); return service.listar(q);
    }

    @PutMapping
    public ClienteDTO salvar(@AuthenticationPrincipal Jwt jwt, @RequestBody ClienteDTO d){ admin(jwt); return service.salvar(d); }
}
