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

    /**
     * Must be a object that extends Serializaable
     * @return
     */
    @Value.Parameter
    @JsonProperty("value")
    Object getValue();

    /**
     * Defaults to "Object"
     * @return
     */
    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "Object";
    }

    /**
     * Defaults to adding the objectTypeName value based on the Canonical Name of the object's class.
     * @return
     */
    @JsonProperty("valueInfo")
    default ValueInfoProperty getValueInfo(){
        return ValueInfo.builder()
                .objectTypeName(getValue().getClass().getCanonicalName())
                .serializationDataFormat("application/x-java-serialized-object")
                .build();
    }

}
