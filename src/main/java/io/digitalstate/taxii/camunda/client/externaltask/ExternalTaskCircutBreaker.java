package io.digitalstate.taxii.camunda.client.externaltask;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Vertx;

import javax.validation.constraints.NotNull;

public class ExternalTaskCircutBreaker {

    private CircuitBreaker circuitBreaker;

    /**
     * options and linearRetryValue can be null.
     * @param vertx
     * @param options
     */
    public ExternalTaskCircutBreaker(@NotNull Vertx vertx, CircuitBreakerOptions options){
        if (options == null){
           options = new CircuitBreakerOptions()
                   .setMaxFailures(4)
                   .setMaxRetries(4) // is actually 20 (0=1)
                   .setTimeout(120000);
        }

        CircuitBreaker breaker = CircuitBreaker.create("camunda-external-task-breaker", vertx, options)
                .openHandler(v -> {
                    System.out.println("camunda-external-task-breaker Circuit opened");
                })
                .closeHandler(v -> {
                    System.out.println("camunda-external-task-breaker Circuit closed");
                })
                .retryPolicy(retryCount -> retryCount * 2000L);

        circuitBreaker = breaker;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }
}
