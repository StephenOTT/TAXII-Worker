package io.digitalstate.taxii.camunda.client.variables.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Property", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = ValueInfo.class) @JsonDeserialize(builder = ValueInfo.Builder.class)
public interface ValueInfoProperty {

    /**
     * Full Canonical Object (typically Class) Name.
     * @return
     */
    @JsonProperty("objectTypeName")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getObjectTypeName();

    /**
     * ??
     * @return
     */
    @JsonProperty("serializationDataFormat")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getSerializationDataFormat();

    /**
     * Used for File
     * @return
     */
    @JsonProperty("filename")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getFilename();

    /**
     * Used for File
     * @return
     */
    @JsonProperty("mimetype")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getMimetype();

    /**
     * Used for File
     * @return
     */
    @JsonProperty("encoding")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<String> getEncoding();

    /**
     * Indicates whether the variable should be transient or not.
     * Only used for when creating a JSON body to send to Camunda
     * @return
     */
    @JsonProperty("transient")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    Optional<Boolean> isTransient();

}
