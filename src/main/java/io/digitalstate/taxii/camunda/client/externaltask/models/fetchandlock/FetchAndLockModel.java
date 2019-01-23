package io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonProperty("workerId")
    @Value.Default
    @NotBlank
    default String getWorkerId(){
        return "worker";
    }

    /**
     * Default 10
     * @return
     */
    @JsonProperty("maxTasks")
    @Value.Default
    @Positive
    default int getMaxTasks(){
        return 10;
    }

    /**
     * Default False
     * @return
     */
    @JsonProperty("usePriority")
    @Value.Default
    default boolean getUsePriority(){
        return false;
    }

    /**
     * The async response timeout value in milliseconds.  Max value is 1,800,0000 (30 minutes)
     */
    @JsonProperty("asyncResponseTimeout")
    @Max(1800000) @Positive
    Optional<Long> getAsyncResponseTimeout();

    /**
     * Requires minimum 1 topic configuration.
     * @return
     */
    @JsonProperty("topics")
    @Size(min = 1)
    Set<FetchAndLockTopicModel> getTopics();

}

