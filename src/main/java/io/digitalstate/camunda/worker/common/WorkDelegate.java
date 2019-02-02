package io.digitalstate.camunda.worker.common;

import io.vertx.core.Future;

public interface WorkDelegate {

    Future<Object> execute();

}
