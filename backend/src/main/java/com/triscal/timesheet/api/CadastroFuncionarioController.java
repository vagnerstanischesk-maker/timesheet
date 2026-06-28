package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.FuncionarioDTO;
import com.triscal.timesheet.application.CadastroFuncionarioService;
import com.triscal.timesheet.application.UsuarioService;
import com.triscal.timesheet.domain.model.Perfil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cadastros/funcionarios")
public class CadastroFuncionarioController {
    private final CadastroFuncionarioService service;
    private final UsuarioService usuarios;
    public CadastroFuncionarioController(CadastroFuncionarioService s, UsuarioService u){ this.service=s; this.usuarios=u; }

    private void admin(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        if (f.getPerfis()==null || f.getPerfis().stream().noneMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    @GetMapping public List<FuncionarioDTO> listar(@AuthenticationPrincipal Jwt jwt){ admin(jwt); return service.listar(); }
    @PutMapping public FuncionarioDTO salvar(@AuthenticationPrincipal Jwt jwt, @RequestBody FuncionarioDTO d){ admin(jwt); return service.salvar(d); }
    @GetMapping("/perfis") public List<Map<String,Object>> perfis(@AuthenticationPrincipal Jwt jwt){
        admin(jwt);
        return service.perfisDisponiveis().stream().map(p -> Map.<String,Object>of("id", p.getId(), "descricao", p.getDescricao())).toList();
    }
}
