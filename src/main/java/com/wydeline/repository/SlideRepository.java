package com.wydeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wydeline.model.Slide;

public interface SlideRepository extends JpaRepository<Slide, Long> {
    List<Slide> findAllByOrderByPageOrderAsc();
}

