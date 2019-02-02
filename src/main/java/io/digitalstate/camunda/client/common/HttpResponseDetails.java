package io.digitalstate.camunda.client.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;

/**
 * Generic interface used by Immutables for adding the general HTTP Response Details attribute
 */
public interface HttpResponseDetails {

    /**
     * Response object used by HTTP client implementation.
     * Use this method to store your HTTP response object so you can pass around things like status code, response headers, etc.
     * @return
     */
    @JsonIgnore
    Optional<Object> getResponseDetails();

}
