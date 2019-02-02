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
@JsonSerialize(as = StringVariable.class) @JsonDeserialize(builder = StringVariable.Builder.class)
public interface StringVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    String getValue();

    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "String";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
