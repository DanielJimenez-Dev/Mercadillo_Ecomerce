package com.ecommerce.payments.dto;

import com.ecommerce.payments.model.Payment.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private OffsetDateTime createdAt;
}
