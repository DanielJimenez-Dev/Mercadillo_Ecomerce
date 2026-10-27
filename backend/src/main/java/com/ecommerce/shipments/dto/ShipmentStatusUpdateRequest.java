package com.ecommerce.shipments.dto;

import com.ecommerce.shipments.model.Shipment.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipmentStatusUpdateRequest {

    @NotNull(message = "El nuevo estado del envío es obligatorio")
    private ShipmentStatus status;
}
