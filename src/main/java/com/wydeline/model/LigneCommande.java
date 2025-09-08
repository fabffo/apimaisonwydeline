package com.wydeline.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ligne_commande")
@Data @NoArgsConstructor @AllArgsConstructor
public class LigneCommande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "commande_id") 
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Commande commande;

    @ManyToOne(optional = false) @JoinColumn(name = "produit_id")
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    // Optionnel pour ton cas d’usage
    private String taille;
    private String couleur;
}
