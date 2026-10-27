package com.ecommerce.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private Long shopId;
    private Long categoryId;
    private Long customsTariffId;
    private String sku;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private BigDecimal weightKg;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
}
