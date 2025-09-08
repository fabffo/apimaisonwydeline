// src/main/java/com/wydeline/service/CommandeService.java
package com.wydeline.service;

import com.wydeline.dto.CreerCommandeRequest;
import com.wydeline.model.Commande;

public interface CommandeService {
    Commande createAndReserve(CreerCommandeRequest req);
}
