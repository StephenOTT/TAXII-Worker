package io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.common.HttpResponseDetails;
import org.immutables.value.Value;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract="*Model", typeImmutable="*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = FetchAndLockResponse.class) @JsonDeserialize(builder = FetchAndLockResponse.Builder.class)
public interface FetchAndLockResponseModel extends HttpResponseDetails {

    @JsonProperty("activityId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getActivityId();

    @JsonProperty("activityInstanceId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getActivityInstanceId();

    @JsonProperty("errorMessage")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getErrorMessage();

    @JsonProperty("errorDetails")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getErrorDetails();

    @JsonProperty("executionId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getExecutionId();

    @JsonProperty("id")
    @NotNull
    String getId();

    @JsonProperty("lockExpirationTime")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getLockExpirationTime();

    @JsonProperty("processDefinitionId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessDefinitionId();

    @JsonProperty("processDefinitionKey")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessDefinitionKey();

    @JsonProperty("processInstanceId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessInstanceId();

    @JsonProperty("tenantId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getTenantId();

    @JsonProperty("retries")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<Integer> getRetries();

    @JsonProperty("suspended")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<Boolean> getSuspended();

    @JsonProperty("workerId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getWorkerId();

    @JsonProperty("priority")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getPriority();

    @JsonProperty("topicName")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getTopicName();

    @JsonProperty("businessKey")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getBusinessKey();

    @JsonProperty("variables")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Map<String, Object> getVariables();

}

