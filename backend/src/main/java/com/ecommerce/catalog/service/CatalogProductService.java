package com.ecommerce.catalog.service;

import com.ecommerce.catalog.dto.ProductRequest;
import com.ecommerce.catalog.dto.ProductResponse;
import com.ecommerce.catalog.model.Category;
import com.ecommerce.catalog.model.CustomsTariff;
import com.ecommerce.catalog.model.Product;
import com.ecommerce.catalog.model.Shop;
import com.ecommerce.catalog.repository.CatalogProductRepository;
import com.ecommerce.catalog.repository.CatalogShopRepository;
import com.ecommerce.catalog.repository.CategoryRepository;
import com.ecommerce.catalog.repository.CustomsTariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogProductService {

    private final CatalogProductRepository catalogProductRepository;
    private final CatalogShopRepository catalogShopRepository;
    private final CategoryRepository categoryRepository;
    private final CustomsTariffRepository customsTariffRepository;

    public List<ProductResponse> getAllProducts() {
        return catalogProductRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request) {
        Shop shop = catalogShopRepository.findById(request.getShopId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found with id: " + request.getShopId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        CustomsTariff customsTariff = null;
        if (request.getCustomsTariffId() != null) {
            customsTariff = customsTariffRepository.findById(request.getCustomsTariffId()).orElse(null);
        }

        Product product = Product.builder()
                .shop(shop)
                .category(category)
                .customsTariff(customsTariff)
                .sku(request.getSku())
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .weightKg(request.getWeightKg())
                .lengthCm(request.getLengthCm())
                .widthCm(request.getWidthCm())
                .heightCm(request.getHeightCm())
                .isActive(true)
                .build();

        Product savedProduct = catalogProductRepository.save(product);
        return mapToResponse(savedProduct);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .isActive(product.getIsActive())
                .shopId(product.getShop() != null ? product.getShop().getId() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }
}