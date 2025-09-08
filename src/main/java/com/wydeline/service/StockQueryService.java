// src/main/java/com/wydeline/service/StockQueryService.java
package com.wydeline.service;

import com.wydeline.dto.*;
import com.wydeline.model.StockTaille;
import com.wydeline.repository.StockTailleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockQueryService {

    private final StockTailleRepository stockTailleRepository;

    public List<ProduitDisponibleResumeDto> getAvailableSummary() {
        List<StockTaille> rows = stockTailleRepository.findAllAvailable();

        // Group by product
        Map<Long, List<StockTaille>> byProduit = rows.stream()
                .collect(Collectors.groupingBy(st -> st.getProduit().getId()));

        List<ProduitDisponibleResumeDto> result = new ArrayList<>();

        for (Map.Entry<Long, List<StockTaille>> e : byProduit.entrySet()) {
            var first = e.getValue().get(0).getProduit();

            List<VariantDisponibleDto> variants = e.getValue().stream()
                    .sorted(Comparator.comparing(StockTaille::getTaille)
                            .thenComparing(st -> Optional.ofNullable(st.getCouleur()).orElse("")))
                    .map(st -> new VariantDisponibleDto(
                            st.getTaille(),
                            st.getCouleur(),                 // peut être null si produit sans couleurs
                            Optional.ofNullable(st.getQuantite()).orElse(0)
                    ))
                    .toList();

            result.add(new ProduitDisponibleResumeDto(
                    first.getId(),
                    first.getNom(),
                    first.getImageUrl(),
                    first.isPrecommande(),
                    first.isOptionCouleur(),
                    variants
            ));
        }

        // (facultatif) trier par nom de produit
        result.sort(Comparator.comparing(ProduitDisponibleResumeDto::getName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }
}
