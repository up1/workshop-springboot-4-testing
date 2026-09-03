package com.example.api.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        @NotNull Long customerId,
        @NotNull @Positive Integer quantity,
        @NotNull @Positive BigDecimal totalPrice,
        @NotEmpty List<@Valid OrderProductRequest> products) {
}
