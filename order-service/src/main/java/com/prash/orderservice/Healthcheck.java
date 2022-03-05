package com.prash.orderservice;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class Healthcheck implements HealthIndicator {

    int errorcode = 0;
    @Override
    public Health health() {
        System.out.println("Health check performed, error code is " + errorcode);
        if (errorcode > 2 && errorcode < 4) {
            errorcode++;
            return Health.down().withDetail("Custom error code", errorcode).build();
        } else {
            errorcode++;
            return Health.up().build();
        }
    }
}
