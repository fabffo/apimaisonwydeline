// src/main/java/com/wydeline/service/StripeWebhookService.java
package com.wydeline.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wydeline.model.Commande;
import com.wydeline.model.CommandeStatus;
import com.wydeline.model.PaiementLog;
import com.wydeline.repository.CommandeRepository;
import com.wydeline.repository.PaiementLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private final PaiementLogRepository paiementLogRepo;
    private final CommandeRepository commandeRepo;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret; // si tu veux vérifier la signature côté contrôleur

    public void handleEvent(String eventId, String payload) throws Exception {
        // Idempotence
        if (paiementLogRepo.findByStripeEventId(eventId).isPresent()) {
            log.info("Stripe webhook déjà traité eventId={}", eventId);
            return;
        }

        JsonNode root = om.readTree(payload);
        String type = text(root, "type");
        boolean livemode = bool(root, "livemode", false);

        JsonNode dataObj = root.path("data").path("object");
        String object = text(dataObj, "object"); // checkout.session, payment_intent, charge…

        Long orderId = extractOrderId(dataObj);
        String sessionId = text(dataObj, "id");
        String paymentIntentId = text(dataObj, "payment_intent");
        String status = text(dataObj, "status");
        Long amount = dataObj.has("amount_total") ? longVal(dataObj, "amount_total")
                       : dataObj.has("amount") ? longVal(dataObj, "amount")
                       : null;
        String currency = text(dataObj, "currency");
        String email = firstNonEmpty(
                text(dataObj, "customer_details", "email"),
                text(dataObj, "receipt_email"),
                text(dataObj, "customer_email")
        );
        OffsetDateTime createdAtStripe = toOdt(root.path("created").asLong(0));

        PaiementLog logRow = PaiementLog.builder()
                .stripeEventId(eventId)
                .eventType(type)
                .livemode(livemode)
                .orderId(orderId)
                .sessionId(sessionId)
                .paymentIntentId(paymentIntentId)
                .status(status)
                .amount(amount)
                .currency(currency)
                .customerEmail(email)
                .createdAtStripe(createdAtStripe)
                .receivedAtApp(OffsetDateTime.now(ZoneOffset.UTC))
                .payloadJson(payload)
                .processedOk(false)
                .build();

        // Met à jour la commande si paiement confirmé
        try {
            boolean ok = maybeUpdateOrder(type, object, status, orderId);
            logRow.setProcessedOk(ok);
            logRow.setProcessedMessage(ok ? "Commande mise à jour" : "Aucune MAJ commande");
        } catch (Exception ex) {
            log.warn("Echec MAJ commande depuis webhook: {}", ex.getMessage(), ex);
            logRow.setProcessedOk(false);
            logRow.setProcessedMessage(ex.getMessage());
        }

        paiementLogRepo.save(logRow);
    }

    private boolean maybeUpdateOrder(String eventType, String object, String status, Long orderId) {
        if (orderId == null) return false;

        // Cas “confirmé”
        boolean paid =
                ("payment_intent.succeeded".equals(eventType))
                || ("checkout.session.completed".equals(eventType)
                    && ("complete".equalsIgnoreCase(status) || "paid".equalsIgnoreCase(status)));

        if (!paid) return false;

        Optional<Commande> opt = commandeRepo.findById(orderId);
        if (opt.isEmpty()) return false;

        Commande cmd = opt.get();
        cmd.setStatus(CommandeStatus.PAYEE);
        commandeRepo.save(cmd);
        return true;
    }

    private Long extractOrderId(JsonNode obj) {
        // 1) metadata.orderId
        if (obj.has("metadata") && obj.path("metadata").has("orderId")) {
            try { return Long.valueOf(obj.path("metadata").path("orderId").asText()); }
            catch (NumberFormatException ignored) {}
        }
        // 2) client_reference_id
        if (obj.has("client_reference_id")) {
            try { return Long.valueOf(obj.path("client_reference_id").asText()); }
            catch (NumberFormatException ignored) {}
        }
        // 3) payment_intent -> metadata.orderId
        if (obj.has("payment_intent") && obj.path("payment_intent").isObject()) {
            JsonNode pi = obj.path("payment_intent");
            if (pi.has("metadata") && pi.path("metadata").has("orderId")) {
                try { return Long.valueOf(pi.path("metadata").path("orderId").asText()); }
                catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    private String text(JsonNode node, String... path) {
        JsonNode cur = node;
        for (String p : path) cur = cur.path(p);
        return cur.isMissingNode() || cur.isNull() ? null : cur.asText();
    }
    private boolean bool(JsonNode node, String field, boolean dft) {
        return node.has(field) ? node.get(field).asBoolean(dft) : dft;
    }
    private Long longVal(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asLong() : null;
    }
    private OffsetDateTime toOdt(long epochSeconds) {
        if (epochSeconds <= 0) return null;
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
    }
    private String firstNonEmpty(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
