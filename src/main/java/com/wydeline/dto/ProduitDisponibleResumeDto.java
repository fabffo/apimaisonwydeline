// src/main/java/com/wydeline/dto/AvailableProductSummaryDto.java
package com.wydeline.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ProduitDisponibleResumeDto {
    private Long produitId;
    private String name;
    private String imageUrl;
    private boolean precommande;
    private boolean optionCouleur;
    private List<VariantDisponibleDto> variants; // liste des variantes en stock
}
