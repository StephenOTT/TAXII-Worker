package io.digitalstate.taxii.camunda.worker;

import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskCircutBreaker;
import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskOptions;
import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskService;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.Complete;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.*;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.Date;
import java.util.List;

public class MainVerticle extends AbstractVerticle {

    // Startup instance of External Task Service.  Vertx instance is passed through as the Vertx Web Client is used in the Service.
    private ExternalTaskService externalTaskService;

    @Override
    public void start() throws Exception {

        // Registers additional modules for Java8 Jackson usage
        JsonConfig.registerModules();

        WebClientOptions wcOptions = new WebClientOptions()
                .setKeepAliveTimeout(600); // Web Client set to 10min mix keep alive

        externalTaskService = new ExternalTaskService(vertx,
                wcOptions,
                new ExternalTaskOptions("http://localhost:8081"));

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
                .asyncResponseTimeout(300000) // Camunda set to 5 min keepalive (note that its shorter than the Web Client).  THe Web Client is acting as the final fail safe / global value for that External Task Service Instance.
                .build();

        // Breaker is used for Circuit Breaker pattern in-case Camunda is unreachable or is providing error responses
        CircuitBreaker breaker = new ExternalTaskCircutBreaker(vertx, null).getCircuitBreaker();

        fetchAndLockUsingBreaker(breaker, falConfig);

    }

    /**
     * Implements Circuit Breaker with looping so as long as the Fetch and Lock is not throwing errors then the breaker will remain closed and endlessly loop.
     * @param breaker
     * @param falConfig
     */
    private void fetchAndLockUsingBreaker(CircuitBreaker breaker, FetchAndLockModel falConfig){
        breaker.execute(future -> {
            Future<Object> tasksCheckResult = fetchAndLock(falConfig);
            tasksCheckResult.setHandler(result -> {
                if (result.succeeded()) {
                    future.complete();
                    fetchAndLockUsingBreaker(breaker, falConfig);
                } else if (result.failed()) {
                    future.fail(result.cause());
                }
            });
        });
    }

    /**
     * Single Use Fetch and Lock.  After tasks are fetched the future is completed and will not loop.
     * @param fetchAndLockModel
     * @return
     */
    private Future<Object> fetchAndLock(FetchAndLockModel fetchAndLockModel) {
        Future<Object> future = Future.future();

        System.out.println("Attempting to Fetch and Lock Tasks... " + new Date().toString());
        externalTaskService.fetchAndLock(fetchAndLockModel).setHandler(result -> {
            if (result.succeeded()) {
                List<FetchAndLockResponseModel> tasks = result.result().getFetchedTasks();

                if (!tasks.isEmpty()) {
                    tasks.forEach(this::doSomeWork);
                    future.complete(tasks.size() + " Tasks found and executed");

                } else {
                    future.complete("No tasks found.");
                }
            } else {
                future.fail(result.cause());
            }
        });
        return future;
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


        // Build the completion object:
        CompleteModel completionInfo = Complete.builder()
                .id(task.getId())
                .workerId(task.getWorkerId())
                .build();

        completeWork(completionInfo).setHandler(result->{
            if (result.succeeded()){
                System.out.println(result.result());

                // If Task could not be completed
            } else if (result.failed()){
                System.out.println(result.cause().getMessage());
            }
        });
    }

    /*
     * In practice this method could be implemented on a per verticle basis,
     * allowing error handling to be dependent on how the processor wants to deal with errors.
     *
     */
    private Future<String> completeWork(CompleteModel completeModel) {
        Future<String> future = Future.future();

        externalTaskService.complete(completeModel).setHandler(completeResult -> {
            if (completeResult.succeeded()) {
                String message = String.format("Task %s completed.", completeModel.getId());
                future.complete(message);
            } else {
                future.fail(completeResult.cause());
            }
        });
        return future;
    }
}
