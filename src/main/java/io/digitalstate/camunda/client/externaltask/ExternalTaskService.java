package io.digitalstate.camunda.client.externaltask;

import com.fasterxml.jackson.core.type.TypeReference;
import io.digitalstate.camunda.client.common.CamundaErrorResponse;
import io.digitalstate.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorModel;
import io.digitalstate.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorResponse;
import io.digitalstate.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorResponseModel;
import io.digitalstate.camunda.client.externaltask.models.complete.CompleteModel;
import io.digitalstate.camunda.client.externaltask.models.complete.CompleteResponse;
import io.digitalstate.camunda.client.externaltask.models.complete.CompleteResponseModel;
import io.digitalstate.camunda.client.externaltask.models.failure.HandleFailureModel;
import io.digitalstate.camunda.client.externaltask.models.failure.HandleFailureResponse;
import io.digitalstate.camunda.client.externaltask.models.failure.HandleFailureResponseModel;
import io.digitalstate.camunda.client.externaltask.models.fetchandlock.FetchAndLockModel;
import io.digitalstate.camunda.client.externaltask.models.fetchandlock.FetchAndLockResponseList;
import io.digitalstate.camunda.client.externaltask.models.fetchandlock.FetchAndLockResponseListModel;
import io.digitalstate.camunda.client.externaltask.models.fetchandlock.FetchAndLockResponseModel;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.List;

public class ExternalTaskService {

    private Vertx vertx;
    private WebClient client;
    private ExternalTaskOptions externalTaskOptions;

    /**
     * Sets up External Task Service using default configurations for WebClient and Camunda API Endpoints.
     * Uses http://localhost:8080 as baseUrl and /engine-rest as the REST URI of Camunda.
     *
     * @param vertx Vertx Instance
     */
    public ExternalTaskService(Vertx vertx) {
        this.vertx = vertx;
        this.client = WebClient.create(vertx);
        this.externalTaskOptions = new ExternalTaskOptions();
    }

    /**
     * Sets up External Task Service using provided configurations.
     *
     * @param vertx               Vertx Instance
     * @param webClientOptions    WebClientOptions for the Vertx WebClient
     * @param externalTaskOptions ExternalTaskOptions instance which defines External Task configurations such as baseUrl and various endpoints.
     */
    public ExternalTaskService(Vertx vertx, WebClientOptions webClientOptions, ExternalTaskOptions externalTaskOptions) {
        this.vertx = vertx;
        this.client = WebClient.create(vertx, webClientOptions);
        this.externalTaskOptions = externalTaskOptions;
    }

    /**
     * Fetch and Lock external task
     *
     * @param requestModel
     *
     * @return Future of FetchAndLockResponseListModel
     */
    public Future<FetchAndLockResponseListModel> fetchAndLock(FetchAndLockModel requestModel) throws CamundaErrorResponse, IllegalStateException {
        Future<FetchAndLockResponseListModel> response = Future.future();

        System.out.println("Setting up Fetch and Lock...");
        HttpRequest<Buffer> request = client.postAbs(externalTaskOptions.getAbsoluteExternalTaskUrl() + externalTaskOptions.getFetchAndLockUri());
        request.headers().addAll(externalTaskOptions.getCommonHeaders());
        request.sendJson(requestModel, ar -> {
            if (ar.succeeded()) {
                switch (ar.result().statusCode()) {
                    case 200:
                        try {
                            List<FetchAndLockResponseModel> list = Json.decodeValue(ar.result().body(),
                                    new TypeReference<List<FetchAndLockResponseModel>>() {
                                    });

                            FetchAndLockResponseList listObject = FetchAndLockResponseList.builder()
                                    .addAllFetchedTasks(list)
                                    .responseDetails(ar.result()).build();

                            response.complete(listObject);

                        } catch (Exception e) {
                            throw new IllegalStateException("Unable to parse external tasks.  Response was: " + ar.result().bodyAsString(), e);
                        }
                        break;
                    case 400:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 400: Returned if the task's most recent lock was not acquired by the provided worker. See the Introduction for the error response format."));
                    case 404:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 404: Returned if the task does not exist. This could indicate a wrong task id as well as a cancelled task, e.g., due to a caught BPMN boundary event. See the Introduction for the error response format."));
                    case 500:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 500: Returned if the corresponding process instance could not be resumed successfully. See the Introduction for the error response format."));
                    default:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a unexpected response."));
                }
            } else {
                throw new IllegalStateException("Unable to execute fetch-and-lock request for external tasks", ar.cause());
            }
        });

        return response;
    }

    /**
     * Complete External Task
     *
     * @param completeModel
     *
     * @return Future of CompleteResponseModel
     */
    public Future<CompleteResponseModel> complete(CompleteModel completeModel) throws CamundaErrorResponse, IllegalStateException {

        Future<CompleteResponseModel> response = Future.future();

        HttpRequest<Buffer> request = client.postAbs(String.format(externalTaskOptions.getAbsoluteExternalTaskUrl() + externalTaskOptions.getCompleteUri(), completeModel.getId()));
        request.headers().addAll(externalTaskOptions.getCommonHeaders());
        request.sendJson(completeModel, ar -> {
            if (ar.succeeded()) {
                switch (ar.result().statusCode()) {
                    case 204:
                        CompleteResponseModel result = CompleteResponse.builder()
                                .responseDetails(ar.result())
                                .build();
                        response.complete(result);
                        break;
                    case 400:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 400: Returned if the task's most recent lock was not acquired by the provided worker. See the Introduction for the error response format."));
                    case 404:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 404: Returned if the task does not exist. This could indicate a wrong task id as well as a cancelled task, e.g., due to a caught BPMN boundary event. See the Introduction for the error response format."));
                    case 500:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 500: Returned if the corresponding process instance could not be resumed successfully. See the Introduction for the error response format."));
                    default:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a unexpected response."));
                }
            } else {
                throw new IllegalStateException("Unable to execute Completion request for external task", ar.cause());
            }
        });

        return response;
    }

    /**
     * Handle a BPMN Error. Reports a BPMN Error of a external task back to Camunda.
     *
     * @param handleBpmnErrorModel
     *
     * @return Future of HandleBpmnErrorResponseModel
     */
    public Future<HandleBpmnErrorResponseModel> handleBpmnError(HandleBpmnErrorModel handleBpmnErrorModel) throws CamundaErrorResponse, IllegalStateException {

        Future<HandleBpmnErrorResponseModel> response = Future.future();

        HttpRequest<Buffer> request = client.postAbs(String.format(externalTaskOptions.getAbsoluteExternalTaskUrl() + externalTaskOptions.getBpmnErrorUri(), handleBpmnErrorModel.getId()));
        request.headers().addAll(externalTaskOptions.getCommonHeaders());
        request.sendJson(handleBpmnErrorModel, ar -> {
            if (ar.succeeded()) {
                switch (ar.result().statusCode()) {
                    case 204:
                        HandleBpmnErrorResponseModel result = HandleBpmnErrorResponse.builder()
                                .responseDetails(ar.result())
                                .build();
                        response.complete(result);
                        break;
                    case 400:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 400: Returned if the task's most recent lock was not acquired by the provided worker. See the Introduction for the error response format."));
                    case 404:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 404: Returned if the task does not exist. This could indicate a wrong task id as well as a cancelled task, e.g., due to a caught BPMN boundary event. See the Introduction for the error response format."));
                    case 500:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 500: Returned if the corresponding process instance could not be resumed successfully. See the Introduction for the error response format."));
                    default:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a unexpected response."));
                }
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
     *
     * @return Future of HandleFailureResponseModel
     */
    public Future<HandleFailureResponseModel> handleFailure(HandleFailureModel handleFailureModel) throws CamundaErrorResponse, IllegalStateException {

        Future<HandleFailureResponseModel> response = Future.future();

        HttpRequest<Buffer> request = client.postAbs(String.format(externalTaskOptions.getAbsoluteExternalTaskUrl() + externalTaskOptions.getFailureUri(), handleFailureModel.getId()));
        request.headers().addAll(externalTaskOptions.getCommonHeaders());
        request.sendJson(handleFailureModel, ar -> {
            if (ar.succeeded()) {
                switch (ar.result().statusCode()) {
                    case 204:
                        HandleFailureResponseModel result = HandleFailureResponse.builder()
                                .responseDetails(ar.result())
                                .build();
                        response.complete(result);
                        break;
                    case 400:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 400: Returned if the task's most recent lock was not acquired by the provided worker. See the Introduction for the error response format."));
                    case 404:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 404: Returned if the task does not exist. This could indicate a wrong task id as well as a cancelled task, e.g., due to a caught BPMN boundary event. See the Introduction for the error response format."));
                    case 500:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a 500: Returned if the corresponding process instance could not be resumed successfully. See the Introduction for the error response format."));
                    default:
                        response.fail(new CamundaErrorResponse(ar.result().statusCode(), ar.result().bodyAsString(), "Received a unexpected response."));
                }
            } else {
                throw new IllegalStateException("Unable to execute Handle-Failure request for external task", ar.cause());
            }
        });

        return response;
    }

}
