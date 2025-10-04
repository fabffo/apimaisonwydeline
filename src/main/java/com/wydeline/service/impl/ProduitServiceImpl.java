package com.wydeline.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wydeline.dto.ResumeProduit;
import com.wydeline.model.Produit;
import com.wydeline.repository.ProduitRepository;
import com.wydeline.repository.StockTailleRepository;
import com.wydeline.service.ProduitService;

@Service
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final StockTailleRepository stockTailleRepository;

    public ProduitServiceImpl(ProduitRepository produitRepository,
                              StockTailleRepository stockTailleRepository) {
        this.produitRepository = produitRepository;
        this.stockTailleRepository = stockTailleRepository;
    }

    @Override
    public List<ResumeProduit> findTop8Disponibles() {
        // Ici, on fait simple : on prend les 8 premiers produits (adapte à ton critère réel)
        List<Produit> produits = produitRepository.findAll()
                                                  .stream()
                                                  .limit(8)
                                                  .toList();

        List<ResumeProduit> out = new ArrayList<>();
        for (Produit p : produits) {
            List<String> tailles = stockTailleRepository.findTaillesEnStockByProduitId(p.getId());

            // Conversion prix → Integer (en €) : adapte selon ton type et logique
            Integer prixEuros = null;
            try {
                // Ton Produit a un float `prix` (d’après le log Hibernate). On arrondit à l’euro.
                prixEuros = Math.round(p.getPrix());
            } catch (Exception ignore) {
                // si tu passes à BigDecimal dans l’entity, utilise :
                // prixEuros = (p.getPrix() != null) ? p.getPrix().setScale(0, RoundingMode.HALF_UP).intValue() : null;
            }

            out.add(new ResumeProduit(
                p.getId(),
                p.getNom(),
                prixEuros,
                tailles
            ));
        }
        return out;
    }
    
    @Override
    public ResumeProduit resumeFromProduit(Produit produit) {
        if (produit == null) {
            return null;
        }

        // On récupère les tailles disponibles pour ce produit
        List<String> tailles = stockTailleRepository.findTaillesEnStockByProduitId(produit.getId());

        // Conversion du prix : ton entity Produit a un float prix (d’après la table).
        Integer prixEuros = null;
        if (produit.getPrix() != 0) {
            prixEuros = Math.round(produit.getPrix()); // arrondi à l’euro
        }

        return new ResumeProduit(
            produit.getId(),
            produit.getNom(),
            prixEuros,
            tailles
        );
    }

}
