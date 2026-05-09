package com.eventflow.orderservice.interfaces.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "customerId is required")
        String customerId,

        @NotNull(message = "totalAmount is required")
        @DecimalMin(value = "0.00", inclusive = false, message = "totalAmount must be greater than zero")
        BigDecimal totalAmount
) {
}
