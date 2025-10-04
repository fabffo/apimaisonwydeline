// src/main/java/com/wydeline/controller/AdminPaiementController.java
package com.wydeline.controller;

import com.wydeline.repository.PaiementLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller @RequiredArgsConstructor
public class AdminPaiementController {
    private final PaiementLogRepository repo;

    @GetMapping("/admin/paiements")
    public String list(Model model){
        model.addAttribute("logs", repo.findAll());
        return "admin/paiements"; // Thymeleaf
    }
}
