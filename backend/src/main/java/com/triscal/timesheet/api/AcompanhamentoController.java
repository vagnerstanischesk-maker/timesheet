package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.AcompanhamentoDTO;
import com.triscal.timesheet.api.dto.InformarAndamentoRequest;
import com.triscal.timesheet.application.AcompanhamentoService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projetos/{cdProjeto}/acompanhamento")
public class AcompanhamentoController {

    private final AcompanhamentoService acompanhamento;
    private final UsuarioService usuarios;

    public AcompanhamentoController(AcompanhamentoService a, UsuarioService u) {
        this.acompanhamento = a; this.usuarios = u;
    }

    /** Exige Gestor de Projeto, Admin/RH ou Diretoria (papéis resolvidos internamente, não do token). */
    private String exigirGestorProjeto(Jwt jwt) {
        var f = usuarios.resolver(jwt).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        boolean ok = f.getPerfis() != null && f.getPerfis().stream()
            .anyMatch(p -> "GESTOR_PROJETO".equals(p.getDescricao()) || "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao()));
        if (!ok) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Requer papel Gestor de Projeto, Admin/RH ou Diretoria.");
        return jwt.getSubject();
    }

    @GetMapping("/{ano}/{mes}/{periodo}")
    public AcompanhamentoDTO obter(@AuthenticationPrincipal Jwt jwt, @PathVariable int cdProjeto,
            @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo) {
        exigirGestorProjeto(jwt);
        return acompanhamento.montar(cdProjeto, ano, mes, periodo);
    }

    @PostMapping("/andamento")
    public ResponseEntity<Void> informar(@AuthenticationPrincipal Jwt jwt, @PathVariable int cdProjeto,
            @RequestBody InformarAndamentoRequest req) {
        String u = exigirGestorProjeto(jwt);
        acompanhamento.informarAndamento(cdProjeto, req.seqFase(), req.seqTarefa(), req.percInformado(), u);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{ano}/{mes}/{periodo}/fechar")
    public ResponseEntity<Void> fechar(@AuthenticationPrincipal Jwt jwt, @PathVariable int cdProjeto,
            @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo,
            @RequestParam BigDecimal percInformado) {
        String u = exigirGestorProjeto(jwt);
        acompanhamento.fecharQuinzena(cdProjeto, ano, mes, periodo, percInformado, u);
        return ResponseEntity.noContent().build();
    }
}
