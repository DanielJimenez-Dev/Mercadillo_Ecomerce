package com.ecommerce.orders.service;

import com.ecommerce.catalog.model.Product;
import com.ecommerce.orders.dto.*;
import com.ecommerce.orders.model.Order;
import com.ecommerce.orders.model.OrderItem;
import com.ecommerce.orders.repository.OrderRepository;
import com.ecommerce.products.repository.MerchantProductRepository;
import com.ecommerce.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final MerchantProductRepository merchantProductRepository;

        public OrderResponse createOrder(OrderRequest request, User buyer) {
                BigDecimal totalAmount = BigDecimal.ZERO;
                List<OrderItem> orderItems = new ArrayList<>();

                Order order = Order.builder()
                                .buyer(buyer)
                                .status(Order.OrderStatus.PENDING)
                                .build();

                for (OrderItemRequest itemRequest : request.getItems()) {
                        Product product = merchantProductRepository.findById(itemRequest.getProductId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Producto no encontrado: " + itemRequest.getProductId()));

                        if (product.getStockQuantity() < itemRequest.getQuantity()) {
                                throw new RuntimeException(
                                                "Stock insuficiente para el producto: " + product.getTitle());
                        }

                        // Descontar stock
                        product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
                        merchantProductRepository.save(product);

                        BigDecimal itemTotal = product.getPrice()
                                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                        totalAmount = totalAmount.add(itemTotal);

                        OrderItem orderItem = OrderItem.builder()
                                        .order(order)
                                        .product(product)
                                        .quantity(itemRequest.getQuantity())
                                        .priceAtPurchase(product.getPrice())
                                        .build();

                        orderItems.add(orderItem);
                }

                order.setTotalAmount(totalAmount);
                order.setItems(orderItems);

                return mapToResponse(orderRepository.save(order));
        }

        public List<OrderResponse> getMyOrders(User buyer) {
                return orderRepository.findByBuyerId(buyer.getId())
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        private OrderResponse mapToResponse(Order order) {
                List<OrderItemResponse> itemResponses = order.getItems().stream()
                                .map(item -> OrderItemResponse.builder()
                                                .id(item.getId())
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getTitle())
                                                .quantity(item.getQuantity())
                                                .priceAtPurchase(item.getPriceAtPurchase())
                                                .build())
                                .collect(Collectors.toList());

                return OrderResponse.builder()
                                .id(order.getId())
                                .buyerId(order.getBuyer().getId())
                                .totalAmount(order.getTotalAmount())
                                .status(order.getStatus())
                                .items(itemResponses)
                                .createdAt(order.getCreatedAt())
                                .build();
        }
}
