package com.wydeline.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wydeline.dto.SlideVitrineVM;
import com.wydeline.service.VitrineService;

@RestController
public class VitrineApiController {

    private final VitrineService service;

    public VitrineApiController(VitrineService service) {
        this.service = service;
    }

    @GetMapping("/api/vitrine")
    public List<SlideVitrineVM> getVitrine() {
        return service.buildVitrineVM();
    }
}
