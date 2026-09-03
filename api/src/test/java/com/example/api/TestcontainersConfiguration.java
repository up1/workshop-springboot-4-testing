package com.example.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));
	}

	@Bean
	public GenericContainer<?> inventoryApiContainer() {
		GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:latest"))
				.withExposedPorts(8080)
				.waitingFor(Wait.forHttp("/__admin/health").forStatusCode(200));
		container.start();
		return container;
	}

	@Bean
	DynamicPropertyRegistrar inventoryApiProperties(GenericContainer<?> inventoryApiContainer) {
		return registry -> registry.add("inventory.api.base-url",
				() -> "http://%s:%d".formatted(inventoryApiContainer.getHost(),
						inventoryApiContainer.getMappedPort(8080)));
	}

}

