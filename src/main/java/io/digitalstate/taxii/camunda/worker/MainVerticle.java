package io.digitalstate.taxii.camunda.worker;

import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskService;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.Complete;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.HttpResponse;

import java.util.List;

public class MainVerticle extends AbstractVerticle {

    // Startup instance of External Task Service.  Vertx instance is passed through as the Vertx Web Client is used in the Service.
    private ExternalTaskService externalTaskService;

    @Override
    public void start() throws Exception {

        // Registers additional modules for Java8 Jackson usage
        JsonConfig.registerModules();

        externalTaskService = new ExternalTaskService(vertx);

        // Setup a Fetch and Lock configuration with a topic.
        FetchAndLock falConfig = FetchAndLock.builder()
                .addTopic(FetchAndLockTopic.builder()
                            .topicName("mytopic")
                            .build())
                .addTopic(FetchAndLockTopic.builder()
                            .topicName("someOtherSimilarTopic")
                            .build())
                .addTopics(FetchAndLockTopic.builder()
                            .topicName("t1")
                            .build(),
                           FetchAndLockTopic.builder()
                            .topicName("t2")
                            .build())
                .build();

        externalTaskService.fetchAndLock(falConfig).setHandler(result -> {
            HttpResponse httpDetails = ((HttpResponse) result.result().getResponseDetails()
                    .orElseThrow(() -> new IllegalStateException("No HTTP Response object was returned!")));

            if (httpDetails.statusCode() == 200) {
                List<FetchAndLockResponseModel> tasks = result.result().getFetchedTasks();

                if (!tasks.isEmpty()) {
                    tasks.forEach(this::doSomeWork);
                } else {
                    System.out.println("No tasks found");
                }
            } else {
                throw new IllegalStateException("Server did not return status code 200.  Returned: " + httpDetails.statusCode() + " : response body: " + httpDetails.bodyAsString());
            }
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
    private void completeWork(CompleteModel completeModel){
        externalTaskService.complete(completeModel).setHandler(completeResult -> {
            HttpResponse response = ((HttpResponse) completeResult.result().getResponseDetails()
                    .orElseThrow(() -> new IllegalStateException("No HTTP Response object was returned!")));

            if (response.statusCode() == 204) {
                System.out.println("COMPLETED!");
            } else {
                System.out.println("FAILED!");
            }
        });
    }
}