package io.digitalstate.taxii.camunda.client.variables.models.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = LongVariable.class) @JsonDeserialize(builder = LongVariable.Builder.class)
public interface LongVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    long getValue();

    @Value.Default
    @JsonProperty("type")
    @NotBlank
    default String getType(){
        return "Long";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
