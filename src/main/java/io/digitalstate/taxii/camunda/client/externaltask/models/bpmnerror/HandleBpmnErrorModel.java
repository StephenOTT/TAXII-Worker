package io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.common.HttpConfig;
import io.digitalstate.taxii.camunda.client.common.HttpConfigModel;
import org.immutables.value.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = HandleBpmnError.class) @JsonDeserialize(builder = HandleBpmnError.Builder.class)
public interface HandleBpmnErrorModel {

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

    @JsonProperty("errorCode")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    String getErrorCode();

    @JsonProperty("errorMessage")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getErrorMessage();

    //@TODO pull in variable support from Camunda lib
    @JsonProperty("variables")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Map<String, Object> getProcessVariables();

    @JsonIgnore
    @Value.Default
    default HttpConfigModel getHttpConfig() {
        HttpConfigModel config = HttpConfig.builder()
                .method("POST")
                .uri("/external-task/" + getId() + "/bpmnError")
                .build();

        return config;
    }


}
