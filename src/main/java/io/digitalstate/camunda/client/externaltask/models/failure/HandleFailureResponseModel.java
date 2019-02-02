package io.digitalstate.camunda.client.externaltask.models.failure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.camunda.client.common.EngineName;
import io.digitalstate.camunda.client.common.HttpResponseDetails;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract = "*Model", typeImmutable = "*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = HandleFailureResponse.class) @JsonDeserialize(builder = HandleFailureResponse.Builder.class)
public interface HandleFailureResponseModel extends HttpResponseDetails, EngineName {

}

