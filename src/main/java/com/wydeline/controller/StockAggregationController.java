// src/main/java/com/wydeline/controller/StockAggregationController.java
package com.wydeline.controller;

import com.wydeline.dto.ProduitDisponibleResumeDto;
import com.wydeline.service.StockQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@CrossOrigin
public class StockAggregationController {

    private final StockQueryService stockQueryService;

    /**
     * GET /api/stock/available-summary
     * Retourne, par produit, toutes les variantes (taille + couleur) dont quantity > 0.
     */
    @GetMapping("/available-summary")
    public ResponseEntity<List<ProduitDisponibleResumeDto>> getAvailableSummary() {
        return ResponseEntity.ok(stockQueryService.getAvailableSummary());
    }
}
