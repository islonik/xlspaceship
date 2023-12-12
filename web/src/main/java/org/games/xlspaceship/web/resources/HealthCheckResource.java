package org.games.xlspaceship.web.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckResource {

    private final HealthEndpoint healthEndpoint;

    @Autowired
    public HealthCheckResource(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthComponent health() {
        return healthEndpoint.health();
    }
}
