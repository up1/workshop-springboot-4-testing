package com.example.api.order.controller;

import com.example.api.TestcontainersConfiguration;
import com.example.api.order.dto.OrderProductRequest;
import com.example.api.order.dto.OrderRequest;
import com.example.api.order.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfiguration.class)
class OrderControllerSuccessIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GenericContainer<?> inventoryApiContainer;

    @BeforeEach
    void setUp() {
        InventoryApiStub.reset(inventoryApiContainer);
    }

    @Test
    void createsOrderAndReturnsCreated() {
        InventoryApiStub.stubCheckStock(inventoryApiContainer, 1, 2, true);
        InventoryApiStub.stubCheckStock(inventoryApiContainer, 2, 1, true);

        OrderRequest request = new OrderRequest(1L, 3, new BigDecimal("100.0"),
                List.of(new OrderProductRequest(1L, 2), new OrderProductRequest(2L, 1)));

        ResponseEntity<OrderResponse> response = restTemplate.postForEntity("/api/orders", request,
                OrderResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        OrderResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.orderId()).isNotNull();
        assertThat(body.customerId()).isEqualTo(1L);
        assertThat(body.quantity()).isEqualTo(3);
        assertThat(body.totalPrice()).isEqualByComparingTo("100.0");
        assertThat(body.products()).hasSize(2);
    }
}
