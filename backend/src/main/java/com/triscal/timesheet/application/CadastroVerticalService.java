package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.VerticalDTO;
import com.triscal.timesheet.domain.model.Vertical;
import com.triscal.timesheet.infrastructure.persistence.VerticalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CadastroVerticalService {
    private final VerticalRepository verticais;
    public CadastroVerticalService(VerticalRepository v){ this.verticais = v; }

    @Transactional(readOnly = true)
    public List<VerticalDTO> listar(){
        return verticais.findAllByOrderByDescricao().stream()
            .map(v -> new VerticalDTO(v.getId(), v.getDescricao(), v.getStatus())).toList();
    }

    @Transactional
    public VerticalDTO salvar(VerticalDTO d){
        if (d.descricao() == null || d.descricao().isBlank())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Descrição é obrigatória.");
        Vertical v;
        if (d.cdVertical() == null) {                 // novo: código sequencial automático
            v = new Vertical((short)(verticais.maxId() + 1));
        } else {
            v = verticais.findById(d.cdVertical()).orElseGet(() -> new Vertical(d.cdVertical()));
        }
        v.setDescricao(d.descricao().trim());
        v.setStatus(d.status() == null ? "AT" : d.status());
        var s = verticais.save(v);
        return new VerticalDTO(s.getId(), s.getDescricao(), s.getStatus());
    }
}
