package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.AprovacaoResumo;
import com.triscal.timesheet.api.dto.PendenteDTO;
import com.triscal.timesheet.domain.model.*;
import com.triscal.timesheet.domain.service.CalculadoraBhe;
import com.triscal.timesheet.domain.service.Quinzena;
import com.triscal.timesheet.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Aprovação/rejeição de dias e aplicação do BHE na aprovação (regra crítica).
 * A aprovação pode ser feita na visão quinzenal, porém CADA DIA é aprovado
 * individualmente (cada dia gera seu próprio cálculo de BHE).
 */
@Service
public class AprovacaoService {

    private final TimesheetDiaRepository dias;
    private final TimesheetDiaTarefaRepository tarefas;
    private final FuncionarioRepository funcionarios;
    private final FeriadoRepository feriados;
    private final MovimentacaoSaldoBheRepository movimentos;
    private final ParamGeralRepository parametros;

    public AprovacaoService(TimesheetDiaRepository dias, TimesheetDiaTarefaRepository tarefas,
                            FuncionarioRepository funcionarios, FeriadoRepository feriados,
                            MovimentacaoSaldoBheRepository movimentos, ParamGeralRepository parametros) {
        this.dias = dias; this.tarefas = tarefas; this.funcionarios = funcionarios;
        this.feriados = feriados; this.movimentos = movimentos; this.parametros = parametros;
    }

    @Transactional(readOnly = true)
    public List<PendenteDTO> pendentes(int aprovadorId) {
        return dias.pendentesDoAprovador(aprovadorId);
    }

    /** Aprova todos os dias 'F' da quinzena do colaborador — cada dia individualmente (com BHE). */
    @Transactional
    public AprovacaoResumo aprovarQuinzena(int aprovadorId, int funcionarioId,
                                           int anoRef, int mesRef, int periodo) {
        var func = validarAprovador(aprovadorId, funcionarioId);
        int corte1 = paramInt("QUINZENA_CORTE_1", 1);
        int corte2 = paramInt("QUINZENA_CORTE_2", 16);
        int aprovados = 0, ignorados = 0;
        for (LocalDate data : Quinzena.datasDoPeriodo(anoRef, mesRef, periodo, corte1, corte2)) {
            var id = new TimesheetDiaId(funcionarioId,
                (short) data.getYear(), (short) data.getMonthValue(), (short) data.getDayOfMonth());
            var d = dias.findById(id).orElse(null);
            if (d != null && d.getStatus() == 'F') { aplicarAprovacao(func, d, data); aprovados++; }
            else ignorados++;
        }
        return new AprovacaoResumo(aprovados, ignorados);
    }

    @Transactional
    public void aprovarDia(int aprovadorId, int funcionarioId, int ano, int mes, int dia) {
        var func = validarAprovador(aprovadorId, funcionarioId);
        var d = dias.findById(new TimesheetDiaId(funcionarioId, (short)ano,(short)mes,(short)dia))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dia não encontrado."));
        if (d.getStatus() != 'F')
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Só é possível aprovar dias fechados (F).");
        aplicarAprovacao(func, d, LocalDate.of(ano, mes, dia));
    }

    @Transactional
    public void rejeitarDia(int aprovadorId, int funcionarioId, int ano, int mes, int dia, String observacao) {
        validarAprovador(aprovadorId, funcionarioId);
        var d = dias.findById(new TimesheetDiaId(funcionarioId, (short)ano,(short)mes,(short)dia))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dia não encontrado."));
        if (d.getStatus() != 'F')
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Só é possível rejeitar dias fechados (F).");
        if (observacao == null || observacao.isBlank())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Observação é obrigatória ao rejeitar.");
        d.setStatus('R'); d.setObs(observacao); d.setDtUltAlt(OffsetDateTime.now());
        dias.save(d);
    }

    // ----- internos -----

    private Funcionario validarAprovador(int aprovadorId, int funcionarioId) {
        if (aprovadorId == funcionarioId)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode aprovar/rejeitar o próprio log.");
        var func = funcionarios.findById(funcionarioId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));
        if (func.getAprovadorId() == null || !func.getAprovadorId().equals(aprovadorId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é o aprovador deste colaborador.");
        return func;
    }

    /** Aplica a aprovação de UM dia: calcula/credita BHE e marca 'P'. */
    private void aplicarAprovacao(Funcionario func, TimesheetDia d, LocalDate data) {
        BigDecimal extras = BigDecimal.ZERO;
        for (TimesheetDiaTarefa t : tarefas.doDia(func.getId(),
                (short) data.getYear(), (short) data.getMonthValue(), (short) data.getDayOfMonth()))
            extras = extras.add(t.getHorasNcExtra()).add(t.getHorasCoExtra());

        boolean domingoOuFeriado = data.getDayOfWeek().getValue() == 7
            || feriados.contarFeriado((short) data.getYear(), (short) data.getMonthValue(),
                                      (short) data.getDayOfMonth(), func.getCdEmpresa()) > 0;

        var res = CalculadoraBhe.calcular(func.getTipo(), func.possuiBancoHoras(),
            extras, d.getAusenciaBhe(), domingoOuFeriado);
        if (!res.vazio()) {
            movimentos.save(new MovimentacaoSaldoBhe(func.getId(), data, res.bhe50(), res.bhe100(),
                'N', (short) data.getYear(), (short) data.getMonthValue(), (short) data.getDayOfMonth(),
                "aprovacao"));
            func.creditarBhe50(res.bhe50());
            func.creditarBhe100(res.bhe100());
            funcionarios.save(func);
        }
        d.setStatus('P');
        d.setDtUltAlt(OffsetDateTime.now());
        dias.save(d);
    }

    private int paramInt(String nome, int padrao) {
        return parametros.findById(nome)
            .map(p -> { try { return Integer.parseInt(p.getValor().trim()); } catch (Exception e) { return padrao; } })
            .orElse(padrao);
    }
}
