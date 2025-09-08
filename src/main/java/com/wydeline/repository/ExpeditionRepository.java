package com.wydeline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wydeline.model.Commande;
import com.wydeline.model.Expedition;

public interface ExpeditionRepository extends JpaRepository<Expedition, Long> {
	Optional<Expedition> findByCommande(Commande commande);
}


