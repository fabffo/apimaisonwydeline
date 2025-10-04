package com.wydeline.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image_deco")
@Data @NoArgsConstructor @AllArgsConstructor
public class ImageDeco {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;          // ex. "Look 01"
    @Column(name = "image_url")
    private String imageUrl;     // ex. "/images/collection/look01.jpg"
    private String description;

    // getters/setters
}
