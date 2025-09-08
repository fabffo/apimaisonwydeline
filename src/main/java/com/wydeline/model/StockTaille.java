package com.wydeline.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "stock_taille",
    uniqueConstraints = @UniqueConstraint(name = "uk_stock_produit_taille_couleur", columnNames = {"produit_id","taille", "couleur"})
)
@Data @NoArgsConstructor @AllArgsConstructor
public class StockTaille {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) 
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @Column(name = "taille", nullable = false, length = 32)
    @JsonProperty("taille")    
    private String taille;

    @Column(name = "couleur", nullable = true, length = 32)
    @JsonProperty("couleur")    
    private String couleur;
    
    @Column(name = "quantite", nullable = false)
    private Integer quantite = 0;
    
}
