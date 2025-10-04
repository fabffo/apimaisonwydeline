package com.wydeline.dto;

import java.util.List;

public class ResumeProduit {
	private Long id;
    private String nom;
    private Integer prix;
    private List<String> tailles;

    public ResumeProduit(Long id, String nom, Integer prix, List<String> tailles) {
        this.id = id; this.nom = nom; this.prix = prix; this.tailles = tailles;
    }
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public Integer getPrix() { return prix; }
    public List<String> getTailles() { return tailles; }
}
