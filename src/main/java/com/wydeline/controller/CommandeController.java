// src/main/java/com/wydeline/controller/CommandeController.java
package com.wydeline.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;                          // ✅ bon import

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wydeline.dto.CreerCommandeRequest;
import com.wydeline.dto.CreerCommandeResponse;
import com.wydeline.model.Commande;
import com.wydeline.repository.CommandeRepository;   // ✅ l’ajouter
import com.wydeline.repository.StockTailleRepository;
import com.wydeline.service.CommandeErreurException;
import com.wydeline.service.CommandeService;
import com.wydeline.service.StockInsuffisantException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class CommandeController {

    private final CommandeService commandeService;
    private final StockTailleRepository stockTailleRepository;
    private final CommandeRepository commandeRepository; // ✅ injecté

    @PostMapping("/commandes")
    public ResponseEntity<CreerCommandeResponse> create(@RequestBody CreerCommandeRequest body) {
        Commande c = commandeService.createAndReserve(body);
        return ResponseEntity.ok(new CreerCommandeResponse(
                c.getId(), c.getStatus().name(), c.getMontantTotal()
        ));
    }

    // Stock d’une variante (product + size + color|NULL)
    @GetMapping("/stock/variant")
    public ResponseEntity<Integer> getVariantStock(
            @RequestParam Long produitId,
            @RequestParam String taille,
            @RequestParam(required = false) String couleur) {

        var opt = stockTailleRepository.findVariant(
                produitId,
                taille,
                (couleur == null || couleur.isBlank()) ? null : couleur.trim()
        );
        return ResponseEntity.ok(opt.map(st -> st.getQuantite()).orElse(0));
    }

    // Ajustement admin d’une variante
    @Transactional
    @PostMapping("/stock/variant/ajuster")
    public ResponseEntity<Integer> adjustVariant(
            @RequestParam Long produitId,
            @RequestParam String taille,
            @RequestParam(required = false) String couleur,
            @RequestParam int delta) {

        var variant = stockTailleRepository.lockVariant(
                produitId,
                taille,
                (couleur == null || couleur.isBlank()) ? null : couleur.trim()
        ).orElseThrow(() -> new CommandeErreurException("Variante inexistante"));

        variant.setQuantite((variant.getQuantite() == null ? 0 : variant.getQuantite()) + delta);
        return ResponseEntity.ok(variant.getQuantite());
    }

    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<String> handleStock(StockInsuffisantException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CommandeErreurException.class)
    public ResponseEntity<String> handleBad(CommandeErreurException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // ✅ récupérer une commande
    @GetMapping("/commande/{id}")
    public ResponseEntity<Commande> getOne(@PathVariable Long id) {
        return ResponseEntity.of(commandeRepository.findById(id));
    }

    // ✅ téléchargement facture PDF
    @GetMapping(value = "/commande/{id}/facture", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) throws IOException {
        var cmd = commandeRepository.findById(id).orElseThrow();
        if (cmd.getCheminFacture() == null || cmd.getCheminFacture().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = Files.readAllBytes(Path.of(cmd.getCheminFacture())); // ✅ Path du JDK
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture-" + id + ".pdf")
                .body(bytes);
    }
}
