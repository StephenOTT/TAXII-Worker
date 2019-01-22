package io.digitalstate.taxii.camunda.client.externaltask;

import com.fasterxml.jackson.core.type.TypeReference;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteResponse;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteResponseModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.*;
import io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorResponse;
import io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorResponseModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.failure.HandleFailureModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.failure.HandleFailureResponse;
import io.digitalstate.taxii.camunda.client.externaltask.models.failure.HandleFailureResponseModel;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;

import java.util.List;

public class ExternalTaskService {

    private Vertx vertx;
    private WebClient client;

    public ExternalTaskService(Vertx vertx) {
        this.vertx = vertx;
        client = WebClient.create(vertx);
    }

    /**
     * Fetch and Lock external task
     *
     * @param requestModel
     * @return Future of FetchAndLockResponseListModel
     */
    public Future<FetchAndLockResponseListModel> fetchAndLock(FetchAndLockModel requestModel) {

        Future<FetchAndLockResponseListModel> future = Future.future();

        client.postAbs(requestModel.getHttpConfig().getAbsoluteUri())
                .sendJson(requestModel, ar -> {
                    if (ar.succeeded()) {
                        try {
                            List<FetchAndLockResponseModel> list = Json.decodeValue(ar.result().body(),
                                    new TypeReference<List<FetchAndLockResponseModel>>() {});

                            FetchAndLockResponseList.Builder response = FetchAndLockResponseList.builder();

                            if (ar.result().statusCode() == 200) {
                                response.addAllFetchedTasks(list);
                            }

                            response.responseDetails(ar.result());
                            future.complete(response.build());

                        } catch (Exception e) {
                            throw new IllegalStateException("Unable to parse external tasks", e);
                        }
                    } else {
                        throw new IllegalStateException("Unable to execute fetch-and-lock request for external tasks", ar.cause());
                    }
                });

        return future;
    }

    /**
     * Complete External Task
     *
     * @param completeModel
     * @return Future of CompleteResponseModel
     */
    public Future<CompleteResponseModel> complete(CompleteModel completeModel) {

        Future<CompleteResponseModel> response = Future.future();

        client.postAbs(completeModel.getHttpConfig().getAbsoluteUri())
                .sendJson(completeModel, ar -> {
                    if (ar.succeeded()) {
                        CompleteResponseModel result = CompleteResponse.builder()
                                .responseDetails(ar.result())
                                .build();
                        response.complete(result);

                    } else {
                        throw new IllegalStateException("Unable to execute Completion request for external task", ar.cause());
                    }
                });

        return response;
    }

    /**
     * Handle a BPMN Error. Reports a BPMN Error of a external task back to Camunda.
     * @param handleBpmnErrorModel
     * @return Future of HandleBpmnErrorResponseModel
     */
    public Future<HandleBpmnErrorResponseModel> handleBpmnError(HandleBpmnErrorModel handleBpmnErrorModel) {

        Future<HandleBpmnErrorResponseModel> response = Future.future();

        client.postAbs(handleBpmnErrorModel.getHttpConfig().getAbsoluteUri())
                .sendJson(handleBpmnErrorModel, ar -> {
                    if (ar.succeeded()) {
                        HandleBpmnErrorResponseModel result = HandleBpmnErrorResponse.builder()
                                .responseDetails(ar.result())
                                .build();
                        response.complete(result);
                    } else {
                        throw new IllegalStateException("Unable to execute Handle-Bpmn-Error request for external task", ar.cause());
                    }
                });

        return response;
    }

    /**
     * Handle a Failure: Reports a failure of the execution of a external task back to Camunda.
     *
     * @param handleFailureModel
     * @return Future of HandleFailureResponseModel
     */
    public Future<HandleFailureResponseModel> handleFailure(HandleFailureModel handleFailureModel) {

        Future<HandleFailureResponseModel> response = Future.future();

        client.postAbs(handleFailureModel.getHttpConfig().getAbsoluteUri())
                .sendJson(handleFailureModel, ar -> {
                    if (ar.succeeded()) {
                        HandleFailureResponseModel result = HandleFailureResponse.builder()
                                .responseDetails(ar.result())
                                .build();
                        response.complete(result);
                    } else {
                        throw new IllegalStateException("Unable to execute Handle-Failure request for external task", ar.cause());
                    }
                });

        return response;
    }

}
