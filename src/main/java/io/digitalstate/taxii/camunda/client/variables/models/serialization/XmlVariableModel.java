package io.digitalstate.taxii.camunda.client.variables.models.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = XmlVariable.class) @JsonDeserialize(builder = XmlVariable.Builder.class)
public interface XmlVariableModel extends CamundaVariable {

    /**
     * Escaped Xml String
     * @return
     */
    @Value.Parameter
    @JsonProperty("value")
    String getValue();

    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "Xml";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
