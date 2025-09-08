
package com.wydeline.repository;

import com.wydeline.model.Produit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
	

    List<Produit> findByIdIn(List<Long> ids);

}
