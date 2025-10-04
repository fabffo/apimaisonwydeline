package com.wydeline.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "slide")
@Data @NoArgsConstructor @AllArgsConstructor
public class Slide {

    public enum TypeImage { FULL, GRID2 }
    public enum ImageKind { DECO, PRODUIT } // ⬅️ NOUVEAU

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_image", nullable = false, length = 16)
    private TypeImage typeImage;

    @Column(name = "nom_image")
    private String nomImage;

    @Column(name = "page_order", nullable = false)
    private Integer pageOrder;

    /* ---------- FULL ---------- */
    @Enumerated(EnumType.STRING)
    @Column(name = "full_kind", length = 16)
    private ImageKind fullKind; // DECO ou PRODUIT

    // si DECO
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "image_full_id")
    private ImageDeco imageFull;

    // si PRODUIT
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_full_id")
    private Produit productFull;

    /* ---------- GRID2 : LEFT ---------- */
    @Enumerated(EnumType.STRING)
    @Column(name = "left_kind", length = 16)
    private ImageKind leftKind; // DECO ou PRODUIT

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "image_left_id")
    private ImageDeco imageLeft;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_left_id")
    private Produit productLeft;

    /* ---------- GRID2 : RIGHT ---------- */
    @Enumerated(EnumType.STRING)
    @Column(name = "right_kind", length = 16)
    private ImageKind rightKind; // DECO ou PRODUIT

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "image_right_id")
    private ImageDeco imageRight;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_right_id")
    private Produit productRight;
}
