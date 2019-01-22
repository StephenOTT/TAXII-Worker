package io.digitalstate.taxii.camunda.client.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;

public interface EngineName {

    /**
     * Allows the storage of a engine name for use with factory and generic web clients that may change their destination based on the engine that the request is for.
     * @return
     */
    @JsonIgnore
    Optional<String> getEngineName();

}
