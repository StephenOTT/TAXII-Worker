package io.digitalstate.taxii.camunda.client.variables.models.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.variables.models.ValueInfo;
import io.digitalstate.taxii.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = ObjectVariable.class) @JsonDeserialize(builder = ObjectVariable.Builder.class)
public interface ObjectVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    Object getValue();

    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "Object";
    }

    @JsonProperty("valueInfo")
    default ValueInfoProperty getValueInfo(){
        return ValueInfo.builder()
                .objectTypeName(getValue().getClass().getCanonicalName())
                .build();
    }

}
