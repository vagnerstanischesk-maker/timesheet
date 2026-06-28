package com.triscal.timesheet.scheduler;

import com.triscal.timesheet.application.AlertaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Jobs de alerta (Spring Scheduler in-process — sem broker). Crons parametrizáveis. */
@Component
public class AlertasScheduler {

    private final AlertaService alertas;

    public AlertasScheduler(AlertaService alertas) { this.alertas = alertas; }

    /** Dia útil 17h: avisa quem não preencheu o log do dia. */
    @Scheduled(cron = "${app.alertas.diario-cron:0 0 17 * * MON-FRI}", zone = "America/Sao_Paulo")
    public void diarioNaoPreenchimento() { alertas.alertaDiarioNaoPreenchimento(LocalDate.now()); }

    /** Dias 11 e 26 às 9h: lembra aprovadores das pendências. */
    @Scheduled(cron = "${app.alertas.quinzenal-cron:0 0 9 11,26 * *}", zone = "America/Sao_Paulo")
    public void quinzenalAprovadores() { alertas.alertaQuinzenalAprovadores(); }

    /** Toda 2ª-feira às 8h: avisa aprovadores sobre subordinados com dias em atraso. */
    @Scheduled(cron = "${app.alertas.inadimplente-cron:0 0 8 * * MON}", zone = "America/Sao_Paulo")
    public void colaboradorInadimplente() { alertas.alertaColaboradorInadimplente(LocalDate.now()); }
}
