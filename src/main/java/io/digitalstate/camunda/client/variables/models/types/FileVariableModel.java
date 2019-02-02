package io.digitalstate.camunda.client.variables.models.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.camunda.client.variables.json.FileBase64Serializer;
import io.digitalstate.camunda.client.variables.models.CamundaVariable;
import io.digitalstate.camunda.client.variables.models.ValueInfo;
import io.digitalstate.camunda.client.variables.models.ValueInfoProperty;
import org.immutables.value.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URLConnection;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = FileVariable.class) @JsonDeserialize(builder = FileVariable.Builder.class)
public interface FileVariableModel extends CamundaVariable {

    /**
     * Bytes of file.  Will automatically be base64 encoded.
     * Required.
     *
     * @return
     */
    @Value.Parameter
    @JsonProperty("value")
    @JsonSerialize(using = FileBase64Serializer.class)
    byte[] getValue();

    /**
     * Defaults to "File"
     * @return
     */
    @Value.Default
    @JsonProperty("type")
    @NotBlank
    default String getType() {
        return "File";
    }

    /**
     * File name.  Required.
     *
     * @return
     */
    @NotBlank
    @JsonIgnore
    @Value.Parameter
    String getFilename();

    /**
     * Mime Type of File.  If this value is not provided, the Mime type will be "guessed" based on the file extension using {@link URLConnection#guessContentTypeFromName(String)}
     *
     * @return
     */
    @NotBlank
    @JsonIgnore
    @Value.Default
    default String getMimetype(){
        return URLConnection.guessContentTypeFromName(this.getFilename());
    }

    /**
     * Encoding of File. Defaults to UTF-8.
     *
     * @return
     */
    @NotBlank
    @JsonIgnore
    @Value.Default
    default String getEncoding(){
        return "UTF-8";
    }

    /**
     * Defaults to false.
     * @return
     */
    @Value.Default
    @JsonIgnore
    default boolean isTransient(){
        return false;
    }

    /**
     * Defaults to the values set in filename, mimetype, encoding, and Transient.
     * If you override this value you must reset each value manually.
     * @return
     */
    @JsonProperty("valueInfo")
    @NotNull
    default ValueInfoProperty getValueInfo() {
        return ValueInfo.builder()
                .filename(this.getFilename())
                .mimetype(this.getMimetype())
                .encoding(this.getEncoding())
                .isTransient(this.isTransient())
                .build();
    }

}
