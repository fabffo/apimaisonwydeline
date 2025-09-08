package com.wydeline.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value; // ✅ @Value Spring

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
  private final JavaMailSender mailSender;
  @Value("${spring.mail.username:}") private String from;

  public void sendInvoice(String to, byte[] pdf, String filename) {
    log.info("MailService.sendInvoice → from={}, to={}, att={} ({})",
        from, to, filename, (pdf==null?0:pdf.length)+" bytes");

    try {
      MimeMessage msg = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject("Votre facture Maison Wydeline");
      helper.setText("Bonjour,\n\nVeuillez trouver votre facture en pièce jointe.\n\nCordialement.", false);

      if (pdf != null && pdf.length > 0) {
        helper.addAttachment(filename, new ByteArrayResource(pdf));
      } else {
        log.warn("Avertissement: PDF nul ou vide, envoi sans pièce jointe.");
      }

      mailSender.send(msg);
      log.info("Mail envoyé (SMTP OK) → to={}", to);
    } catch (Exception e) {
      log.error("MailService → échec d'envoi (to={}, from={}): {}", to, from, e.toString(), e);
      throw new RuntimeException("Échec envoi mail", e);
    }
  }
}
