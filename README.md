# TAXII-Worker

External Task Worker for TAXII-springboot-bpmn.

This is a generic task worker that can execute scripts in different polymorphic languages as determined by the execution env and worker configurations.
Scripts can be injected during runtime allowing a flexible architecture.

Example of a BPMN that would be executed on the TAXII-springboot-bpmn Server, and the worker(s) would Fetch and Lock, and then complete the work as orchestrated by Camunda.

![bpmn example](./docs/bpmn/example_taxii_worker.png)

Using External workers allow execution of TAXII and STIX data evaluations and actions to be done in any language.  
This worker acts as a universal bridge example allowing a unified worker framework to execute scripting in various polymorphic languages providing flexibility to downstream Cyber Analysts.

## Benefits of using Worker patterns:

1. **Crossing System Boundaries**: An external worker does not need to run in the same Java process, on the same machine, in the same cluster or even on the same continent as the workflow/process engine. All that is required is that it can access the process engine’s API (via REST or Java). Due to the polling pattern, the worker does not need to expose any interface for the process engine to access.
1. **Crossing Technology Boundaries**: An external worker does not need to be implemented in Java. Instead, any technology can be used that is most suitable to perform a work item and that can be used to access the process engine’s API (via REST or Java).  The TAXII-Worker provides a layer of abstraction around this need, so that all of the interactions with the server are handled, and only the core scripting is required by the end-user/developer/analyst.
1. **Specialized Workers**: An external worker does not need to be a general purpose application. Each external task instance receives a topic name identifying the nature of the task to perform. Workers can poll tasks for only those topics that they can work on.  The TAXII-Worker framework allows multiple instances to be added into the network and automatic connectivity within the cluster is provided through Vertx's Event Bus.
1. **Fine-Grained Scaling**: If there is high load concentrated on external task processing, the number of external workers for the respective topics can be scaled out independently of the process engine.  If a specific threat is active and requires additional resources, the worker for that threat can be stood-up and scaled independently of the rest of the network of workers and the process engine.
1. **Independent Maintenance**: Workers can be maintained independently of the process engine without breaking operations. For example, if a worker for a specific topic has a downtime (e.g., due to an update), there is no immediate impact on the process engine. Execution of external tasks for such workers degrades gracefully: They are stored in the external task list until the external worker resumes operation.


# Current Workflow Actions Supported

## Fetch and Lock

```java
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
```

## Complete

```java
externalTaskService.complete(completeModel).setHandler(completeResult -> {
            if (completeResult.succeeded()) {
                System.out.println(String.format("Task %s completed.", completeModel.getId()));
            } else {
                System.out.println(completeResult.cause().getMessage());
            }
        });
```

## Handle BPMN Error

```java
externalTaskService.handleBpmnError(handleBpmnErrorModel)
```

## Handle Failure

```java
externalTaskService.handleFailure(handleFailureModel)
```

All methods return a `Future<>` so you can implement the `.setHandler()` method to action when a result is returned by the async code:

```java
externalTaskService.complete(completeModel).setHandler(completeResult -> {
        if (completeResult.succeeded()) {
            System.out.println(String.format("Task %s completed.", completeModel.getId()));
        } else {
            System.out.println(completeResult.cause().getMessage());
        }
    });
```

As long as the Web Client was able to make a successful http connection, the Future will complete.
It is up to your implementations to evaluate if the Future was a success or failure.  In the case of a Failure, the returned `Throwable` will be a instance of CamundaErrorResponse.class.
The HTTP Status Code, the raw response body, and a in-code defined message are provided for context about what went wrong.

Example usage is, if you were using the fetchAndLock(), and camunda returned a status code other than 200, then you will get failed `Future`, as `200` is the defined status code for a successful request as per Camunda API spec docs.


# Example usage

...

# A circut breaker can be implemented using the ExternalTaskCircuitBreaker class:

```java

CircuitBreaker breaker = new ExternalTaskCircutBreaker(vertx, null).getCircuitBreaker();

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

```

# Larger scale usage

The usage of vertx should be that the Vertx Event Bus is leveraged to pass tasks around.  All the Immutables has been setup to be serializable as Json using vertx's jackson `Json.encode()`.

A verticle can be setup to collect tasks and send them to other verticles through the event bus to process the work.

This enables other verticles to not to have worry about Camunda specific information and details.

Further you could also have each verticle long poll or jitter poll camunda and process everything within the verticle it self.

The current thinking is that centralized polling with pushing the work to individual verticles through the event bus is optimial as it scales the best without having to increase the load of polling on the Camunda server.


# Known Bugs

[CAM-9562](https://app.camunda.com/jira/browse/CAM-9562) - Camunda Versions: 7.10.0, 7.9.6, 7.11.9: Long Polling that has a connection terminated will cause tasks to be locked automatically without the worker receiving them.  This is a bug with Camunda and not TAXII-Worker.