package com.itau.challenge_localization_api.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for tracking custom metrics related to pet location operations.
 * Uses Spring Boot Actuator's Micrometer for simple metrics collection.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationMetricsService {

    private final MeterRegistry meterRegistry;

    /**
     * Increment total location requests counter.
     */
    public void incrementLocationRequests() {
        Counter.builder("pet.location.requests.total")
                .description("Total number of pet location requests")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented location requests counter");
    }

    /**
     * Increment successful location requests counter.
     */
    public void incrementLocationRequestsSuccess() {
        Counter.builder("pet.location.requests.success")
                .description("Total number of successful pet location requests")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented successful location requests counter");
    }

    /**
     * Increment failed location requests counter.
     *
     * @param errorType the type of error that occurred
     */
    public void incrementLocationRequestsError(String errorType) {
        Counter.builder("pet.location.requests.error")
                .description("Total number of failed pet location requests")
                .tag("error.type", errorType)
                .register(meterRegistry)
                .increment();
        log.debug("Incremented error location requests counter for error type: {}", errorType);
    }

    /**
     * Create a timer for measuring location request duration.
     *
     * @return Timer.Sample to be stopped when operation completes
     */
    public Timer.Sample startLocationRequestTimer() {
        Timer timer = Timer.builder("pet.location.request.duration")
                .description("Duration of pet location requests")
                .register(meterRegistry);
        return Timer.start(meterRegistry);
    }

    /**
     * Stop a timer and record the duration.
     *
     * @param timerSample the timer sample to stop
     */
    public void stopTimer(Timer.Sample timerSample) {
        Timer timer = meterRegistry.find("pet.location.request.duration").timer();
        if (timer != null) {
            timerSample.stop(timer);
            log.debug("Stopped location request timer");
        }
    }
}
