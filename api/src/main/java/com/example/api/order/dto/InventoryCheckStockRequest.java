package com.example.api.order.dto;

public record InventoryCheckStockRequest(Long productId, Integer quantity) {
}
