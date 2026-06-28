package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.QuinzenaDTO;
import com.triscal.timesheet.api.dto.GradeEdicaoDTO;
import com.triscal.timesheet.application.QuinzenaService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/timesheet/quinzena")
public class QuinzenaController {

    private final QuinzenaService quinzena;
    private final UsuarioService usuarios;

    public QuinzenaController(QuinzenaService q, UsuarioService u) { this.quinzena = q; this.usuarios = u; }

    private int funcId(Jwt jwt) {
        return usuarios.resolver(jwt).map(f -> f.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
    }

    /** Visão da quinzena do colaborador autenticado. */
    @GetMapping("/{ano}/{mes}/{periodo}")
    public QuinzenaDTO obter(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo) {
        return quinzena.montar(funcId(jwt), ano, mes, periodo);
    }

    /** Dados do período inteiro para o grid editável (estilo "Meu dia" para todos os dias). */
    @GetMapping("/{ano}/{mes}/{periodo}/edicao")
    public GradeEdicaoDTO edicao(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo) {
        return quinzena.gradeEdicao(funcId(jwt), ano, mes, periodo, true);
    }

    /** Fecha a quinzena (cada dia A/R do período -> F). */
    @PostMapping("/{ano}/{mes}/{periodo}/fechar")
    public Map<String, Integer> fechar(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo) {
        int fechados = quinzena.fecharQuinzena(funcId(jwt), ano, mes, periodo, jwt.getSubject());
        return Map.of("diasFechados", fechados);
    }
}
