package io.digitalstate.camunda.client.externaltask.models.fetchandlock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract="*Model", typeImmutable="*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = FetchAndLockTopic.class) @JsonDeserialize(builder = FetchAndLockTopic.Builder.class)
public interface FetchAndLockTopicModel {

    @JsonProperty("topicName")
    @NotBlank
    String getTopicName();

    @JsonProperty("lockDuration")
    @Positive
    @Value.Default
    default long getLockDuration(){
        return 300000L;
    }

    @JsonProperty("variables")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Set<String> variables();

    @JsonProperty("localVariables")
    @Value.Default
    default boolean getLocalVariables(){
        return false;
    }

    @JsonProperty("businessKey")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getBusinessKey();

    @JsonProperty("processDefinitionId")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessDefinitionId();

    @JsonProperty("processDefinitionIdIn")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Set<String> getProcessDefinitionIdIn();

    @JsonProperty("processDefinitionKey")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getProcessDefinitionKey();

    @JsonProperty("processDefinitionKeyIn")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Set<String> getProcessDefinitionKeyIn();

    @JsonProperty("withoutTenantId")
    @Value.Default
    default boolean getWithoutTenantId(){
        return false;
    }

    @JsonProperty("tenantIdIn")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Set<String> getTenantIdIn();

    //@TODO pull in variable support from Camunda lib
    @JsonProperty("processVariables")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Map<String, Object> getProcessVariables();

    @JsonProperty("deserializeValues")
    @Value.Default
    default boolean getDeserializeValues(){
        return false;
    }

}

