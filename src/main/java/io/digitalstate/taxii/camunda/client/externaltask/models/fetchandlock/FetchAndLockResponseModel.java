package io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.common.HttpResponseDetails;
import io.digitalstate.taxii.camunda.client.variables.models.deserialization.VariableResponseModel;
import org.immutables.value.Value;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract="*Model", typeImmutable="*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = FetchAndLockResponse.class) @JsonDeserialize(builder = FetchAndLockResponse.Builder.class)
public interface FetchAndLockResponseModel extends HttpResponseDetails {

    /**
     * The id of the activity that this external task belongs to.
     * @return the activity id
     */
    @JsonProperty("activityId")
    String getActivityId();

    /**
     * The id of the activity instance that the external task belongs to.
     * @return
     */
    @JsonProperty("activityInstanceId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getActivityInstanceId();

    /**
     * The full error message submitted with the latest reported failure executing this task;
     * null if no failure was reported previously or if no error message was submitted
     * @return
     */
    @JsonProperty("errorMessage")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getErrorMessage();

    /**
     * The error details submitted with the latest reported failure executing this task.
     * null if no failure was reported previously or if no error details was submitted
     * @return
     */
    @JsonProperty("errorDetails")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getErrorDetails();

    /**
     * The id of the execution that the external task belongs to.
     * @return
     */
    @JsonProperty("executionId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getExecutionId();

    /**
     * The id of the external task.
     * @return
     */
    @JsonProperty("id")
    @NotNull
    String getId();

    /**
     * The date that the task's most recent lock expires or has expired.
     * @return
     */
    @JsonProperty("lockExpirationTime")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getLockExpirationTime();

    /**
     * The id of the process definition the external task is defined in.
     * @return
     */
    @JsonProperty("processDefinitionId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessDefinitionId();

    /**
     * The key of the process definition the external task is defined in.
     * @return
     */
    @JsonProperty("processDefinitionKey")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessDefinitionKey();

    /**
     * The id of the process instance the external task belongs to.
     * @return
     */
    @JsonProperty("processInstanceId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessInstanceId();

    /**
     * The id of the tenant the external task belongs to.
     * @return
     */
    @JsonProperty("tenantId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getTenantId();

    /**
     * The number of retries the task currently has left.
     * @return
     */
    @JsonProperty("retries")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<Integer> getRetries();

    /**
     * @TODO.  Currently missing in Camunda 7.10 API Docs
     * @return
     */
    @JsonProperty("suspended")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<Boolean> getSuspended();

    /**
     * The id of the worker that posesses or posessed the most recent lock.
     * @return
     */
    @JsonProperty("workerId")
    String getWorkerId();

    /**
     * The priority of the external task.
     * @return
     */
    @JsonProperty("priority")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getPriority();

    /**
     * The topic name of the external task.
     * @return
     */
    @JsonProperty("topicName")
    String getTopicName();

    /**
     * The business key of the process instance the external task belongs to.
     * @return
     */
    @JsonProperty("businessKey")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getBusinessKey();

    /**
     * A object containing a property for each of the requested variables.
     * The key is the variable name, the value is a object of serialized variable values
     * @return
     */
    @JsonProperty("variables")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Map<String, VariableResponseModel> getVariables();

}

