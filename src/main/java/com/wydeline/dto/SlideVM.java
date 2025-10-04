package com.wydeline.dto;

import java.util.List;

public class SlideVM {
    public enum Type { FULL, GRID2 }
    public enum ImageType { DECO, PRODUIT } // ⬅️

    public int pageOrder;
    public Type type;
    public List<SlideImageVM> images;

    public static class SlideImageVM {
        public ImageType imageType;  // ⬅️ "DECO" ou "PRODUIT"

        // Toujours présents pour l’affichage de l’image
        public String src;
        public String alt;

        // Renseignés UNIQUEMENT si imageType == PRODUIT
        public Long produitId;
        public String produitNom;
        public Integer prix;
        public List<String> tailles;
    }
}
