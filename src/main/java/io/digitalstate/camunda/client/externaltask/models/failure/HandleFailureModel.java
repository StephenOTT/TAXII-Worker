package io.digitalstate.camunda.client.externaltask.models.failure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.camunda.client.common.EngineName;
import org.immutables.value.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = HandleFailure.class) @JsonDeserialize(builder = HandleFailure.Builder.class)
public interface HandleFailureModel extends EngineName {

    /**
     * The Id of the task to be completed.
     * Used in in the path params.
     * Excluded from JSON.
     * @return
     */
    @JsonIgnore
    @NotBlank
    String getId();

    @JsonProperty("workerId")
    @Value.Default
    @NotBlank
    default String getWorkerId(){
        return "worker";
    }

    @JsonProperty("errorMessage")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    String getErrorMessage();

    @JsonProperty("errorDetails")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getErrorDetails();

    /**
     * Must be >= 0.  Defaults to 3.
     * @return
     */
    @JsonProperty("retires")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @Min(0)
    @Value.Default
    default int getRetries(){
     return 10;
    }

    /**
     * A timeout in milliseconds before the external task becomes available again for fetching.
     * Must be >= 0.
     * @return
     */
    @JsonProperty("retryTimeout")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @Min(0)
    Optional<Long> getRetryTimeout();

}
