package io.digitalstate.taxii.camunda.client.externaltask;

import com.fasterxml.jackson.core.type.TypeReference;
import io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorResponse;
import io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror.HandleBpmnErrorResponseModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteResponse;
import io.digitalstate.taxii.camunda.client.externaltask.models.complete.CompleteResponseModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.failure.HandleFailureModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.failure.HandleFailureResponse;
import io.digitalstate.taxii.camunda.client.externaltask.models.failure.HandleFailureResponseModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.FetchAndLockModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.FetchAndLockResponseList;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.FetchAndLockResponseListModel;
import io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock.FetchAndLockResponseModel;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.List;
import java.util.Objects;

public class ExternalTaskService {

    private Vertx vertx;
    private WebClient client;

    private String fetchAndLockUri = "/fetchAndLock";
    private String completeUri = "/%s/complete";
    private String bpmnErrorUri = "/%s/bpmnError";
    private String failureUri = "/%s/failure";

    private String camundaRestUri = "/engine-rest";
    private String camundaExternalTaskUri = "/external-task";
    private String baseUrl = "http://localhost:8080";

    private MultiMap commonHeaders = new CaseInsensitiveHeaders();

    public ExternalTaskService(Vertx vertx) {
        this.vertx = vertx;
        client = WebClient.create(vertx);
    }

    public ExternalTaskService(Vertx vertx, WebClientOptions webClientOptions) {
        this.vertx = vertx;
        client = WebClient.create(vertx, webClientOptions);
    }

    /**
     * Fetch and Lock external task
     *
     * @param requestModel
     *
     * @return Future of FetchAndLockResponseListModel
     */
    public Future<FetchAndLockResponseListModel> fetchAndLock(FetchAndLockModel requestModel) {

        Future<FetchAndLockResponseListModel> future = Future.future();

        HttpRequest<Buffer> request = client.postAbs(getAbsoluteExternalTaskUrl() + fetchAndLockUri);
        request.headers().addAll(commonHeaders);
        request.sendJson(requestModel, ar -> {
            if (ar.succeeded()) {
                try {
                    System.out.println(ar.result().statusCode());
                    List<FetchAndLockResponseModel> list = Json.decodeValue(ar.result().body(),
                            new TypeReference<List<FetchAndLockResponseModel>>() {
                            });

                    FetchAndLockResponseList.Builder response = FetchAndLockResponseList.builder();

                    if (ar.result().statusCode() == 200) {
                        response.addAllFetchedTasks(list);
                    }

                    response.responseDetails(ar.result());
                    future.complete(response.build());

                } catch (Exception e) {
                    throw new IllegalStateException("Unable to parse external tasks.  Response was: " + ar.result().bodyAsString(), e);
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
     *
     * @return Future of CompleteResponseModel
     */
    public Future<CompleteResponseModel> complete(CompleteModel completeModel) {

        Future<CompleteResponseModel> response = Future.future();

        HttpRequest<Buffer> request = client.postAbs(String.format(getAbsoluteExternalTaskUrl() + completeUri, completeModel.getId()));
        request.headers().addAll(commonHeaders);
        request.sendJson(completeModel, ar -> {
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
     *
     * @param handleBpmnErrorModel
     *
     * @return Future of HandleBpmnErrorResponseModel
     */
    public Future<HandleBpmnErrorResponseModel> handleBpmnError(HandleBpmnErrorModel handleBpmnErrorModel) {

        Future<HandleBpmnErrorResponseModel> response = Future.future();

        HttpRequest<Buffer> request = client.postAbs(String.format(getAbsoluteExternalTaskUrl() + bpmnErrorUri, handleBpmnErrorModel.getId()));
        request.headers().addAll(commonHeaders);
        request.sendJson(handleBpmnErrorModel, ar -> {
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
     *
     * @return Future of HandleFailureResponseModel
     */
    public Future<HandleFailureResponseModel> handleFailure(HandleFailureModel handleFailureModel) {

        Future<HandleFailureResponseModel> response = Future.future();

        HttpRequest<Buffer> request = client.postAbs(String.format(getAbsoluteExternalTaskUrl() + failureUri, handleFailureModel.getId()));
        request.headers().addAll(commonHeaders);
        request.sendJson(handleFailureModel, ar -> {
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


    //
    // SETTERS AND GETTERS
    //

    public String getAbsoluteExternalTaskUrl(){
        return getAbsoluteBaseUrl() + getCamundaRestUri() + getCamundaExternalTaskUri();
    }

    public String getFetchAndLockUri() {
        return fetchAndLockUri;
    }

    public ExternalTaskService setFetchAndLockUri(String fetchAndLockUri) {
        Objects.requireNonNull(fetchAndLockUri);
        this.fetchAndLockUri = fetchAndLockUri;
        return this;
    }

    public String getCompleteUri() {
        return completeUri;
    }

    public ExternalTaskService setCompleteUri(String completeUri) {
        Objects.requireNonNull(completeUri);
        this.completeUri = completeUri;
        return this;
    }

    public String getBpmnErrorUri() {
        return bpmnErrorUri;
    }

    public ExternalTaskService setBpmnErrorUri(String bpmnErrorUri) {
        Objects.requireNonNull(bpmnErrorUri);
        this.bpmnErrorUri = bpmnErrorUri;
        return this;
    }

    public String getFailureUri() {
        return failureUri;
    }

    public ExternalTaskService setFailureUri(String failureUri) {
        Objects.requireNonNull(failureUri);
        this.failureUri = failureUri;
        return this;
    }

    public String getAbsoluteBaseUrl() {
        return baseUrl;
    }

    public ExternalTaskService setBaseUrl(String baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.baseUrl = baseUrl;
        return this;
    }

    public MultiMap getCommonHeaders() {
        return commonHeaders;
    }

    public ExternalTaskService setCommonHeaders(CaseInsensitiveHeaders commonHeaders) {
        Objects.requireNonNull(commonHeaders);
        this.commonHeaders = commonHeaders;
        return this;
    }

    public String getCamundaRestUri() {
        return camundaRestUri;
    }

    public ExternalTaskService setCamundaRestUri(String camundaRestUri) {
        this.camundaRestUri = camundaRestUri;
        return this;
    }

    public String getCamundaExternalTaskUri() {
        return camundaExternalTaskUri;
    }

    public ExternalTaskService setCamundaExternalTaskUri(String camundaExternalTaskUri) {
        this.camundaExternalTaskUri = camundaExternalTaskUri;
        return this;
    }
}
