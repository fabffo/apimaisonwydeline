package com.wydeline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantDto {
    private String taille;
    private String couleur;
    private Integer quantite;
}
