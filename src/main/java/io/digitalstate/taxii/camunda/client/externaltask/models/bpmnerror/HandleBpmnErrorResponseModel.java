package io.digitalstate.taxii.camunda.client.externaltask.models.bpmnerror;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.common.EngineName;
import io.digitalstate.taxii.camunda.client.common.HttpResponseDetails;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = HandleBpmnErrorResponse.class) @JsonDeserialize(builder = HandleBpmnErrorResponse.Builder.class)
public interface HandleBpmnErrorResponseModel extends HttpResponseDetails, EngineName {

}

