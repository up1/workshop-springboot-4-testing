package com.example.api.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderProductRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity) {
}
