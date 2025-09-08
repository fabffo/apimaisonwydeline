package com.wydeline.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Embeddable
public class AdresseLivraison {
  @NotBlank private String pays;
  @NotBlank private String prenom;
  @NotBlank private String nom;
             private String entreprise;
  @NotBlank private String adresse;
             private String complement;
  @NotBlank private String codePostal;
  @NotBlank private String ville;
  @NotBlank private String telephone;
}
