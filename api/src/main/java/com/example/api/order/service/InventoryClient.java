package com.example.api.order.service;

import com.example.api.order.dto.InventoryCheckStockRequest;
import com.example.api.order.dto.InventoryCheckStockResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(RestClient.Builder restClientBuilder,
            @Value("${inventory.api.base-url}") String baseUrl) {
        // Force HTTP/1.1: the JDK HttpClient's default HTTP/2 upgrade attempt is reset by WireMock in tests.
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.restClient = restClientBuilder.baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    public boolean isStockAvailable(Long productId, Integer quantity) {
        InventoryCheckStockResponse response = restClient.post()
                .uri("/inventory/check-stock")
                .body(new InventoryCheckStockRequest(productId, quantity))
                .retrieve()
                .body(InventoryCheckStockResponse.class);
        return response != null && response.available();
    }
}
