package com.triscal.timesheet.api;

import com.triscal.timesheet.api.dto.MeResponse;
import com.triscal.timesheet.application.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MeController {

    private final UsuarioService usuarios;

    public MeController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    /** Identidade autenticada + papéis resolvidos internamente. */
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt) {
        return usuarios.toMe(jwt);
    }
}
