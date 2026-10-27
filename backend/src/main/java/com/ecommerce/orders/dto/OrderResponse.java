package com.ecommerce.orders.dto;

import com.ecommerce.orders.model.Order.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID buyerId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private OffsetDateTime createdAt;
}
