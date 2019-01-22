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
     * @param linearRetryValue
     */
    public ExternalTaskCircutBreaker(@NotNull Vertx vertx, @NotNull final Long linearRetryValue, CircuitBreakerOptions options){
        if (options == null){
           options = new CircuitBreakerOptions()
                   .setMaxFailures(1)
                   .setResetTimeout(1000)
                   .setMaxRetries(4) // is actually 5 (0=1)
                   .setTimeout(60000);
        }

        CircuitBreaker breaker = CircuitBreaker.create("camunda-external-task-breaker", vertx, options
        ).openHandler(v -> {
            System.out.println("Circuit opened");
        }).closeHandler(v -> {
            System.out.println("Circuit closed");
        });

        breaker.retryPolicy(retryCount -> retryCount * linearRetryValue);
        circuitBreaker = breaker;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }
}
