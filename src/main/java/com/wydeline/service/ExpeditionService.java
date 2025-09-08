package com.wydeline.service;

import org.springframework.stereotype.Service;

import com.wydeline.model.Commande;
import com.wydeline.model.Expedition;
import com.wydeline.model.ExpeditionLigne;
import com.wydeline.repository.ExpeditionLigneRepository;
import com.wydeline.repository.ExpeditionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpeditionService {
  private final ExpeditionRepository expeditionRepo;
  private final ExpeditionLigneRepository expeditionLigneRepo;

  @Transactional
  public Expedition createIfAbsent(Commande cmd) {
    // Si déjà existante, renvoie-la
    var existing = expeditionRepo.findByCommande(cmd);
    if (existing.isPresent()) return existing.get();

    // Sinon, crée-la
    Expedition ex = new Expedition();
    ex.setCommande(cmd);
    ex = expeditionRepo.save(ex);

    for (var lc : cmd.getLignes()) {
      ExpeditionLigne xl = new ExpeditionLigne();
      xl.setExpedition(ex);
      xl.setLigneCommande(lc);
      xl.setQuantite(lc.getQuantite());
      expeditionLigneRepo.save(xl);
    }
    return ex;
  }
}
