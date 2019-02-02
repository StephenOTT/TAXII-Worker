package io.digitalstate.camunda.client.variables.models.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.camunda.client.variables.models.CamundaVariable;
import io.digitalstate.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = ShortVariable.class) @JsonDeserialize(builder = ShortVariable.Builder.class)
public interface ShortVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    short getValue();

    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "Short";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
