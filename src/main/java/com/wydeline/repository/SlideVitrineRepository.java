package com.wydeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wydeline.model.SlideVitrine;

public interface SlideVitrineRepository extends JpaRepository<SlideVitrine, Long> {
    List<SlideVitrine> findAllByOrderByPageOrderAsc();
}

