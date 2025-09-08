// src/main/java/com/wydeline/service/PaymentService.java
package com.wydeline.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.wydeline.model.Commande;
import com.wydeline.model.CommandeStatus;
import com.wydeline.repository.CommandeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaiementService {

  @Value("${stripe.apiKey}") private String apiKey;
  @Value("${app.front.success-url}") private String successUrl;
  @Value("${app.front.cancel-url}")  private String cancelUrl;

  private final CommandeRepository commandeRepository;

  /** Crée une session Checkout et renvoie url + sessionId */
  public Map<String,String> createCheckoutSession(Long orderId) throws Exception {
    Stripe.apiKey = apiKey;

    Commande cmd = commandeRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Commande introuvable: " + orderId));

    List<SessionCreateParams.LineItem> lineItems = cmd.getLignes().stream().map(lc ->
      SessionCreateParams.LineItem.builder()
        .setQuantity(lc.getQuantite().longValue())
        .setPriceData(
          SessionCreateParams.LineItem.PriceData.builder()
            .setCurrency("eur")
            .setUnitAmount(lc.getPrixUnitaire().multiply(new BigDecimal("100")).longValue()) // en centimes
            .setProductData(
              SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(lc.getProduit().getNom()
                  + (lc.getTaille()!=null ? " T" + lc.getTaille(): "")
                  + (lc.getCouleur()!=null ? " " + lc.getCouleur(): ""))
                .build()
            )
            .build()
        )
        .build()
    ).toList();

    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .addAllLineItem(lineItems)
        .setSuccessUrl(successUrl + "?orderId=" + orderId + "&session_id={CHECKOUT_SESSION_ID}")
        .setCancelUrl(cancelUrl + "?orderId=" + orderId)
        .putMetadata("orderId", String.valueOf(orderId))
        .build();

    Session session = Session.create(params);

    if (cmd.getStatus() == null) cmd.setStatus(CommandeStatus.CREE);
    commandeRepository.save(cmd);

    Map<String,String> out = new HashMap<>();
    out.put("sessionId", session.getId());
    out.put("url", session.getUrl()); // <— ce lien ouvre le Checkout Stripe
    return out;
  }
}
