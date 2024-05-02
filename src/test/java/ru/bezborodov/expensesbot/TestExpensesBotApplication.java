package ru.bezborodov.expensesbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestExpensesBotApplication {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer(DynamicPropertyRegistry registry) {
        var container = new PostgreSQLContainer<>("postgres:15");
        registry.add("postgresql.driver", container::getDriverClassName);
        return container;
    }

    public static void main(String[] args) {
        SpringApplication.from(ExpensesBotApplication::main).with(TestExpensesBotApplication.class).run(args);
    }

}
