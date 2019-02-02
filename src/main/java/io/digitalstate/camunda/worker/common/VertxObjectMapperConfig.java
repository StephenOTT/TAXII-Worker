package io.digitalstate.camunda.worker.common;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.vertx.core.json.Json;

public class VertxObjectMapperConfig {

    /**
     * Register Object Mapper modules against the global Vertx instance
     */
    public static void registerModules(){
        Json.mapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }
}
