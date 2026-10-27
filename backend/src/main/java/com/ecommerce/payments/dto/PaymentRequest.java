package com.ecommerce.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {

    @NotNull(message = "El ID de la orden es obligatorio")
    private UUID orderId;

    @NotBlank(message = "El método de pago es obligatorio")
    private String paymentMethod;
}