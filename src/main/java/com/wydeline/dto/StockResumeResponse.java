package com.wydeline.dto;

import java.util.List;

public record StockResumeResponse(Long produitId, List<StockResumeArticle> articles) {}
