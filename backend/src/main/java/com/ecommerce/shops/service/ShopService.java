package com.ecommerce.shops.service;

import com.ecommerce.shops.dto.ShopRequest;
import com.ecommerce.shops.dto.ShopResponse;
import com.ecommerce.shops.model.Shop;
import com.ecommerce.shops.repository.ShopRepository;
import com.ecommerce.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopResponse createShop(ShopRequest request, User owner) {
        Shop shop = Shop.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        Shop savedShop = shopRepository.save(shop);
        return mapToResponse(savedShop);
    }

    public List<ShopResponse> getMyShops(User owner) {
        return shopRepository.findByOwnerId(owner.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ShopResponse mapToResponse(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .description(shop.getDescription())
                .ownerId(shop.getOwner().getId())
                .createdAt(shop.getCreatedAt())
                .build();
    }
}
