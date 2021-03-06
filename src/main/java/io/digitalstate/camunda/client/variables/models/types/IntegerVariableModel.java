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
@JsonSerialize(as = IntegerVariable.class) @JsonDeserialize(builder = IntegerVariable.Builder.class)
public interface IntegerVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    int getValue();

    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "Integer";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
