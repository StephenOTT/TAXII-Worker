package io.digitalstate.taxii.camunda.client.variables;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import org.immutables.value.Value;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Property", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = Variable.class) @JsonDeserialize(builder = Variable.Builder.class)
public interface VariableProperty {

    @JsonProperty("type")
    String getType();

    @JsonProperty("value")
    Object getValue();

    @JsonProperty("valueInfo")
    Optional<ValueInfoProperty> getValueInfo();

    /**
     * Lazy is used incase conversion is not possible and therefore the original value is still available allowing custom processing fall-backs.
     * @return
     */
    @Value.Lazy
    default Object getValueTyped() {
        switch (getType()){
            case "Boolean":
                return Boolean.valueOf(getValue().toString());

            case "Bytes":
                return Byte.valueOf(getValue().toString());

            case "Short":
                return Short.valueOf(getValue().toString());

            case "Integer":
                return Integer.valueOf(getValue().toString());

            case "Long":
                return Long.valueOf(getValue().toString());

            case "Double":
                return Double.valueOf(getValue().toString());

            case "Date":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss.SSSX");
                return LocalDate.parse(getValue().toString(), formatter);

            case "String":
                return String.valueOf(getValue().toString());

            case "Json":
                return new JsonObject(getValue().toString());

            case "Xml":
                return String.valueOf(getValue().toString());

            case "Object":
                String objectTypeName = getValueInfo()
                        .orElseThrow(()-> new IllegalStateException("Response is missing Value Info"))
                        .getObjectTypeName()
                        .orElseThrow(()->new IllegalStateException("Missing Object Type Name Value"));
                try {
                    // A basic sanity check that the client can read the java serialized pojo.
                    // Does not mean the class is a different version.  Its just a quick check.
                    Class.forName(objectTypeName);

                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Object Type does not exist on this client: " + objectTypeName, e);
                }

                byte[] decodedValue = Base64.getDecoder().decode(getValue().toString());
                InputStream targetStream = new ByteArrayInputStream(decodedValue);

                ObjectInputStream objectInputStream = null;
                try {
                    objectInputStream = new ObjectInputStream(targetStream);
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to read serialized pojo: " + e.getMessage(), e);
                }

                try {
                   Object deserializedObject = objectInputStream.readObject();
                   objectInputStream.close();

                   return deserializedObject;

                } catch (IOException e) {
                    throw new IllegalStateException("IO Exception when reading object stream: " + e.getMessage(), e);

                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Cannot find class for serialized object: " + e.getMessage(), e);
                }

            case "File":
                return String.valueOf(getValue().toString());

            default:
                throw new IllegalStateException("Unable to Type the value.  Unknown Type: " + getType());

        }
    }

}
