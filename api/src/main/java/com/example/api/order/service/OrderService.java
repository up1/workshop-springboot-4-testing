package com.example.api.order.service;

import com.example.api.customer.repository.CustomerRepository;
import com.example.api.order.dto.OrderProductRequest;
import com.example.api.order.dto.OrderProductResponse;
import com.example.api.order.dto.OrderRequest;
import com.example.api.order.dto.OrderResponse;
import com.example.api.order.exception.OrderValidationException;
import com.example.api.order.model.Order;
import com.example.api.order.model.OrderItem;
import com.example.api.order.repository.OrderRepository;
import com.example.api.product.model.Product;
import com.example.api.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderService(CustomerRepository customerRepository, ProductRepository productRepository,
            OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        if (!customerRepository.existsById(request.customerId())) {
            throw new OrderValidationException("Invalid order data");
        }

        Order order = new Order(request.customerId(), request.quantity(), request.totalPrice());
        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderProductRequest item : request.products()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new OrderValidationException("Invalid order data"));

            if (!inventoryClient.isStockAvailable(item.productId(), item.quantity())) {
                throw new OrderValidationException("Invalid order data");
            }

            totalQuantity += item.quantity();
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));

            order.addItem(new OrderItem(item.productId(), item.quantity()));
        }

        if (totalQuantity != request.quantity() || totalPrice.compareTo(request.totalPrice()) != 0) {
            throw new OrderValidationException("Invalid order data");
        }

        Order savedOrder = orderRepository.save(order);
        return toOrderResponse(savedOrder);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderProductResponse> products = order.getItems().stream()
                .map(item -> new OrderProductResponse(item.getProductId(), item.getQuantity()))
                .toList();
        return new OrderResponse(order.getOrderId(), order.getCustomerId(), order.getQuantity(),
                order.getTotalPrice(), products);
    }
}
