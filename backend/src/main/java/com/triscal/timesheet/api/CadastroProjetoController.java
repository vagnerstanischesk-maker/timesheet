package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.LookupDTO;
import com.triscal.timesheet.api.dto.ProjetoDTO;
import com.triscal.timesheet.application.CadastroProjetoService;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cadastros/projetos")
public class CadastroProjetoController {

    private final CadastroProjetoService service;
    private final UsuarioService usuarios;

    public CadastroProjetoController(CadastroProjetoService s, UsuarioService u) { this.service = s; this.usuarios = u; }

    private void exigirAdmin(Jwt jwt) {
        var f = usuarios.resolver(jwt).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não cadastrado."));
        boolean ok = f.getPerfis() != null && f.getPerfis().stream()
            .anyMatch(p -> "ADMIN_RH".equals(p.getDescricao()) || "DIRETORIA".equals(p.getDescricao()));
        if (!ok) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a Admin/RH ou Diretoria.");
    }

    @GetMapping
    public List<ProjetoDTO> listar(@AuthenticationPrincipal Jwt jwt,
                                   @RequestParam(value="q", required=false) String q,
                                   @RequestParam(value="cliente", required=false) Integer cliente) {
        exigirAdmin(jwt); return service.listar(q, cliente);
    }

    @PutMapping
    public ProjetoDTO salvar(@AuthenticationPrincipal Jwt jwt, @RequestBody ProjetoDTO dto) {
        exigirAdmin(jwt); return service.salvar(dto);
    }

    @GetMapping("/listas/verticais")
    public List<LookupDTO> verticais(@AuthenticationPrincipal Jwt jwt){ exigirAdmin(jwt); return service.verticais(); }
    @GetMapping("/listas/tipos-contrato")
    public List<LookupDTO> tiposContrato(@AuthenticationPrincipal Jwt jwt){ exigirAdmin(jwt); return service.tiposContrato(); }
    @GetMapping("/listas/tipos-faturamento")
    public List<LookupDTO> tiposFaturamento(@AuthenticationPrincipal Jwt jwt){ exigirAdmin(jwt); return service.tiposFaturamento(); }
    @GetMapping("/listas/gerentes-tecnicos")
    public List<LookupDTO> gerentesTecnicos(@AuthenticationPrincipal Jwt jwt){ exigirAdmin(jwt); return service.gerentesTecnicos(); }
    @GetMapping("/listas/gerentes-comerciais")
    public List<LookupDTO> gerentesComerciais(@AuthenticationPrincipal Jwt jwt){ exigirAdmin(jwt); return service.gerentesComerciais(); }
}
