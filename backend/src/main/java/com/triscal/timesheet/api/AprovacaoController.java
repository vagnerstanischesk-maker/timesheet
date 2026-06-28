package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.AprovacaoResumo;
import com.triscal.timesheet.api.dto.PendenteDTO;
import com.triscal.timesheet.api.dto.QuinzenaDTO;
import com.triscal.timesheet.api.dto.GradeEdicaoDTO;
import com.triscal.timesheet.api.dto.RejeicaoRequest;
import com.triscal.timesheet.application.AprovacaoService;
import com.triscal.timesheet.application.QuinzenaService;
import com.triscal.timesheet.application.UsuarioService;
import com.triscal.timesheet.infrastructure.persistence.FuncionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aprovacao")
public class AprovacaoController {

    private final AprovacaoService aprovacao;
    private final UsuarioService usuarios;
    private final QuinzenaService quinzena;
    private final FuncionarioRepository funcionarios;

    public AprovacaoController(AprovacaoService a, UsuarioService u, QuinzenaService q, FuncionarioRepository f) {
        this.aprovacao = a; this.usuarios = u; this.quinzena = q; this.funcionarios = f;
    }

    private int aprovadorId(Jwt jwt) {
        return usuarios.resolver(jwt).map(f -> f.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
    }

    @GetMapping("/pendentes")
    public List<PendenteDTO> pendentes(@AuthenticationPrincipal Jwt jwt) {
        return aprovacao.pendentes(aprovadorId(jwt));
    }

    /** Grade (horas por projeto × dia) do colaborador, para o aprovador avaliar o lançamento. */
    @GetMapping("/grade/{func}/{ano}/{mes}/{periodo}")
    public QuinzenaDTO grade(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int func, @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo) {
        int aprovador = aprovadorId(jwt);
        var sub = funcionarios.findById(func).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado."));
        if (sub.getAprovadorId() == null || !sub.getAprovadorId().equals(aprovador))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é o aprovador deste colaborador.");
        return quinzena.montar(func, ano, mes, periodo);
    }

    /** Grade detalhada (tarefa × dia, por tipo de hora) do colaborador, para o aprovador avaliar. */
    @GetMapping("/edicao/{func}/{ano}/{mes}/{periodo}")
    public GradeEdicaoDTO edicao(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int func, @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo) {
        int aprovador = aprovadorId(jwt);
        var sub = funcionarios.findById(func).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado."));
        if (sub.getAprovadorId() == null || !sub.getAprovadorId().equals(aprovador))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é o aprovador deste colaborador.");
        return quinzena.gradeEdicao(func, ano, mes, periodo);
    }

    @PostMapping("/quinzena/{func}/{ano}/{mes}/{periodo}")
    public AprovacaoResumo aprovarQuinzena(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int func, @PathVariable int ano, @PathVariable int mes, @PathVariable int periodo) {
        return aprovacao.aprovarQuinzena(aprovadorId(jwt), func, ano, mes, periodo);
    }

    @PostMapping("/dia/{func}/{ano}/{mes}/{dia}/aprovar")
    public ResponseEntity<Void> aprovar(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int func, @PathVariable int ano, @PathVariable int mes, @PathVariable int dia) {
        aprovacao.aprovarDia(aprovadorId(jwt), func, ano, mes, dia);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dia/{func}/{ano}/{mes}/{dia}/rejeitar")
    public ResponseEntity<Void> rejeitar(@AuthenticationPrincipal Jwt jwt,
            @PathVariable int func, @PathVariable int ano, @PathVariable int mes, @PathVariable int dia,
            @RequestBody RejeicaoRequest req) {
        aprovacao.rejeitarDia(aprovadorId(jwt), func, ano, mes, dia, req.observacao());
        return ResponseEntity.noContent().build();
    }
}
