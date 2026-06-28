package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.FeriadoDTO;
import com.triscal.timesheet.application.CadastroFeriadoService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cadastros/feriados")
public class CadastroFeriadoController {
    private final CadastroFeriadoService service;
    private final UsuarioService usuarios;
    public CadastroFeriadoController(CadastroFeriadoService s, UsuarioService u){ this.service=s; this.usuarios=u; }

    private void admin(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        if (f.getPerfis()==null || f.getPerfis().stream().noneMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    @GetMapping public List<FeriadoDTO> listar(@AuthenticationPrincipal Jwt jwt){ admin(jwt); return service.listar(); }
    @PutMapping public FeriadoDTO salvar(@AuthenticationPrincipal Jwt jwt, @RequestBody FeriadoDTO d){ admin(jwt); return service.salvar(d); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id){ admin(jwt); service.excluir(id); return ResponseEntity.noContent().build(); }
}
