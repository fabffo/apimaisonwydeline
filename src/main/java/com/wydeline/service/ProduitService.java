package com.wydeline.service;

import java.util.List;

import com.wydeline.dto.ResumeProduit;
import com.wydeline.model.Produit;

public interface ProduitService {
    /** Renvoie jusqu’à 8 produits à mettre en avant pour la collection */
    List<ResumeProduit> findTop8Disponibles();
    ResumeProduit resumeFromProduit(Produit produit); // ⬅️ NOUVEAU
}
