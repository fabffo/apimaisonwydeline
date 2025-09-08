// src/main/java/com/wydeline/controller/StripeWebhookController.java
package com.wydeline.controller;

import org.springframework.beans.factory.annotation.Value;             // ✅ Spring @Value
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;                                      // ✅ Stripe Event
import com.stripe.model.checkout.Session;                           // ✅ Checkout Session
import com.stripe.net.Webhook;                                      // ✅ Webhook util

import com.wydeline.model.CommandeStatus;
import com.wydeline.repository.CommandeRepository;
import com.wydeline.service.ExpeditionService;
import com.wydeline.service.FactureService;                         // ✅ Ton service facture
import com.wydeline.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

  @Value("${stripe.webhook.secret}") private String endpointSecret;

  private final CommandeRepository commandeRepo;
  private final com.wydeline.service.FactureService invoiceService; // <- ton service facture maison
  private final MailService mailService;
  private final ExpeditionService expeditionService;

  private final ObjectMapper mapper = new ObjectMapper(); // pour fallback JSON

  @PostMapping("/webhook")
  public ResponseEntity<String> handle(@RequestBody String payload,
                                       @RequestHeader("Stripe-Signature") String sigHeader) {
	  log.info("Webhook Stripe reçu. Signature présente ? {}", sigHeader != null);
    Event event;
    try {
      event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
    } catch (com.stripe.exception.SignatureVerificationException e) {
      log.warn("Signature Stripe invalide: {}", e.getMessage());
      return ResponseEntity.status(400).body("Bad signature");
    }

    log.info("Stripe event type: {}", event.getType());

    if ("checkout.session.completed".equals(event.getType())) {
      var deser = event.getDataObjectDeserializer();

      // 1) Essai de désérialisation typée
      if (deser.getObject().isPresent()) {
        var stripeObj = deser.getObject().get();
        if (stripeObj instanceof Session session) {
          processCompletedSession(session);
        } else {
          log.warn("Objet inattendu pour checkout.session.completed: {}", stripeObj.getClass());
          // Fallback JSON
          fallbackProcessFromJson(payload);
        }
      } else {
        // 2) Fallback JSON + log de la raison
    	  if (deser.getObject().isPresent()) {
    		    // ... traitement normal
    		} else {
    		    // Certaines versions n'exposent pas la raison → on loggue simple
    		    log.warn("Impossible de désérialiser l'objet de l'événement {}. Fallback JSON.",
    		            event.getType());
    		    fallbackProcessFromJson(payload); // ← ta méthode de secours
    		}

        fallbackProcessFromJson(payload);
      }
    }

    return ResponseEntity.ok("ok");
  }

  private void fallbackProcessFromJson(String payload) {
    try {
      JsonNode root = mapper.readTree(payload);
      JsonNode obj = root.path("data").path("object");
      String sessionId = obj.path("id").asText(null);
      String orderIdStr = obj.path("metadata").path("orderId").asText(null);

      log.info("Fallback JSON – sessionId={}, orderId={}", sessionId, orderIdStr);

      if (orderIdStr == null) {
        log.error("Pas de metadata.orderId dans le webhook.");
        return;
      }
      Long orderId = Long.valueOf(orderIdStr);

      // Ici on n’a pas absolument besoin de re-fetch la session Stripe si tout est dans metadata.
      // Mais si tu veux, tu peux: Session retrieved = Session.retrieve(sessionId);

      finalizeOrder(orderId);

    } catch (Exception e) {
      log.error("Erreur fallback JSON webhook", e);
    }
  }

  private void processCompletedSession(Session session) {
    try {
      String orderIdStr = session.getMetadata().get("orderId");
      if (orderIdStr == null) {
        log.error("checkout.session.completed sans metadata.orderId");
        return;
      }
      Long orderId = Long.valueOf(orderIdStr);
      finalizeOrder(orderId);
    } catch (Exception e) {
      log.error("Erreur traitement Session Stripe", e);
    }
  }

  private void finalizeOrder(Long orderId) {
	  var cmd = commandeRepo.findById(orderId).orElseThrow();

	  if (CommandeStatus.PAYEE.equals(cmd.getStatus())) {
	    log.info("Commande {} déjà PAYEE, skip finalisation.", orderId);
	    return;
	  }

	  log.info("Finalisation commande {} : génération facture…", orderId);
	  byte[] pdf = invoiceService.renderInvoicePdf(cmd);
	  var path = invoiceService.store(orderId, pdf);

	  cmd.setCheminFacture(path.toString());
	  cmd.setNumeroFacture("WYD-" + java.time.LocalDate.now().getYear() + "-" + orderId);
	  cmd.setStatus(CommandeStatus.PAYEE);
	  commandeRepo.save(cmd);
	  log.info("Commande {} : facture OK ({})", orderId, path);

	  expeditionService.createIfAbsent(cmd);

	// juste avant l’envoi
	  String to = cmd.getEmailClient();
	  if (to != null) to = to.trim();

	  if (to == null || to.isBlank()) {
	    log.warn("Email client absent → pas d'envoi de facture pour orderId={}", orderId);
	  } else {
	    try {
	      log.info("Tentative d'envoi facture: orderId={}, to={}, filename=facture-{}.pdf, bytes={}",
	          orderId, to, orderId, (pdf==null?0:pdf.length));
	      mailService.sendInvoice(to, pdf, "facture-" + orderId + ".pdf");
	      log.info("Facture envoyée avec succès: orderId={}, to={}", orderId, to);
	    } catch (Exception e) {
	      log.error("Échec envoi facture: orderId={}, to={}, cause={}", orderId, to, e.toString(), e);
	    }
	  }
	  log.info("Commande {} finalisée: statut=PAYEE, expédition OK.", orderId);

	}

}