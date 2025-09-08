// src/main/java/com/wydeline/dto/AvailableVariantDto.java
package com.wydeline.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class VariantDisponibleDto {
    private String taille;     // "37"
    private String couleur;    // "Noir" ou null si pas de couleur
    private Integer quantite; // > 0
}
