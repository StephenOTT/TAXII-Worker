package io.digitalstate.taxii.camunda.worker;

import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskOptions;
import io.digitalstate.taxii.camunda.client.externaltask.ExternalTaskService;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.FetchAndLock;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.FetchAndLockTopic;
import io.digitalstate.taxii.camunda.worker.common.VertxObjectMapperConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;

public class MainVerticle extends AbstractVerticle {

    // Startup instance of External Task Service.  Vertx instance is passed through as the Vertx Web Client is used in the Service.
    private ExternalTaskService externalTaskService;

    @Override
    public void start() throws Exception {
        VertxObjectMapperConfig.registerModules();

        // Setup a Fetch and Lock configuration with a topic.
        FetchAndLock falConfig = FetchAndLock.builder()
                .addTopic(FetchAndLockTopic.builder()
                        .topicName("mytopic")
                        .addVariable("myVarObject")
                        .addVariable("myTestVar")
                        .addVariable("myBytes")
                        .addVariable("mySpin")
                        .build())
                .addTopic(FetchAndLockTopic.builder()
                        .topicName("someOtherSimilarTopic")
                        .build())
                .maxTasks(50)
                .usePriority(true)
                .workerId("central-worker")
                .asyncResponseTimeout(60000) // Camunda set to 5 min keepalive (note that its shorter than the Web Client).  THe Web Client is acting as the final fail safe / global value for that External Task Service Instance.
                .build();

        ExternalTaskOptions externalTaskOptions = new ExternalTaskOptions("http://localhost:8081");
//        externalTaskOptions.getCommonHeaders()
//                .add("x-test1", "some_header_value; 123")
//                .add("x-test2", "some-other-header-value");

        //@TODO create a method that builds this object for you
        JsonObject configObject = new JsonObject();
        configObject.put("externalTaskOptions", Json.encode(externalTaskOptions));
        configObject.put("fetchAndLockBody", Json.encode(falConfig));
        configObject.put("webClientOptions", Json.encode(new WebClientOptions().setKeepAliveTimeout(600).setHttp2KeepAliveTimeout(600).setKeepAlive(true))); // 10 min long polling
        //@TODO add config for Breaker Configs

        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(configObject)
                .setWorker(true); // Sets the verticle as a background worker so it has a dedicated thread because of the long polling possibly blocking the event-loop.

        vertx.deployVerticle(FetchAndLockVerticle.class, deploymentOptions, result -> {
            if (result.succeeded()) {
                System.out.println("Fetch and Lock Verticle has been Deployed under ID: " + result.result());
            } else {
                System.out.println("Deployment failed!");
                System.out.println(result.cause().getMessage());
                result.cause().printStackTrace();
            }
        });
    }
}
