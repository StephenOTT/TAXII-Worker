package io.digitalstate.camunda.client.externaltask.models.complete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.camunda.client.common.EngineName;
import io.digitalstate.camunda.client.variables.models.CamundaVariable;
import org.immutables.value.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = Complete.class) @JsonDeserialize(builder = Complete.Builder.class)
public interface CompleteModel extends EngineName {

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


    @JsonProperty("variables")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Map<String, CamundaVariable> getVariables();

    @JsonProperty("localVariables")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    @NotNull
    Map<String, CamundaVariable> getLocalVariables();

}

