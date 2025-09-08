package com.wydeline.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wydeline.model.Produit;
import com.wydeline.model.StockTaille;

import jakarta.persistence.LockModeType;

public interface StockTailleRepository extends JpaRepository<StockTaille, Long> {

    // Lecture simple d’une variante (produit + taille + couleur|NULL)
    @Query("""
        select s from StockTaille s
        where s.produit.id = :pid
          and s.taille = :taille
          and ( (s.couleur is null and :clr is null) or s.couleur = :clr )
    """)
    Optional<StockTaille> findVariant(
        @Param("pid") Long produitId,
        @Param("taille") String taille,
        @Param("clr") String couleur
    );

    // Lecture avec verrouillage pessimiste pour décrémenter sans courses critiques
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select s from StockTaille s
        where s.produit.id = :pid
          and s.taille = :taille
          and ( (s.couleur is null and :clr is null) or s.couleur = :clr )
    """)
    Optional<StockTaille> lockVariant(
        @Param("pid") Long produitId,
        @Param("taille") String taille,
        @Param("clr") String couleur
    );
    
    List<StockTaille> findByProduit_Id(Long produitId);
    
    List<StockTaille> findByProduit_IdIn(Collection<Long> produitIds); // pour batch multi-produits
    
 // Tous les produits ayant au moins une variante en stock
    @Query("select distinct s.produit.id from StockTaille s where s.quantite > 0")
    List<Long> findAllProduitIdsWithStock();

    // Variante : nombre de variantes > 0 par produit (utile pour debug/stats)
    @Query("select s.produit.id, count(s) from StockTaille s where s.quantite > 0 group by s.produit.id")
    List<Object[]> countVariantsWithStockByProduit();

    
    @Query("""
            select st
            from StockTaille st
            join fetch st.produit p
            where st.quantite > 0
            """)
     List<StockTaille> findAllAvailable();
    
    
    
    
}
