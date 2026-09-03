package com.example.api.order.service;

import com.example.api.customer.repository.CustomerRepository;
import com.example.api.order.dto.OrderProductRequest;
import com.example.api.order.dto.OrderRequest;
import com.example.api.order.dto.OrderResponse;
import com.example.api.order.repository.OrderRepository;
import com.example.api.product.model.Product;
import com.example.api.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceSuccessUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private OrderService orderService;

    private Product widget;
    private Product gadget;

    @BeforeEach
    void setUp() {
        widget = new Product("Widget", new BigDecimal("20.00"), 100);
        gadget = new Product("Gadget", new BigDecimal("60.00"), 50);
    }

    @Test
    void createsOrderWhenRequestIsValid() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(productRepository.findById(2L)).thenReturn(Optional.of(gadget));
        when(inventoryClient.isStockAvailable(any(), anyInt())).thenReturn(true);
        when(orderRepository.save(any())).thenAnswer(invocation -> {
            var order = invocation.getArgument(0, com.example.api.order.model.Order.class);
            ReflectionTestUtils.setField(order, "orderId", 1L);
            return order;
        });

        OrderRequest request = new OrderRequest(1L, 3, new BigDecimal("100.00"),
                List.of(new OrderProductRequest(1L, 2), new OrderProductRequest(2L, 1)));

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.orderId()).isEqualTo(1L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(3);
        assertThat(response.totalPrice()).isEqualByComparingTo("100.00");
        assertThat(response.products()).hasSize(2);
    }

    @Test
    void checksStockAvailabilityForEachProduct() {
        when(customerRepository.existsById(eq(1L))).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(productRepository.findById(2L)).thenReturn(Optional.of(gadget));
        when(inventoryClient.isStockAvailable(any(), anyInt())).thenReturn(true);
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = new OrderRequest(1L, 3, new BigDecimal("100.00"),
                List.of(new OrderProductRequest(1L, 2), new OrderProductRequest(2L, 1)));

        orderService.createOrder(request);

        org.mockito.Mockito.verify(inventoryClient).isStockAvailable(1L, 2);
        org.mockito.Mockito.verify(inventoryClient).isStockAvailable(2L, 1);
    }
}
