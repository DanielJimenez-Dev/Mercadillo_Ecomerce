package com.ecommerce.payments.controller;

import com.ecommerce.payments.dto.PaymentRequest;
import com.ecommerce.payments.dto.PaymentResponse;
import com.ecommerce.payments.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User buyer
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(request, buyer));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal User buyer
    ) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId, buyer));
    }
}