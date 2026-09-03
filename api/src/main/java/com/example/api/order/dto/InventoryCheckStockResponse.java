package com.example.api.order.dto;

public record InventoryCheckStockResponse(Long productId, boolean available) {
}
