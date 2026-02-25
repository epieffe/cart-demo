package dev.epieffe.demo.cart;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration
public class TestContainersConfig {

	@Bean
	@ServiceConnection
	public PostgreSQLContainer postgres() {
		return new PostgreSQLContainer("postgres:18-alpine");
	}
}
