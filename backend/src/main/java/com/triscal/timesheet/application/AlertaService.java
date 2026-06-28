package com.triscal.timesheet.application;

import com.triscal.timesheet.api.dto.InadimplenteDTO;
import com.triscal.timesheet.domain.model.Funcionario;
import com.triscal.timesheet.infrastructure.notificacao.NotificacaoService;
import com.triscal.timesheet.infrastructure.persistence.FuncionarioRepository;
import com.triscal.timesheet.infrastructure.persistence.TimesheetDiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Geração dos alertas (chamados pelos jobs agendados). */
@Service
public class AlertaService {

    private final FuncionarioRepository funcionarios;
    private final TimesheetDiaRepository dias;
    private final NotificacaoService notificacao;

    public AlertaService(FuncionarioRepository funcionarios, TimesheetDiaRepository dias,
                         NotificacaoService notificacao) {
        this.funcionarios = funcionarios; this.dias = dias; this.notificacao = notificacao;
    }

    /** Alerta diário: colaboradores que não preencheram o log do dia (ignora fim de semana). */
    @Transactional(readOnly = true)
    public int alertaDiarioNaoPreenchimento(LocalDate dia) {
        if (dia.getDayOfWeek().getValue() >= 6) return 0;
        List<Funcionario> pendentes = funcionarios.semLancamentoNoDia(
            (short) dia.getYear(), (short) dia.getMonthValue(), (short) dia.getDayOfMonth());
        for (Funcionario f : pendentes)
            notificacao.enviar(f.getEmail(), "Apontamento de horas pendente",
                "Olá, " + f.getNome() + ". Você ainda não preencheu o log de horas de "
                + dia + ". Acesse o sistema e registre seu dia.");
        return pendentes.size();
    }

    /** Alerta quinzenal: lembra cada aprovador de logs pendentes de aprovação. */
    @Transactional(readOnly = true)
    public int alertaQuinzenalAprovadores() {
        List<Funcionario> aprovadores = funcionarios.aprovadoresComPendencias();
        for (Funcionario apr : aprovadores) {
            int qtd = dias.pendentesDoAprovador(apr.getId()).size();
            notificacao.enviar(apr.getEmail(), "Aprovações de log pendentes",
                "Olá, " + apr.getNome() + ". Há " + qtd + " dia(s) de log aguardando sua aprovação.");
        }
        return aprovadores.size();
    }

    /** Alerta ao aprovador sobre subordinados com dias em aberto/atrasados (antes de hoje). */
    @Transactional(readOnly = true)
    public int alertaColaboradorInadimplente(LocalDate hoje) {
        int hojeNum = hoje.getYear() * 10000 + hoje.getMonthValue() * 100 + hoje.getDayOfMonth();
        List<InadimplenteDTO> linhas = funcionarios.subordinadosAtrasados(hojeNum);
        // agrupa por aprovador
        Map<Integer, List<InadimplenteDTO>> porAprovador = linhas.stream()
            .collect(Collectors.groupingBy(InadimplenteDTO::aprovadorId, LinkedHashMap::new, Collectors.toList()));
        for (var entrada : porAprovador.entrySet()) {
            var lista = entrada.getValue();
            var primeiro = lista.get(0);
            String corpo = lista.stream()
                .map(i -> "• " + i.colaboradorNome() + ": " + i.diasAtrasados() + " dia(s) em aberto")
                .collect(Collectors.joining("\n"));
            notificacao.enviar(primeiro.aprovadorEmail(),
                "Colaboradores com apontamento em atraso",
                "Olá, " + primeiro.aprovadorNome() + ". Os seguintes colaboradores têm dias de log em aberto:\n\n"
                + corpo + "\n\nPor favor, oriente o preenchimento.");
        }
        return porAprovador.size();
    }
}
