// com.wydeline.service.InsufficientStockException
package com.wydeline.service;

public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(Long produitId, String taille, String couleur, int asked, int available) {
        super("Stock insuffisant pour produit=" + produitId +
              ", taille=" + taille +
              ", couleur=" + (couleur == null ? "-" : couleur) +
              " (demande=" + asked + ", dispo=" + available + ")");
    }
}
