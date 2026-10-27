package com.ecommerce.products.service;

import com.ecommerce.catalog.model.Product;
import com.ecommerce.catalog.model.Shop;
import com.ecommerce.catalog.repository.CatalogShopRepository;
import com.ecommerce.products.dto.ProductRequest;
import com.ecommerce.products.dto.ProductResponse;
import com.ecommerce.products.repository.MerchantProductRepository;
import com.ecommerce.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantProductService {

    private final MerchantProductRepository merchantProductRepository;
    private final CatalogShopRepository catalogShopRepository;

    public ProductResponse createProduct(ProductRequest request, User owner) {
        Shop shop = catalogShopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada"));

        if (shop.getOwner() != null && !shop.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("No tienes permisos para agregar productos a esta tienda");
        }

        Product product = Product.builder()
                .title(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStock())
                .shop(shop)
                .build();

        return mapToResponse(merchantProductRepository.save(product));
    }

    public List<ProductResponse> getAllProducts() {
        return merchantProductRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByShop(Long shopId) {
        return merchantProductRepository.findByShopId(shopId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStockQuantity())
                .shopId(product.getShop() != null ? product.getShop().getId() : null)
                .shopName(product.getShop() != null ? product.getShop().getName() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }
}