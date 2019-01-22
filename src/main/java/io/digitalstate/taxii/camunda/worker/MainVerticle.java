package io.digitalstate.taxii.camunda.worker;

import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskCircutBreaker;
import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskService;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.Complete;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.*;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerState;
import io.vertx.core.AbstractVerticle;

import java.util.Date;
import java.util.List;

public class MainVerticle extends AbstractVerticle {

    // Startup instance of External Task Service.  Vertx instance is passed through as the Vertx Web Client is used in the Service.
    private ExternalTaskService externalTaskService;

    @Override
    public void start() throws Exception {

        // Registers additional modules for Java8 Jackson usage
        JsonConfig.registerModules();

        externalTaskService = new ExternalTaskService(vertx)
                .setBaseUrl("http://localhost:8081");

        // Setup a Fetch and Lock configuration with a topic.
        FetchAndLock falConfig = FetchAndLock.builder()
                .addTopic(FetchAndLockTopic.builder()
                        .topicName("mytopic")
                        .build())
                .addTopic(FetchAndLockTopic.builder()
                        .topicName("someOtherSimilarTopic")
                        .build())
                .maxTasks(50)
                .usePriority(true)
                .workerId("central-worker")
                .build();

        CircuitBreaker breaker = new ExternalTaskCircutBreaker(vertx, 2000L, null).getCircuitBreaker();

        checkForTasks(breaker, falConfig);
        vertx.setPeriodic(5000, t->{
            System.out.println("Breaker state is: " + breaker.state().toString());
            if (breaker.state().equals(CircuitBreakerState.HALF_OPEN) || breaker.state().equals(CircuitBreakerState.OPEN)){
                System.out.println("Resetting breaker...");
                breaker.reset();

                checkForTasks(breaker,falConfig);
            }
        });


    }

    private void checkForTasks(CircuitBreaker breaker, FetchAndLockModel fetchAndLockModel){
         breaker.execute(future -> {
            System.out.println("Attempting to Fetch and Lock Tasks... " + new Date().toString());

            externalTaskService.fetchAndLock(fetchAndLockModel).setHandler(result -> {
                if (result.succeeded()) {
                    List<FetchAndLockResponseModel> tasks = result.result().getFetchedTasks();

                    if (!tasks.isEmpty()) {
                        future.complete();
                        tasks.forEach(this::doSomeWork);
                    } else {
                        System.out.println("No tasks found");
                        future.fail("No Tasks Found");
                    }
                } else {
                    System.out.println(result.cause().getMessage());
                    future.fail(result.cause());
                }
            });
        });
    }


    /*
     * Generic method that would represent some worker processor.
     * In practice this method would be replaced with other verticles and Event Bus messages.
     *
     */
    private void doSomeWork(FetchAndLockResponseModel task) {

        // ...
        // Some work would be done here with the task variable
        // ...
        System.out.println("WORK IS BEING DONE....");

        CompleteModel completionInfo = Complete.builder()
                .id(task.getId())
                .build();

        completeWork(completionInfo);
    }

    /*
     * In practice this method could be implemented on a per verticle basis,
     * allowing error handling to be dependent on how the processor wants to deal with errors.
     *
     */
    private void completeWork(CompleteModel completeModel) {
        externalTaskService.complete(completeModel).setHandler(completeResult -> {
            if (completeResult.succeeded()) {
                System.out.println(String.format("Task %s completed.", completeModel.getId()));
            } else {
                System.out.println(completeResult.cause().getMessage());
            }
        });
    }
}
