package com.wydeline.controller;

import com.wydeline.dto.SlideVM;
import com.wydeline.service.CollectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CollectionApiController {

    private final CollectionService service;

    public CollectionApiController(CollectionService service) {
        this.service = service;
    }

    @GetMapping("/api/collection")
    public List<SlideVM> getCollection() {
        return service.buildCollectionVM();
    }
}
