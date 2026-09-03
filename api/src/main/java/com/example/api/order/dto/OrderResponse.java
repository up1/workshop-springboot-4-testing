package com.example.api.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long customerId,
        Integer quantity,
        BigDecimal totalPrice,
        List<OrderProductResponse> products) {
}
