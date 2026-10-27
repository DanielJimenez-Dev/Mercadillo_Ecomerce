package com.ecommerce.payments.service;

import com.ecommerce.orders.model.Order;
import com.ecommerce.orders.repository.OrderRepository;
import com.ecommerce.payments.dto.PaymentRequest;
import com.ecommerce.payments.dto.PaymentResponse;
import com.ecommerce.payments.model.Payment;
import com.ecommerce.payments.repository.PaymentRepository;
import com.ecommerce.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, User buyer) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Validar que la orden pertenezca al usuario autenticado
        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new RuntimeException("No tienes permiso para pagar esta orden");
        }

        // Validar si la orden ya fue pagada
        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new RuntimeException("Esta orden ya ha sido pagada previamente");
        }

        // Crear el registro de pago (Simulamos que el pago es aprobado automáticamente)
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(Payment.PaymentStatus.APPROVED)
                .build();

        // Actualizar el estado de la orden a PAID
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);

        return mapToResponse(paymentRepository.save(payment));
    }

    public PaymentResponse getPaymentByOrderId(UUID orderId, User buyer) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para esta orden"));

        if (!payment.getOrder().getBuyer().getId().equals(buyer.getId())) {
            throw new RuntimeException("No tienes permiso para ver este pago");
        }

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
