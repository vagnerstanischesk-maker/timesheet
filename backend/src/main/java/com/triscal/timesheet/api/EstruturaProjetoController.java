package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.*;
import com.triscal.timesheet.application.EstruturaProjetoService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cadastros/projetos/{cd}")
public class EstruturaProjetoController {
    private final EstruturaProjetoService service;
    private final UsuarioService usuarios;
    public EstruturaProjetoController(EstruturaProjetoService s, UsuarioService u){ this.service=s; this.usuarios=u; }

    private void admin(Jwt jwt){
        var f = usuarios.resolver(jwt).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        if (f.getPerfis()==null || f.getPerfis().stream().noneMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    @GetMapping("/fases") public List<FaseDTO> fases(@AuthenticationPrincipal Jwt jwt, @PathVariable int cd){ admin(jwt); return service.listarFases(cd); }
    @PutMapping("/fases") public FaseDTO salvarFase(@AuthenticationPrincipal Jwt jwt, @PathVariable int cd, @RequestBody FaseDTO d){ admin(jwt); return service.salvarFase(cd, d); }

    @GetMapping("/fases/{seqFase}/tarefas") public List<TarefaCadastroDTO> tarefas(@AuthenticationPrincipal Jwt jwt, @PathVariable int cd, @PathVariable short seqFase){ admin(jwt); return service.listarTarefas(cd, seqFase); }
    @PutMapping("/tarefas") public TarefaCadastroDTO salvarTarefa(@AuthenticationPrincipal Jwt jwt, @PathVariable int cd, @RequestBody TarefaCadastroDTO d){ admin(jwt); return service.salvarTarefa(cd, d); }

    @GetMapping("/fases/{seqFase}/tarefas/{seqTarefa}/alocacoes") public List<AlocacaoDTO> alocacoes(@AuthenticationPrincipal Jwt jwt, @PathVariable int cd, @PathVariable short seqFase, @PathVariable short seqTarefa){ admin(jwt); return service.listarAlocacoes(cd, seqFase, seqTarefa); }
    @PutMapping("/alocacoes") public ResponseEntity<Void> alocar(@AuthenticationPrincipal Jwt jwt, @PathVariable int cd, @RequestBody AlocacaoDTO d){ admin(jwt); service.alocar(cd, d); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/fases/{seqFase}/tarefas/{seqTarefa}/alocacoes/{func}") public ResponseEntity<Void> desalocar(@AuthenticationPrincipal Jwt jwt, @PathVariable int cd, @PathVariable short seqFase, @PathVariable short seqTarefa, @PathVariable int func){ admin(jwt); service.desalocar(cd, seqFase, seqTarefa, func); return ResponseEntity.noContent().build(); }
}
