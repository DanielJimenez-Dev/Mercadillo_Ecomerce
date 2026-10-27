package com.ecommerce.products.controller;

import com.ecommerce.products.dto.ProductRequest;
import com.ecommerce.products.dto.ProductResponse;
import com.ecommerce.products.service.MerchantProductService;
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
@RequestMapping("/api/v1/merchant/products")
@RequiredArgsConstructor
public class MerchantProductController {

    private final MerchantProductService merchantProductService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal User owner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(merchantProductService.createProduct(request, owner));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(merchantProductService.getAllProducts());
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductResponse>> getProductsByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(merchantProductService.getProductsByShop(shopId));
    }
}