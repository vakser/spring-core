package com.epam.learn.springcore.monitoring;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DBHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) { // You can adjust the timeout (1 second here)
                return Health.up().withDetail("database", "MySQL").withDetail("status", "Connected!").build();
            } else {
                return Health.down().withDetail("database", "MySQL").withDetail("status", "Invalid connection!").build();
            }
        } catch (SQLException e) {
            return Health.down(e).withDetail("database", "MySQL").withDetail("status", "Down").build();
        }
    }
}
