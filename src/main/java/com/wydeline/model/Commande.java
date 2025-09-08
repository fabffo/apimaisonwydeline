package com.wydeline.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commande")
@Data @NoArgsConstructor @AllArgsConstructor
public class Commande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandeStatus status = CommandeStatus.CREE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignes = new ArrayList<>();
    
    
 // champs supplémentaires
    private String emailClient;      // pour envoyer la facture
    
    @Embedded
    @AttributeOverrides({
      @AttributeOverride(name="pays",       column=@Column(name="shipping_pays")),
      @AttributeOverride(name="prenom",     column=@Column(name="shipping_prenom")),
      @AttributeOverride(name="nom",        column=@Column(name="shipping_nom")),
      @AttributeOverride(name="entreprise", column=@Column(name="shipping_entreprise")),
      @AttributeOverride(name="adresse",    column=@Column(name="shipping_adresse")),
      @AttributeOverride(name="complement", column=@Column(name="shipping_complement")),
      @AttributeOverride(name="codePostal", column=@Column(name="shipping_code_postal")),
      @AttributeOverride(name="ville",      column=@Column(name="shipping_ville")),
      @AttributeOverride(name="telephone",  column=@Column(name="shipping_telephone"))
    })
    private AdresseLivraison livraison;

    
    private String numeroFacture;      // ex: WYD-2025-000123
    private String cheminFacture;        // chemin du PDF écrit sur disque

}
