package com.wydeline.dto;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreerCommandeRequest {
	String emailClient;
    @NotEmpty
    private List<Item> articles;

    @NotNull(message="Les informations de livraison sont requises")
    @Valid
    private Livraison livraison;  // <-- FR

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Item {
        @NotNull
        private Long produitId;

        @NotNull @Min(1)
        private Integer quantite;

        @NotBlank
        private String taille;   // ← OBLIGATOIRE maintenant

        private String couleur;  // optionnel pour l’info commande
    	}
    
    @Data
    public static class Livraison {
      @NotBlank(message="Le pays est requis")       private String pays;
      @NotBlank(message="Le prénom est requis")     private String prenom;
      @NotBlank(message="Le nom est requis")        private String nom;
                                                    private String entreprise;
      @NotBlank(message="L'adresse est requise")    private String adresse;
                                                    private String complement;
      @NotBlank(message="Le code postal est requis")private String codePostal;
      @NotBlank(message="La ville est requise")     private String ville;

      @NotBlank(message="Le téléphone est requis")
      @Pattern(regexp="^[0-9+().\\-\\s]{6,}$", message="Téléphone invalide")
      private String telephone;
    }
    
}
