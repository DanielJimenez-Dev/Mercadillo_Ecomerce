package com.ecommerce.shipments.service;

import com.ecommerce.orders.model.Order;
import com.ecommerce.orders.repository.OrderRepository;
import com.ecommerce.shipments.dto.ShipmentRequest;
import com.ecommerce.shipments.dto.ShipmentResponse;
import com.ecommerce.shipments.dto.ShipmentStatusUpdateRequest;
import com.ecommerce.shipments.model.Shipment;
import com.ecommerce.shipments.repository.ShipmentRepository;
import com.ecommerce.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ShipmentResponse createShipment(ShipmentRequest request, User buyer) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new RuntimeException("No tienes permiso para solicitar un envío para esta orden");
        }

        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("Solo se pueden generar envíos para órdenes que estén pagadas");
        }

        Shipment shipment = Shipment.builder()
                .order(order)
                .shippingAddress(request.getShippingAddress())
                .trackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(Shipment.ShipmentStatus.PENDING)
                .build();

        return mapToResponse(shipmentRepository.save(shipment));
    }

    @Transactional
    public ShipmentResponse updateShipmentStatus(UUID shipmentId, ShipmentStatusUpdateRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));

        shipment.setStatus(request.getStatus());

        // Si el envío ya fue despachado, actualizamos también el estado de la orden
        if (request.getStatus() == Shipment.ShipmentStatus.SHIPPED || request.getStatus() == Shipment.ShipmentStatus.IN_TRANSIT) {
            Order order = shipment.getOrder();
            order.setStatus(Order.OrderStatus.SHIPPED);
            orderRepository.save(order);
        }

        return mapToResponse(shipmentRepository.save(shipment));
    }

    public ShipmentResponse getShipmentByOrderId(UUID orderId, User buyer) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado para esta orden"));

        if (!shipment.getOrder().getBuyer().getId().equals(buyer.getId())) {
            throw new RuntimeException("No tienes permiso para consultar este envío");
        }

        return mapToResponse(shipment);
    }

    private ShipmentResponse mapToResponse(Shipment shipment) {
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder().getId())
                .shippingAddress(shipment.getShippingAddress())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus())
                .createdAt(shipment.getCreatedAt())
                .build();
    }
}