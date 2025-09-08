package com.wydeline.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
//com.wydeline.model.Expedition
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Expedition {

@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@OneToOne(optional = false)
@JoinColumn(name = "commande_id", unique = true)
private Commande commande;

@Column(nullable = false)
private OffsetDateTime createdAt = OffsetDateTime.now();

@OneToMany(mappedBy = "expedition", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ExpeditionLigne> lignes = new ArrayList<>();
}
