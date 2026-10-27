package com.ecommerce.shops.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ShopResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private OffsetDateTime createdAt;
}
