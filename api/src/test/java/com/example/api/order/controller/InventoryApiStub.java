package com.example.api.order.controller;

import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;

/** Helper for configuring stub responses on the WireMock inventory container used in tests. */
final class InventoryApiStub {

    private InventoryApiStub() {
    }

    static void stubCheckStock(GenericContainer<?> inventoryApiContainer, long productId, int quantity,
            boolean available) {
        String baseUrl = "http://%s:%d".formatted(inventoryApiContainer.getHost(),
                inventoryApiContainer.getMappedPort(8080));
        RestClient restClient = RestClient.create(baseUrl);

        String mapping = """
                {
                    "request": {
                        "method": "POST",
                        "urlPath": "/inventory/check-stock",
                        "bodyPatterns": [ { "equalToJson": "{\\"productId\\":%d,\\"quantity\\":%d}" } ]
                    },
                    "response": {
                        "status": 200,
                        "headers": { "Content-Type": "application/json" },
                        "jsonBody": { "productId": %d, "available": %b }
                    }
                }
                """.formatted(productId, quantity, productId, available);

        restClient.post()
                .uri("/__admin/mappings")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(mapping)
                .retrieve()
                .toBodilessEntity();
    }

    static void stubServerError(GenericContainer<?> inventoryApiContainer, long productId, int quantity) {
        String baseUrl = "http://%s:%d".formatted(inventoryApiContainer.getHost(),
                inventoryApiContainer.getMappedPort(8080));
        RestClient restClient = RestClient.create(baseUrl);

        String mapping = """
                {
                    "request": {
                        "method": "POST",
                        "urlPath": "/inventory/check-stock",
                        "bodyPatterns": [ { "equalToJson": "{\\"productId\\":%d,\\"quantity\\":%d}" } ]
                    },
                    "response": {
                        "status": 500,
                        "headers": { "Content-Type": "application/json" },
                        "jsonBody": { "error": "Server error" }
                    }
                }
                """.formatted(productId, quantity);

        restClient.post()
                .uri("/__admin/mappings")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(mapping)
                .retrieve()
                .toBodilessEntity();
    }

    static void reset(GenericContainer<?> inventoryApiContainer) {
        String baseUrl = "http://%s:%d".formatted(inventoryApiContainer.getHost(),
                inventoryApiContainer.getMappedPort(8080));
        RestClient.create(baseUrl).post().uri("/__admin/mappings/reset").retrieve().toBodilessEntity();
    }
}
