package com.example.demo.observability;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "app.otel.debug-logging", name = "enabled", havingValue = "true")
public class OtelDebugLogging extends OncePerRequestFilter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OtelDebugLogging.class);

    private final Tracer tracer;
    private final String serviceName;
    private final String environment;
    private final String tracesEndpoint;
    private final double samplingProbability;

    public OtelDebugLogging(
            Tracer tracer,
            @Value("${spring.application.name}") String serviceName,
            @Value("${management.opentelemetry.resource-attributes.deployment.environment:unknown}") String environment,
            @Value("${management.otlp.tracing.endpoint}") String tracesEndpoint,
            @Value("${management.tracing.sampling.probability}") double samplingProbability) {
        this.tracer = tracer;
        this.serviceName = serviceName;
        this.environment = environment;
        this.tracesEndpoint = tracesEndpoint;
        this.samplingProbability = samplingProbability;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("OTEL debug logging enabled: serviceName={}, environment={}, tracesEndpoint={}, samplingProbability={}",
                serviceName, environment, tracesEndpoint, samplingProbability);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startedAt = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            Span span = tracer.currentSpan();
            Map<String, String> trace = traceFields(span);
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            log.info("HTTP trace method={} uri={} status={} durationMs={} traceId={} spanId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs,
                    trace.get("traceId"),
                    trace.get("spanId"));
        }
    }

    private static Map<String, String> traceFields(Span span) {
        if (span == null) {
            return Map.of("traceId", "none", "spanId", "none");
        }
        return Map.of(
                "traceId", span.context().traceId(),
                "spanId", span.context().spanId());
    }
}
