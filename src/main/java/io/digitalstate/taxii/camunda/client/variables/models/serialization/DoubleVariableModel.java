package io.digitalstate.taxii.camunda.client.variables.models.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = DoubleVariable.class) @JsonDeserialize(builder = DoubleVariable.Builder.class)
public interface DoubleVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    double getValue();

    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "Double";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
