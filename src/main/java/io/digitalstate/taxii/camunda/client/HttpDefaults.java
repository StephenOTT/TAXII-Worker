package io.digitalstate.taxii.camunda.client;

import io.digitalstate.taxii.camunda.client.common.HttpConfigModel;
import io.digitalstate.taxii.camunda.client.common.HttpConfig;

/**
 * Http Client Default Configurations used by Camunda Client.
 * Set attributes with your own configurations at application startup.
 */
public class HttpDefaults {

    /**
     * HttpConfig Immutable storing the default configurations for HTTP Client used for commuting with Camunda.
     */
    public static HttpConfigModel httpConfigDefaults = HttpConfig.builder().build();

}
