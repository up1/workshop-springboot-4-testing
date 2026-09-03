package com.example.api.order.service;

import com.example.api.customer.repository.CustomerRepository;
import com.example.api.order.dto.OrderProductRequest;
import com.example.api.order.dto.OrderRequest;
import com.example.api.order.exception.OrderValidationException;
import com.example.api.order.repository.OrderRepository;
import com.example.api.product.model.Product;
import com.example.api.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceFailureUnitTest {

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

    @BeforeEach
    void setUp() {
        widget = new Product("Widget", new BigDecimal("20.00"), 100);
    }

    @Test
    void rejectsOrderWhenCustomerDoesNotExist() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("40.00"),
                List.of(new OrderProductRequest(1L, 2)));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OrderValidationException.class)
                .hasMessage("Invalid order data");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsOrderWhenProductDoesNotExist() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("40.00"),
                List.of(new OrderProductRequest(1L, 2)));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OrderValidationException.class)
                .hasMessage("Invalid order data");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsOrderWhenStockIsInsufficient() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(inventoryClient.isStockAvailable(any(), anyInt())).thenReturn(false);

        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("40.00"),
                List.of(new OrderProductRequest(1L, 2)));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OrderValidationException.class)
                .hasMessage("Invalid order data");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsOrderWhenQuantityDoesNotMatchProducts() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(inventoryClient.isStockAvailable(any(), anyInt())).thenReturn(true);

        OrderRequest request = new OrderRequest(1L, 5, new BigDecimal("40.00"),
                List.of(new OrderProductRequest(1L, 2)));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OrderValidationException.class)
                .hasMessage("Invalid order data");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsOrderWhenTotalPriceDoesNotMatchProducts() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(widget));
        when(inventoryClient.isStockAvailable(any(), anyInt())).thenReturn(true);

        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("999.00"),
                List.of(new OrderProductRequest(1L, 2)));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OrderValidationException.class)
                .hasMessage("Invalid order data");
        verify(orderRepository, never()).save(any());
    }
}
