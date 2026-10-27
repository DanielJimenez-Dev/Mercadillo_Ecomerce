package com.ecommerce.shops.controller;

import com.ecommerce.shops.dto.ShopRequest;
import com.ecommerce.shops.dto.ShopResponse;
import com.ecommerce.shops.service.ShopService;
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
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ShopResponse> createShop(
            @Valid @RequestBody ShopRequest request,
            @AuthenticationPrincipal User owner
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shopService.createShop(request, owner));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<List<ShopResponse>> getMyShops(@AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(shopService.getMyShops(owner));
    }
}