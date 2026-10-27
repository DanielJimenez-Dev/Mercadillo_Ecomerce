package com.ecommerce.shipments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ShipmentRequest {

    @NotNull(message = "El ID de la orden es obligatorio")
    private UUID orderId;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String shippingAddress;
}