package com.ecommerce.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String sku;
    private Boolean isActive;
    private Long shopId;
    private Long categoryId;
}
