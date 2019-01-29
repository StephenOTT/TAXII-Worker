package io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.common.EngineName;
import org.immutables.value.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract="*Model", typeImmutable="*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = FetchAndLock.class) @JsonDeserialize(builder = FetchAndLock.Builder.class)
public interface FetchAndLockModel extends EngineName {

    /**
     * Mandatory. The id of the worker on which behalf tasks are fetched.
     * The returned tasks are locked for that worker and can only be completed when providing the same worker id.
     * Defaults to "worker".
     * @return
     */
    @JsonProperty("workerId")
    @Value.Default
    @NotBlank
    default String getWorkerId(){
        return "worker";
    }

    /**
     * Mandatory. The maximum number of tasks to return.
     * Default to 10.
     * @return
     */
    @JsonProperty("maxTasks")
    @Value.Default
    @Positive
    default int getMaxTasks(){
        return 10;
    }

    /**
     * A boolean value, which indicates whether the task should be fetched based on its priority or arbitrarily.
     * Default to False.
     * @return
     */
    @JsonProperty("usePriority")
    @Value.Default
    default boolean getUsePriority(){
        return false;
    }

    /**
     * The Long Polling timeout in milliseconds.
     * Note: The value cannot be set larger than 1.800.000 milliseconds (corresponds to 30 minutes).
     * Use this when you want to use Long Polling.
     */
    @JsonProperty("asyncResponseTimeout")
    @Max(1800000) @Positive
    Optional<Long> getAsyncResponseTimeout();

    /**
     * A JSON array of topic objects for which external tasks should be fetched.
     * The returned tasks may be arbitrarily distributed among these topics.
     * Each topic object is a instance of {@link FetchAndLockTopicModel}
     * Requires minimum 1 topic configuration.
     * @return
     */
    @JsonProperty("topics")
    @Size(min = 1)
    Set<FetchAndLockTopicModel> getTopics();

}

