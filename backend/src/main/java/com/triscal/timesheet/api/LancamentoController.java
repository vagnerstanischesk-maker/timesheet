package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.DiaDTO;
import com.triscal.timesheet.api.dto.TarefaAlocadaDTO;
import com.triscal.timesheet.application.LancamentoService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timesheet")
public class LancamentoController {

    private final LancamentoService lancamentos;
    private final UsuarioService usuarios;

    public LancamentoController(LancamentoService l, UsuarioService u) {
        this.lancamentos = l; this.usuarios = u;
    }

    private int funcId(Jwt jwt) {
        return usuarios.resolver(jwt).map(f -> f.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
    }

    /** Tarefas que o colaborador pode lançar (alocadas, ativas e dentro da validade na data informada). */
    @GetMapping("/tarefas-alocadas")
    public List<TarefaAlocadaDTO> tarefasAlocadas(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "data", required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate data) {
        return lancamentos.tarefasAlocadas(funcId(jwt), data);
    }

    @GetMapping("/dia/{ano}/{mes}/{dia}")
    public DiaDTO obterDia(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int ano, @PathVariable int mes, @PathVariable int dia) {
        return lancamentos.montarDia(funcId(jwt), ano, mes, dia);
    }

    @PutMapping("/dia")
    public ResponseEntity<Void> salvarDia(@AuthenticationPrincipal Jwt jwt, @RequestBody DiaDTO dia) {
        lancamentos.salvarRascunho(funcId(jwt), dia, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dia/{ano}/{mes}/{dia}/fechar")
    public ResponseEntity<Void> fecharDia(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int ano, @PathVariable int mes, @PathVariable int dia) {
        lancamentos.fecharDia(funcId(jwt), ano, mes, dia, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
