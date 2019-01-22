package io.digitalstate.taxii.camunda.client.common;

import org.immutables.value.Value;

import javax.validation.constraints.NotBlank;

/**
 * Immutable for setup of HTTP Configs used by Camunda Client.
 * You can inject your instance of this Immutable into the {@link io.digitalstate.taxii.camunda.client.HttpDefaults} class.
 * Sensible Defaults are used for common usage, but any connections that are not localhost:8080
 * will require you to inject a new instance of this Immutable at application startup.
 */
@Value.Immutable
@Value.Style(jdkOnly = true, typeAbstract="*Model", typeImmutable="*", validationMethod = Value.Style.ValidationMethod.NONE)
public interface HttpConfigModel {

    @Value.Default
    @NotBlank
    default String getProtocol(){
        return "http";
    }

    @NotBlank
    String getMethod();

    @Value.Default
    @NotBlank
    default String getBaseUrl(){
        return "localhost";
    }

    @Value.Default
    @NotBlank
    default int getPort(){
        return 8080;
    }

    @Value.Default
    @NotBlank
    default String getBaseRestUri(){
        return "/engine-rest";
    }

    @NotBlank
    String getUri();

    @Value.Lazy
    @NotBlank
    default String getAbsoluteUri(){
        return getProtocol() + "://" + getBaseUrl() + ":" + getPort() + getBaseRestUri() + getUri();
    }

}

