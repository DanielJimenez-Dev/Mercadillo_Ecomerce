package com.ecommerce.shipments.controller;

import com.ecommerce.shipments.dto.ShipmentRequest;
import com.ecommerce.shipments.dto.ShipmentResponse;
import com.ecommerce.shipments.dto.ShipmentStatusUpdateRequest;
import com.ecommerce.shipments.service.ShipmentService;
import com.ecommerce.users.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody ShipmentRequest request,
            @AuthenticationPrincipal User buyer
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.createShipment(request, buyer));
    }

    @PatchMapping("/{shipmentId}/status")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ShipmentResponse> updateShipmentStatus(
            @PathVariable UUID shipmentId,
            @Valid @RequestBody ShipmentStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(shipmentId, request));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShipmentResponse> getShipmentByOrderId(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal User buyer
    ) {
        return ResponseEntity.ok(shipmentService.getShipmentByOrderId(orderId, buyer));
    }
}