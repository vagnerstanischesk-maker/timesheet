package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.FeriadoDTO;
import com.triscal.timesheet.domain.model.Feriado;
import com.triscal.timesheet.infrastructure.persistence.FeriadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CadastroFeriadoService {
    private static final Set<String> TIPOS = Set.of("NN","NF","LN","LF");
    private final FeriadoRepository feriados;
    public CadastroFeriadoService(FeriadoRepository f){ this.feriados=f; }

    @Transactional(readOnly = true)
    public List<FeriadoDTO> listar(){ return feriados.findAll().stream().map(this::toDTO).toList(); }

    @Transactional
    public FeriadoDTO salvar(FeriadoDTO d){
        if (d.nome()==null||d.nome().isBlank()) throw erro("Nome é obrigatório.");
        if (d.mes()==null||d.dia()==null) throw erro("Mês e dia são obrigatórios.");
        if (d.tipo()==null||!TIPOS.contains(d.tipo())) throw erro("Tipo deve ser NN, NF, LN ou LF.");
        if (("LN".equals(d.tipo())||"LF".equals(d.tipo())) && d.filial()==null) throw erro("Feriado local exige filial.");
        if (("NN".equals(d.tipo())||"LN".equals(d.tipo())) && d.ano()==null) throw erro("Feriado anual exige ano.");
        var f = d.id()==null ? new Feriado() : feriados.findById(d.id()).orElseGet(Feriado::new);
        f.setNome(d.nome()); f.setAno(d.ano()); f.setMes(d.mes()); f.setDia(d.dia());
        f.setTipo(d.tipo()); f.setFilial(d.filial());
        f.setHorasATrabalhar(d.horasATrabalhar()==null?BigDecimal.ZERO:d.horasATrabalhar());
        f.setInativo(d.inativo()?'S':'N'); f.setDtUltAlt(OffsetDateTime.now());
        return toDTO(feriados.save(f));
    }

    @Transactional
    public void excluir(Long id){ feriados.deleteById(id); }

    private ResponseStatusException erro(String m){ return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, m); }
    private FeriadoDTO toDTO(Feriado f){ return new FeriadoDTO(f.getId(), f.getNome(), f.getAno(), f.getMes(),
        f.getDia(), f.getTipo(), f.getFilial(), f.getHorasATrabalhar(), f.getInativo()=='S'); }
}
