package io.digitalstate.camunda.client.externaltask.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.vertx.core.MultiMap;
import io.vertx.core.http.CaseInsensitiveHeaders;

import java.io.IOException;


public class CaseInsensitiveHeadersDeserializer extends StdDeserializer<MultiMap> {

    protected CaseInsensitiveHeadersDeserializer() {
        super(MultiMap.class);
    }

    @Override
    public MultiMap deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        CaseInsensitiveHeaders headers = new CaseInsensitiveHeaders();
        TreeNode tree = p.readValueAsTree();
        if (tree.isArray()){
            ArrayNode array = (ArrayNode)tree;
            array.forEach(item->{
                item.fields().forEachRemaining(f->{
                    headers.add(f.getKey(), f.getValue().asText());
                });
            });
        }
        return headers;
    }

}

