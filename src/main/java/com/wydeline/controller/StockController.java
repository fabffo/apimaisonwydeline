// src/main/java/com/wydeline/controller/StockController.java
package com.wydeline.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wydeline.dto.StockResumeArticle;
import com.wydeline.dto.StockResumeResponse;
import com.wydeline.dto.VariantDto;
import com.wydeline.model.Produit;
import com.wydeline.model.StockTaille;
import com.wydeline.repository.ProduitRepository;
import com.wydeline.repository.StockTailleRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockTailleRepository stockRepo;
    private final ProduitRepository productRepository;

    // ========= Endpoints déjà existants (garde-les si utiles) =========

    // 1 produit
    @GetMapping("/resume")
    public StockResumeResponse resume(@RequestParam Long produitId) {
        var rows = stockRepo.findByProduit_Id(produitId);
        var items = rows.stream()
                .map(st -> new StockResumeArticle(st.getTaille(), st.getCouleur(), st.getQuantite()))
                .toList();
        return new StockResumeResponse(produitId, items);
    }

    // Plusieurs produits d’un coup: /api/stock/summary/batch?productId=1&productId=2...
    @GetMapping("/resume/batch")
    public List<StockResumeResponse> resumeBatch(@RequestParam("produitId") List<Long> produitIds) {
        var rows = stockRepo.findByProduit_IdIn(produitIds);
        Map<Long, List<StockResumeArticle>> map = new HashMap<>();
        for (var st : rows) {
            map.computeIfAbsent(st.getProduit().getId(), k -> new ArrayList<>())
               .add(new StockResumeArticle(st.getTaille(), st.getCouleur(), st.getQuantite()));
        }
        return produitIds.stream()
                .map(pid -> new StockResumeResponse(pid, map.getOrDefault(pid, List.of())))
                .toList();
    }

    // IDs des produits disponibles (≥1 variante avec stock > 0)
    @GetMapping("/produits-disponibles/ids")
    public ResponseEntity<List<Long>> getAvailableProductIds() {
        return ResponseEntity.ok(stockRepo.findAllProduitIdsWithStock());
    }

    // Produits complets disponibles (filtrés par stock > 0)
    @GetMapping("/produits-disponibles")
    public ResponseEntity<List<Produit>> getAvailableProducts() {
        var ids = stockRepo.findAllProduitIdsWithStock();
        if (ids.isEmpty()) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(productRepository.findByIdIn(ids));
    }

    // Stats : nb de variantes en stock par produit
    @GetMapping("/produits-disponibles/stats")
    public ResponseEntity<List<Map<String,Object>>> getAvailableProductsStats() {
        var rows = stockRepo.countVariantsWithStockByProduit();
        var out = new ArrayList<Map<String,Object>>();
        for (Object[] r : rows) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("productId", (Long) r[0]);
            m.put("variantsInStock", ((Number) r[1]).intValue());
            out.add(m);
        }
        return ResponseEntity.ok(out);
    }

    // ========= Endpoints attendus par la vitrine =========

    // A) Variantes disponibles pour un produit => List<VariantDto>
    @GetMapping("/disponible-par-produit/{productId}")
    public ResponseEntity<List<VariantDto>> availableByProduct(@PathVariable Long productId) {
        // On renvoie TOUTES les variantes; la vitrine pourra filtrer quantity>0,
        // ou on filtre ici (choix).
        List<VariantDto> variants = stockRepo.findByProduit_Id(productId).stream()
                .map(this::toVariantDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(variants);
    }

    // ========= Helpers =========

    private VariantDto toVariantDto(StockTaille st) {
        VariantDto v = new VariantDto();
        v.setTaille(st.getTaille());
        v.setCouleur(st.getCouleur());
        v.setQuantite(st.getQuantite());
        return v;
    }
}
