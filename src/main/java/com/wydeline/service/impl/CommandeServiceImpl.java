// com.wydeline.service.impl.CommandeServiceImpl
package com.wydeline.service.impl;

import com.wydeline.dto.CreerCommandeRequest;
import com.wydeline.model.*;
import com.wydeline.repository.*;
import com.wydeline.service.CommandeErreurException;
import com.wydeline.service.CommandeService;
import com.wydeline.service.StockInsuffisantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CommandeServiceImpl implements CommandeService {

    private final ProduitRepository produitRepository;
    private final StockTailleRepository stockTailleRepository;
    private final CommandeRepository commandeRepository;

    public CommandeServiceImpl(ProduitRepository productRepository,
                               StockTailleRepository stockTailleRepository,
                               CommandeRepository commandeRepository) {
        this.produitRepository = productRepository;
        this.stockTailleRepository = stockTailleRepository;
        this.commandeRepository = commandeRepository;
    }

    @Override
    @Transactional
    public Commande createAndReserve(CreerCommandeRequest req) {
        if (req.getArticles() == null || req.getArticles().isEmpty()) {
            throw new CommandeErreurException("Panier vide");
        }
        System.out.println("REQUETE "+req.toString());
        Commande cmd = new Commande();
     // ⚠️ email depuis le DTO
        if (req.getEmailClient() != null && !req.getEmailClient().isBlank()) {
            cmd.setEmailClient(req.getEmailClient().trim());
        }

        BigDecimal total = BigDecimal.ZERO;
        
     // dans CommandeServiceImpl#createAndReserve
        var l = req.getLivraison();
        var adr = new AdresseLivraison();
        adr.setPays(l.getPays().trim());
        adr.setPrenom(l.getPrenom().trim());
        adr.setNom(l.getNom().trim());
        adr.setEntreprise(l.getEntreprise());
        adr.setAdresse(l.getAdresse().trim());
        adr.setComplement(l.getComplement());
        adr.setCodePostal(l.getCodePostal().trim());
        adr.setVille(l.getVille().trim());
        adr.setTelephone(l.getTelephone().trim());
        cmd.setLivraison(adr);
        System.out.println("livraison"+cmd.getLivraison().toString());

        // pour les lignes, tu continues d'accepter { "taille": "...", "couleur": "..." } grâce à @JsonProperty


        for (CreerCommandeRequest.Item it : req.getArticles()) {
            if (it.getProduitId() == null) {
                throw new CommandeErreurException("article.productId manquant");
            }
            final int quantiteDemandee = (it.getQuantite() == null || it.getQuantite() <= 0) ? 1 : it.getQuantite();

            // Produit
            final Produit p = produitRepository.findById(it.getProduitId())
                    .orElseThrow(() -> new CommandeErreurException("Produit introuvable: " + it.getProduitId()));

            // Taille (obligatoire)
            final String size = (it.getTaille() == null || it.getTaille().isBlank()) ? null : it.getTaille().trim();
            if (size == null) {
                throw new CommandeErreurException("taille produit manquant pour productId=" + it.getProduitId());
            }

            // Couleur (obligatoire seulement si le produit a des couleurs)
            String couleurDemandee;
            if (p.isOptionCouleur()) {
                if (it.getCouleur() == null || it.getCouleur().isBlank()) {
                    throw new CommandeErreurException("couleur produit requis pour le produit " + p.getId());
                }
                couleurDemandee = it.getCouleur().trim();
            } else {
                couleurDemandee = null; // force null si le produit n'a pas de couleurs
            }

            // ==> Rendre les valeurs capturées "finales"
            final Long pid = p.getId();
            final String cleCouleur = couleurDemandee;
            final String cleTaille  = size;
            System.out.println("pid " + pid + "cleCouleur " + cleCouleur +" cleTaille " + cleTaille);
            
            // Verrouiller la variante (produit + taille + couleur|NULL)
            StockTaille variant = stockTailleRepository.lockVariant(pid, cleTaille, cleCouleur)
                    .orElseGet(() -> {
                        StockTaille s = new StockTaille();
                        s.setProduit(p);
                        s.setTaille(cleTaille);
                        s.setCouleur(cleCouleur);
                        s.setQuantite(0);
                        return stockTailleRepository.save(s);
                    });

            // Gestion du stock si pas de précommande
            if (!p.isPrecommande()) {  	
                int quantiteDisponible = (variant.getQuantite() == null) ? 0 : variant.getQuantite();
                System.out.println("QUANTITEDISPO "+ quantiteDisponible  + "et demande " + quantiteDemandee);
                if (quantiteDisponible < quantiteDemandee) {
                    throw new StockInsuffisantException(p.getId(), cleTaille, cleCouleur, quantiteDemandee, quantiteDisponible);
                }
                variant.setQuantite(quantiteDisponible - quantiteDemandee);
                // pas besoin de save() explicite : dirty checking JPA
            }

            // Prix
            BigDecimal unit = BigDecimal.valueOf(p.getPrix());
            total = total.add(unit.multiply(BigDecimal.valueOf(quantiteDemandee)));

            // Ligne commande
            LigneCommande lc = new LigneCommande();
            lc.setCommande(cmd);
            lc.setProduit(p);
            lc.setQuantite(quantiteDemandee);
            lc.setPrixUnitaire(unit);
            lc.setTaille(cleTaille);
            lc.setCouleur(cleCouleur);
            cmd.getLignes().add(lc);
        }

        cmd.setMontantTotal(total);
        cmd.setStatus(CommandeStatus.PREPARATION_EN_COURS);
        return commandeRepository.save(cmd);
    }

}
