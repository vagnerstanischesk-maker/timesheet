package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.VerticalDTO;
import com.triscal.timesheet.application.CadastroVerticalService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Cadastro de Verticais (linha de negócio) — gerenciável pelo Admin/RH. */
@RestController
@RequestMapping("/api/v1/cadastros/verticais")
public class CadastroVerticalController {
    private final CadastroVerticalService service;
    private final UsuarioService usuarios;
    public CadastroVerticalController(CadastroVerticalService s, UsuarioService u){ this.service=s; this.usuarios=u; }

    private void admin(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        if (f.getPerfis()==null || f.getPerfis().stream().noneMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    @GetMapping public List<VerticalDTO> listar(@AuthenticationPrincipal Jwt jwt){ admin(jwt); return service.listar(); }
    @PutMapping public VerticalDTO salvar(@AuthenticationPrincipal Jwt jwt, @RequestBody VerticalDTO d){ admin(jwt); return service.salvar(d); }
}
