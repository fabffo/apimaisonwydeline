// src/main/java/com/wydeline/controller/PaymentController.java
package com.wydeline.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wydeline.service.PaiementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaiementController {

  private final PaiementService paymentService;

  /**
   * Lance un Checkout Session Stripe pour la commande.
   * Renvoie l’URL de redirection (et l’id de session, pratique pour debug).
   */
  @PostMapping("/checkout")
  public ResponseEntity<Map<String, String>> checkout(@RequestParam Long orderId) throws Exception {
    // adapte ton service pour renvoyer directement l’URL ET l’id
    var res = paymentService.createCheckoutSession(orderId); // voir section 2 ci-dessous
    return ResponseEntity.ok(res);
  }
}
