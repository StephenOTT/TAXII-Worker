package io.digitalstate.taxii.camunda.client.externaltask.models.fetchandlock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.digitalstate.taxii.camunda.client.common.EngineName;
import io.digitalstate.taxii.camunda.client.common.HttpResponseDetails;
import org.immutables.value.Value;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract="*Model", typeImmutable="*", validationMethod = Value.Style.ValidationMethod.NONE, depluralize = true)
@JsonSerialize(as = FetchAndLockResponseList.class) @JsonDeserialize(builder = FetchAndLockResponseList.Builder.class)
public interface FetchAndLockResponseListModel extends HttpResponseDetails, EngineName {

    @JsonProperty("fetchedTasks")
    @JsonInclude(value = NON_EMPTY, content= NON_EMPTY)
    List<FetchAndLockResponseModel> getFetchedTasks();


}

