package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.MovimentoBheDTO;
import com.triscal.timesheet.application.BheManualService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/cadastros/bhe")
public class BheManualController {
    private final BheManualService service;
    private final UsuarioService usuarios;
    public BheManualController(BheManualService s, UsuarioService u){ this.service=s; this.usuarios=u; }

    private void admin(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        if (f.getPerfis()==null || f.getPerfis().stream().noneMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    @PostMapping("/manual") public ResponseEntity<Void> registrar(@AuthenticationPrincipal Jwt jwt, @RequestBody MovimentoBheDTO d){
        admin(jwt); service.registrar(d, jwt.getSubject()); return ResponseEntity.noContent().build();
    }
}
