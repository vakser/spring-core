package com.epam.learn.springcore.monitoring;

import com.epam.learn.springcore.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingTypeServiceHealthIndicator implements HealthIndicator {
    private final TrainingTypeService trainingTypeService;

    @Override
    public Health health() {
        boolean isHealthy = checkTrainingTypeService();
        if (isHealthy) {
            return Health.up().withDetail("Training Type Service", "Functioning normally!").build();
        }
        return Health.down().withDetail("Training Type Service", "Is down!").build();
    }

    private boolean checkTrainingTypeService() {
        return trainingTypeService.getAllTrainingTypes().size() == 5;
    }
}
