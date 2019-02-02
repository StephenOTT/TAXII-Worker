package io.digitalstate.camunda.client.externaltask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.camunda.client.externaltask.json.CaseInsensitiveHeadersDeserializer;
import io.digitalstate.camunda.client.externaltask.json.CaseInsensitiveHeadersSerializer;
import io.vertx.core.MultiMap;
import io.vertx.core.http.CaseInsensitiveHeaders;

import java.util.Objects;

public class ExternalTaskOptions {

    private String fetchAndLockUri = "/fetchAndLock";
    private String completeUri = "/%s/complete";
    private String bpmnErrorUri = "/%s/bpmnError";
    private String failureUri = "/%s/failure";

    private String camundaRestUri = "/engine-rest";
    private String camundaExternalTaskUri = "/external-task";
    private String baseUrl = "http://localhost:8080";

    @JsonSerialize(using = CaseInsensitiveHeadersSerializer.class)
    @JsonDeserialize(using = CaseInsensitiveHeadersDeserializer.class)
    private MultiMap commonHeaders = new CaseInsensitiveHeaders();

    /**
     * Setup ExternalTaskOptions using defaults
     * Default baseUrl is http://localhost:8080
     */
    public ExternalTaskOptions(){

    }

    /**
     * Setup ExternalTaskOptions using defaults with a customized baseUrl.
     * baseUrl should be of format of {@code http://example.com:8080}
     *
     * @param baseUrl
     */
    public ExternalTaskOptions(String baseUrl){
        this.baseUrl = baseUrl;
    }


    /**
     * Get the absolute path of the External Task Rest Endpoint: BaseUrl + RestUi + ExternalTaskUri
     * @return
     */
    @JsonIgnore
    public String getAbsoluteExternalTaskUrl() {
        return getBaseUrl() + getCamundaRestUri() + getCamundaExternalTaskUri();
    }

    public String getFetchAndLockUri() {
        return this.fetchAndLockUri;
    }

    public ExternalTaskOptions setFetchAndLockUri(String fetchAndLockUri) {
        Objects.requireNonNull(fetchAndLockUri);
        this.fetchAndLockUri = fetchAndLockUri;
        return this;
    }

    public String getCompleteUri() {
        return this.completeUri;
    }

    public ExternalTaskOptions setCompleteUri(String completeUri) {
        Objects.requireNonNull(completeUri);
        this.completeUri = completeUri;
        return this;
    }

    public String getBpmnErrorUri() {
        return this.bpmnErrorUri;
    }

    public ExternalTaskOptions setBpmnErrorUri(String bpmnErrorUri) {
        Objects.requireNonNull(bpmnErrorUri);
        this.bpmnErrorUri = bpmnErrorUri;
        return this;
    }

    public String getFailureUri() {
        return this.failureUri;
    }

    public ExternalTaskOptions setFailureUri(String failureUri) {
        Objects.requireNonNull(failureUri);
        this.failureUri = failureUri;
        return this;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public ExternalTaskOptions setBaseUrl(String baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.baseUrl = baseUrl;
        return this;
    }


    /**
     * Get the headers that will be used on all External Task http requests.
     * @return MultiMap / {@link CaseInsensitiveHeaders} of common headers.
     */
    public MultiMap getCommonHeaders() {
        return this.commonHeaders;
    }

    /**
     * Sets the headers that should be used on all External Task http requests.
     * Commonly used when you want to setup things like Basic Auth or oAuth.
     * @param commonHeaders
     * @return
     */
    public ExternalTaskOptions setCommonHeaders(CaseInsensitiveHeaders commonHeaders) {
        Objects.requireNonNull(commonHeaders);
        this.commonHeaders = commonHeaders;
        return this;
    }

    public String getCamundaRestUri() {
        return this.camundaRestUri;
    }

    public ExternalTaskOptions setCamundaRestUri(String camundaRestUri) {
        this.camundaRestUri = camundaRestUri;
        return this;
    }

    public String getCamundaExternalTaskUri() {
        return this.camundaExternalTaskUri;
    }

    public ExternalTaskOptions setCamundaExternalTaskUri(String camundaExternalTaskUri) {
        this.camundaExternalTaskUri = camundaExternalTaskUri;
        return this;
    }

}
