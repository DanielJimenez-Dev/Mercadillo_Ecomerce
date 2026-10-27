package com.ecommerce.shipments.repository;

import com.ecommerce.shipments.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    Optional<Shipment> findByOrderId(UUID orderId);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
