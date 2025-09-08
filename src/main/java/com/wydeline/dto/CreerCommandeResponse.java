// src/main/java/com/wydeline/dto/CreateOrderResponse.java
package com.wydeline.dto;

import java.math.BigDecimal;

public class CreerCommandeResponse {
    private Long commandeId;
    private String status;
    private BigDecimal total;

    public CreerCommandeResponse(Long commandeId, String status, BigDecimal total) {
        this.commandeId = commandeId;
        this.status = status;
        this.total = total;
    }

    public Long getCommandeId() { return commandeId; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
}
