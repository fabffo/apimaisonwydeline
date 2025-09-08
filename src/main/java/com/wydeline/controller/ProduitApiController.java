package com.wydeline.controller;

import com.wydeline.model.Produit;
import com.wydeline.repository.ProduitRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produits")
public class ProduitApiController {

    private final ProduitRepository repository;

    // ✅ Injection de dépendances
    public ProduitApiController(ProduitRepository repository) {
        this.repository = repository;
    }

    // 🔹 GET : tous les produits
    @GetMapping
    public List<Produit> all() {
        return repository.findAll();
    }

    // 🔹 GET : un produit par ID
    @GetMapping("/{id}")
    public Optional<Produit> one(@PathVariable Long id) {
        return repository.findById(id);
    }

    // 🔹 POST : créer un nouveau produit
    @PostMapping
    public Produit create(@RequestBody Produit produit) {
        return repository.save(produit);
    }

    // 🔹 PUT : mettre à jour un produit existant
    @PutMapping("/{id}")
    public Produit update(@PathVariable Long id, @RequestBody Produit updatedProduit) {
        return repository.findById(id)
                .map(prod -> {
                    prod.setNom(updatedProduit.getNom());
                    prod.setPrix(updatedProduit.getPrix());
                    prod.setDescription(updatedProduit.getDescription());
                    prod.setImageUrl(updatedProduit.getImageUrl());
                    prod.setPrecommande(updatedProduit.isPrecommande());
                    prod.setOptionCouleur(updatedProduit.isOptionCouleur());
                    return repository.save(prod);
                })
                .orElseGet(() -> {
                    updatedProduit.setId(id);
                    return repository.save(updatedProduit);
                });
    }

    // 🔹 DELETE : supprimer un produit
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
