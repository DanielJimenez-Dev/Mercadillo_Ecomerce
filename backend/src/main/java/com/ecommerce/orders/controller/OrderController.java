package com.ecommerce.orders.controller;

import com.ecommerce.orders.dto.OrderRequest;
import com.ecommerce.orders.dto.OrderResponse;
import com.ecommerce.orders.service.OrderService;
import com.ecommerce.users.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User buyer
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request, buyer));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal User buyer) {
        return ResponseEntity.ok(orderService.getMyOrders(buyer));
    }
}