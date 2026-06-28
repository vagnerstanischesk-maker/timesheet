package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.RelatorioAcompanhamentoDTO;
import com.triscal.timesheet.api.dto.RelatorioBheDTO;
import com.triscal.timesheet.api.dto.RelatorioLogDTO;
import com.triscal.timesheet.application.RelatorioService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/relatorios")
public class RelatorioController {
    private static final Set<String> PAPEIS = Set.of("GESTOR", "GESTOR_PROJETO", "DIRETORIA", "ADMIN_RH");
    private final RelatorioService service;
    private final UsuarioService usuarios;
    public RelatorioController(RelatorioService s, UsuarioService u){ this.service=s; this.usuarios=u; }

    private void exigirGestao(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        boolean ok = f.getPerfis()!=null && f.getPerfis().stream().anyMatch(p -> PAPEIS.contains(p.getDescricao()));
        if (!ok) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito à gestão.");
    }

    /** Logs (colaborador × período) com status diferente de Fechado. */
    @GetMapping("/logs-nao-fechados")
    public List<RelatorioLogDTO> logsNaoFechados(@AuthenticationPrincipal Jwt jwt){
        exigirGestao(jwt); return service.logsNaoFechados();
    }

    /** Acompanhamento consolidado de todos os projetos (soma de todos os períodos). */
    @GetMapping("/acompanhamento-projetos")
    public List<RelatorioAcompanhamentoDTO> acompanhamentoProjetos(@AuthenticationPrincipal Jwt jwt){
        exigirGestao(jwt); return service.acompanhamentoProjetos();
    }

    /** Movimentação de BHE no período (inicio/fim em ISO; default = mês corrente). */
    @GetMapping("/bhe")
    public List<RelatorioBheDTO> movimentacoesBhe(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(value="inicio", required=false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate inicio,
            @RequestParam(value="fim", required=false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate fim){
        exigirGestao(jwt);
        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.time.LocalDate ini = inicio != null ? inicio : hoje.withDayOfMonth(1);
        java.time.LocalDate f = fim != null ? fim : hoje.withDayOfMonth(hoje.lengthOfMonth());
        return service.movimentacoesBhe(ini, f);
    }
}
