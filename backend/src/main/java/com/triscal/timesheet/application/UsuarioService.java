package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.MeResponse;
import com.triscal.timesheet.domain.model.Funcionario;
import com.triscal.timesheet.domain.model.Perfil;
import com.triscal.timesheet.infrastructure.persistence.FuncionarioRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Resolve o usuário autenticado (identidade do token do Microsoft Entra ID) para o
 * funcionário interno e seus papéis. O token NÃO traz papéis: tudo vem das
 * tabelas internas (perfil, funcionario_perfil).
 */
@Service
public class UsuarioService {

    private final FuncionarioRepository funcionarios;

    public UsuarioService(FuncionarioRepository funcionarios) {
        this.funcionarios = funcionarios;
    }

    /** Casa o token com o funcionário: preferência por e-mail/UPN, fallback por 'oid'. */
    public Optional<Funcionario> resolver(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null) email = jwt.getClaimAsString("preferred_username");
        Optional<Funcionario> f = Optional.empty();
        if (email != null) {
            f = funcionarios.findByCdgUsurIgnoreCase(email);
            if (f.isEmpty()) f = funcionarios.findByEmailIgnoreCase(email);
        }
        if (f.isEmpty()) {
            String oid = jwt.getClaimAsString("oid");
            if (oid != null) f = funcionarios.findByCdgUsurIgnoreCase(oid);
        }
        return f;
    }

    public MeResponse toMe(Jwt jwt) {
        return resolver(jwt)
            .map(f -> new MeResponse(
                f.getId(), f.getNome(), f.getEmail(),
                f.getPerfis() == null ? List.of()
                    : f.getPerfis().stream().map(Perfil::getDescricao).sorted().toList(),
                true))
            .orElseGet(() -> new MeResponse(
                null,
                jwt.getClaimAsString("name"),
                jwt.getClaimAsString("email"),
                List.of(),
                false)); // autenticou no Entra mas ainda não há funcionário cadastrado
    }
}
