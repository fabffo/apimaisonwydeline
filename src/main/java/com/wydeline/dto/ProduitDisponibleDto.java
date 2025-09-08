package com.wydeline.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDisponibleDto {
    private Long produitId;
    private List<VariantDto> variants;
}
