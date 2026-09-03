package com.example.api.order.controller;

import com.example.api.TestcontainersConfiguration;
import com.example.api.order.dto.ErrorResponse;
import com.example.api.order.dto.OrderProductRequest;
import com.example.api.order.dto.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfiguration.class)
class OrderControllerFailureIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GenericContainer<?> inventoryApiContainer;

    @BeforeEach
    void setUp() {
        InventoryApiStub.reset(inventoryApiContainer);
    }

    @Test
    void returnsBadRequestWhenCustomerDoesNotExist() {
        OrderRequest request = new OrderRequest(999L, 2, new BigDecimal("40.0"),
                List.of(new OrderProductRequest(1L, 2)));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/orders", request,
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenProductDoesNotExist() {
        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("40.0"),
                List.of(new OrderProductRequest(999L, 2)));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/orders", request,
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenStockIsInsufficient() {
        InventoryApiStub.stubCheckStock(inventoryApiContainer, 1, 2, false);

        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("40.0"),
                List.of(new OrderProductRequest(1L, 2)));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/orders", request,
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenTotalPriceDoesNotMatch() {
        InventoryApiStub.stubCheckStock(inventoryApiContainer, 1, 2, true);

        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("999.0"),
                List.of(new OrderProductRequest(1L, 2)));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/orders", request,
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenProductsListIsEmpty() {
        OrderRequest request = new OrderRequest(1L, 0, new BigDecimal("0.0"), Collections.emptyList());

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/orders", request,
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenQuantityIsNotPositive() {
        OrderRequest request = new OrderRequest(1L, -1, new BigDecimal("40.0"),
                List.of(new OrderProductRequest(1L, 2)));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/orders", request,
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenTotalPriceIsMissing() {
        String json = """
                { "customerId": 1, "quantity": 2, "products": [ { "productId": 1, "quantity": 2 } ] }
                """;

        ResponseEntity<ErrorResponse> response = postRawJson(json);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenCustomerIdIsMissing() {
        String json = """
                { "quantity": 2, "totalPrice": 40.0, "products": [ { "productId": 1, "quantity": 2 } ] }
                """;

        ResponseEntity<ErrorResponse> response = postRawJson(json);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenQuantityIsMissing() {
        String json = """
                { "customerId": 1, "totalPrice": 40.0, "products": [ { "productId": 1, "quantity": 2 } ] }
                """;

        ResponseEntity<ErrorResponse> response = postRawJson(json);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenAllFieldsAreMissing() {
        ResponseEntity<ErrorResponse> response = postRawJson("{ }");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsBadRequestWhenRequestBodyIsMissing() {
        ResponseEntity<ErrorResponse> response = postRawJson("");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("Invalid order data"));
    }

    @Test
    void returnsInternalServerErrorWhenInventoryApiFails() {
        InventoryApiStub.stubServerError(inventoryApiContainer, 1, 2);

        OrderRequest request = new OrderRequest(1L, 2, new BigDecimal("40.0"),
                List.of(new OrderProductRequest(1L, 2)));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/orders", request,
                ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse("An unexpected error occurred"));
    }

    private ResponseEntity<ErrorResponse> postRawJson(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity("/api/orders", new HttpEntity<>(json, headers), ErrorResponse.class);
    }
}
