package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.ResumoDashboardDTO;
import com.triscal.timesheet.application.DashboardService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/dashboards")
public class DashboardController {

    private static final Set<String> PERMITIDOS = Set.of("GESTOR", "GESTOR_PROJETO", "DIRETORIA", "ADMIN_RH");

    private final DashboardService dashboards;
    private final UsuarioService usuarios;

    public DashboardController(DashboardService d, UsuarioService u) { this.dashboards = d; this.usuarios = u; }

    private void exigirGestao(Jwt jwt) {
        var f = usuarios.resolver(jwt).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        boolean ok = f.getPerfis() != null && f.getPerfis().stream()
            .anyMatch(p -> PERMITIDOS.contains(p.getDescricao()));
        if (!ok) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a perfis de gestão.");
    }

    @GetMapping("/resumo/{ano}/{mes}")
    public ResumoDashboardDTO resumo(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int ano, @PathVariable int mes) {
        exigirGestao(jwt);
        return dashboards.resumo(ano, mes);
    }
}
