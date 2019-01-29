package io.digitalstate.taxii.camunda.client.variables.models.serialization;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.digitalstate.taxii.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = DateVariable.class) @JsonDeserialize(builder = DateVariable.Builder.class)
public interface DateVariableModel extends CamundaVariable {

    @Value.Parameter
    @JsonProperty("value")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull
    LocalDate getValue();

    @Value.Default
    @JsonProperty("type")
    default String getType(){
        return "Date";
    }

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

}
