package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.MovimentoBheDTO;
import com.triscal.timesheet.domain.model.MovimentacaoSaldoBhe;
import com.triscal.timesheet.infrastructure.persistence.FuncionarioRepository;
import com.triscal.timesheet.infrastructure.persistence.MovimentacaoSaldoBheRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Movimentação manual de BHE (RH): registra o movimento e ajusta o saldo do funcionário. */
@Service
public class BheManualService {
    private final FuncionarioRepository funcionarios;
    private final MovimentacaoSaldoBheRepository movimentos;
    public BheManualService(FuncionarioRepository f, MovimentacaoSaldoBheRepository m){ this.funcionarios=f; this.movimentos=m; }

    @Transactional
    public void registrar(MovimentoBheDTO d, String usuario){
        if (d.cdFuncionario()==null) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Funcionário é obrigatório.");
        BigDecimal v50 = d.valor50()==null?BigDecimal.ZERO:d.valor50();
        BigDecimal v100 = d.valor100()==null?BigDecimal.ZERO:d.valor100();
        if (v50.signum()==0 && v100.signum()==0) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Informe um valor para 50% ou 100%.");
        var func = funcionarios.findById(d.cdFuncionario()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));
        LocalDate data = d.data()==null?LocalDate.now():d.data();
        movimentos.save(new MovimentacaoSaldoBhe(func.getId(), data, v50, v100, 'S',
            (short)data.getYear(), (short)data.getMonthValue(), (short)data.getDayOfMonth(), usuario));
        func.creditarBhe50(v50); func.creditarBhe100(v100);
        funcionarios.save(func);
    }
}
