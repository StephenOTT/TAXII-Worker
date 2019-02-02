package io.digitalstate.camunda.client.variables.models.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.camunda.client.variables.models.CamundaVariable;
import io.digitalstate.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = BytesVariable.class) @JsonDeserialize(builder = BytesVariable.Builder.class)
public interface BytesVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    byte[] getValue();

    @Value.Default
    @JsonProperty("type")
    @NotBlank
    default String getType(){
        return "Bytes";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
