package com.ecommerce.shipments.dto;

import com.ecommerce.shipments.model.Shipment.ShipmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ShipmentResponse {
    private UUID id;
    private UUID orderId;
    private String shippingAddress;
    private String trackingNumber;
    private ShipmentStatus status;
    private OffsetDateTime createdAt;
}