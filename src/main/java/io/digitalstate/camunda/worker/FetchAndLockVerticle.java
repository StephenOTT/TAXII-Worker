package io.digitalstate.camunda.worker;

import io.digitalstate.camunda.client.externaltask.ExternalTaskCircutBreaker;
import io.digitalstate.camunda.client.externaltask.ExternalTaskOptions;
import io.digitalstate.camunda.client.externaltask.ExternalTaskService;
import io.digitalstate.camunda.client.externaltask.models.complete.Complete;
import io.digitalstate.camunda.client.externaltask.models.complete.CompleteModel;
import io.digitalstate.camunda.client.externaltask.models.complete.CompleteResponseModel;
import io.digitalstate.camunda.client.externaltask.models.fetchandlock.FetchAndLockModel;
import io.digitalstate.camunda.client.externaltask.models.fetchandlock.FetchAndLockResponseModel;
import io.digitalstate.camunda.client.variables.models.types.FileVariable;
import io.digitalstate.camunda.client.variables.models.types.JsonVariable;
import io.digitalstate.camunda.worker.common.VertxObjectMapperConfig;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Note that this verticle will install ObjectMapper modules to {@link Json}
 * See: {@link VertxObjectMapperConfig}
 * This Verticle is designed to be used as standalone or as a base to extend and override.
 */
public class FetchAndLockVerticle extends AbstractVerticle {

    private ExternalTaskService externalTaskService;
    private EventBus bus = vertx.eventBus();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // Validate that the expected keys exist
        if (!validateConfigKeys()) {
            startFuture.fail("Missing required configs");
        }

        VertxObjectMapperConfig.registerModules();

        //@TODO Refactor:
        ExternalTaskOptions externalTaskOptions = Json.decodeValue(config().getString("externalTaskOptions"), ExternalTaskOptions.class);
        FetchAndLockModel fetchAndLockBody = Json.decodeValue(config().getString("fetchAndLockBody"), FetchAndLockModel.class);
        WebClientOptions webClientOptions = Json.decodeValue(config().getString("webClientOptions"), WebClientOptions.class);
        boolean useCircuitBreaker = config().getBoolean("useCircuitBreaker", true);

        externalTaskService = new ExternalTaskService(vertx, webClientOptions, externalTaskOptions);

        if (useCircuitBreaker) {
            CircuitBreaker breaker = new ExternalTaskCircutBreaker(vertx, null).getCircuitBreaker();
            startFuture.complete();
            fetchAndLockUsingBreaker(breaker, fetchAndLockBody);
        } else {
            startFuture.complete();
            fetchAndLockWithoutBreaker(fetchAndLockBody);
        }
    }

    /**
     * Implements Circuit Breaker with looping so as long as the Fetch and Lock is not throwing errors then the breaker will remain closed and endlessly loop.
     *
     * @param breaker
     * @param falConfig
     */
    private void fetchAndLockUsingBreaker(CircuitBreaker breaker, FetchAndLockModel falConfig) {
        breaker.execute(future -> {
            fetchAndLock(falConfig).setHandler(result -> {
                if (result.succeeded()) {
                    //@TODO logging
                    System.out.println(result.result());
                    future.complete();
                    fetchAndLockUsingBreaker(breaker, falConfig); // Creates loop, but we complete the future before looping.
                } else if (result.failed()) {
                    future.fail(result.cause());
                }
            });
        });
    }

    private void fetchAndLockWithoutBreaker(FetchAndLockModel falConfig) {
        fetchAndLock(falConfig).setHandler(result -> {
            if (result.succeeded()) {
                System.out.println(result.result());
                fetchAndLockWithoutBreaker(falConfig); // Creates loop, but we complete the future before looping.
            } else if (result.failed()) {
                System.out.println("FAIL: " + result.cause().getMessage());
                result.cause().printStackTrace();
            }
        });
    }


    /**
     * Single Use Fetch and Lock.  After tasks are fetched the future is completed and will not loop.
     *
     * @param fetchAndLockModel
     *
     * @return
     */
    private Future<List<FetchAndLockResponseModel>> fetchAndLock(FetchAndLockModel fetchAndLockModel) {

        Future<List<FetchAndLockResponseModel>> future = Future.future();
        //@TODO logging
        System.out.println("Attempting to Fetch and Lock Tasks... " + new Date().toString());

        externalTaskService.fetchAndLock(fetchAndLockModel)
                .setHandler(result -> {
                    if (result.succeeded()) {
                        List<FetchAndLockResponseModel> tasks = result.result().getFetchedTasks();
                        tasks.forEach(t->{
                            DeliveryOptions options = new DeliveryOptions();
                            bus.publish(t.getTopicName(), t, options);
                        });


                        future.complete(tasks);
                    } else {
                        future.fail(result.cause());
                    }
                });
        return future;
    }


    /*
     * In practice this method could be implemented on a per verticle basis,
     * allowing error handling to be dependent on how the processor wants to deal with errors.
     *
     */
    private Future<CompleteResponseModel> completeWork(CompleteModel completeModel) {
        Future<CompleteResponseModel> future = Future.future();

        System.out.println("Trying to Complete Task: " + completeModel.getId());
        externalTaskService.complete(completeModel).setHandler(completeResult -> {
            //@TODO logging
            if (completeResult.succeeded()) {
//                String message = String.format("Task %s completed.", completeModel.getId());
                future.complete(completeResult.result());
            } else {
                future.fail(completeResult.cause());
            }
        });
        return future;
    }

    private boolean validateConfigKeys() {
        //@TODO add null handling for keys
        //@TODO add a common config somewhere
        String[] requiredKeys = {"externalTaskOptions", "fetchAndLockBody", "webClientOptions"};
        for (String requiredKey : requiredKeys) {
            if (!config().containsKey(requiredKey)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Generic method that would represent some worker processor.
     * In practice this method would be replaced with other verticles and Event Bus messages.
     */
    private Future<Object> doSomeWork(FetchAndLockResponseModel task) {
        Future<Object> future = Future.future();

        // ...
        // Some work would be done here with the task variable
        // ...
        System.out.println("WORK IS BEING DONE....");
        //@TODO add some random sync delay to show that only N tasks will be executed/pulled at any one time.

        // Would add a If statement here that would throw a "future.fail(...)" if the actual work could not be compeleted.
        // Could also implement Handle BPMN Error or Handle Failure calls as well.

//        System.out.println("Executing Code over Graal: ");

//        String script = vertx.fileSystem().readFileBlocking("...").toString(Charset.forName("UTF-8"));
//        String pyScript = vertx.fileSystem().readFileBlocking("...").toString(Charset.forName("UTF-8"));

//        new PolymorphicExecutor("js", script, "myJsScript");
//        new PolymorphicExecutor("python", pyScript, "myPythonScript");

//        System.out.println(task.getVariables().toString());
//        System.out.println(task.getVariables().get("myVarObject").getValueTyped().getClass().getCanonicalName());
//        System.out.println(task.getVariables().get("myBytes").getValueTyped());
//        System.out.println(task.getVariables().get("myVar").getValueTyped().getClass().getCanonicalName());

        System.out.println(task.getVariables().get("mySpin").getTypedValue().getClass().getCanonicalName());

        // Build the completion object:
        String myValue = "dogs!";
        CompleteModel completionInfo = Complete.builder()
                .id(task.getId())
                .workerId(task.getWorkerId())
                .putVariable("someFile", FileVariable.of(myValue.getBytes(), "dogssss.txt").withIsTransient(false))
                .putVariable("myJson", JsonVariable.of("{\"dog\":\"cat\"}"))
                .build();

        completeWork(completionInfo).setHandler(result -> {
            if (result.succeeded()) {
                future.complete(result.result());

            } else if (result.failed()) {
                result.cause().printStackTrace();
                future.fail(result.cause());
            }
        });

        return future;
    }
}
