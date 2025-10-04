// src/main/java/com/wydeline/model/PaiementLog.java
package com.wydeline.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "paiement_log", uniqueConstraints = {
    @UniqueConstraint(name = "uk_stripe_event_id", columnNames = "stripe_event_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaiementLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stripe_event_id", length = 100, nullable = false)
    private String stripeEventId;

    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;

    @Column(name = "livemode", nullable = false)
    private boolean livemode;

    @Column(name = "order_id")
    private Long orderId;                // metadata.orderId (si présent)

    @Column(name = "session_id", length = 120)
    private String sessionId;            // checkout.session.id

    @Column(name = "payment_intent_id", length = 120)
    private String paymentIntentId;      // pi_xxx

    @Column(name = "status", length = 60)
    private String status;               // succeeded, canceled, etc.

    @Column(name = "amount")
    private Long amount;                 // en cents

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "customer_email", length = 180)
    private String customerEmail;

    @Column(name = "created_at_stripe")
    private OffsetDateTime createdAtStripe;

    @Column(name = "received_at_app", nullable = false)
    private OffsetDateTime receivedAtApp;

    @Lob
    @Column(name = "payload_json", columnDefinition = "CLOB")
    private String payloadJson;          // corps brut de l’événement

    @Column(name = "processed_ok")
    private Boolean processedOk;         // true si on a mis à jour la commande

    @Column(name = "processed_message", length = 500)
    private String processedMessage;     // note/debug
}
