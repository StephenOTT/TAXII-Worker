package io.digitalstate.taxii.camunda.client.variables.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.vertx.core.MultiMap;

import java.io.IOException;
import java.util.Base64;


public class FileBase64Serializer extends StdSerializer<byte[]> {

    protected FileBase64Serializer() {
        super(byte[].class);
    }

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(Base64.getEncoder().encodeToString(value));
    }
}

