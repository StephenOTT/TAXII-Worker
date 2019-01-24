package io.digitalstate.taxii.camunda.client.externaltask.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.vertx.core.MultiMap;

import java.io.IOException;


public class CaseInsensitiveHeadersSerializer extends StdSerializer<MultiMap> {

    protected CaseInsensitiveHeadersSerializer() {
        super(MultiMap.class);
    }

    @Override
    public void serialize(MultiMap value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        value.forEach(v -> {
            try {
                gen.writeObject(v);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        gen.writeEndArray();
    }
}

