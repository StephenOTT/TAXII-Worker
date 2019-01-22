package io.digitalstate.taxii.camunda.client.common;

import javax.management.RuntimeErrorException;

public class CamundaErrorResponse extends RuntimeErrorException {

    public CamundaErrorResponse(int responseCode, String responseBody, String errorExplanation) {
        super(new Error(errorExplanation), String.format("Camunda Returned non-success response (%s): Code: %d  Response Body: %s",
                errorExplanation, responseCode, responseBody));
    }

}
