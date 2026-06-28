package com.triscal.timesheet.infrastructure.notificacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de e-mail por SMTP (protocolo aberto). Se o JavaMailSender não estiver
 * configurado (sem SMTP em dev), apenas registra em log — sem quebrar a aplicação.
 */
@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);
    private final ObjectProvider<JavaMailSender> mailSender;

    public NotificacaoService(ObjectProvider<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    public void enviar(String para, String assunto, String corpo) {
        if (para == null || para.isBlank()) return;
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.info("[ALERTA: SMTP não configurado] para={} assunto={}", para, assunto);
            return;
        }
        var msg = new SimpleMailMessage();
        msg.setTo(para);
        msg.setSubject(assunto);
        msg.setText(corpo);
        sender.send(msg);
        log.info("Alerta enviado para {} ({})", para, assunto);
    }
}
