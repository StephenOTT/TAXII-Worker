package io.digitalstate.taxii.camunda.client.variables;

import java.io.Serializable;

/**
 * Testing class for serialization
 */
class SimpleClass1 implements Serializable {

    private static final long serialVersionUID = 111L;

    String value1;
    String value2;

    SimpleClass1(String value1, String value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    String getValue1() {
        return value1;
    }

    void setValue1(String value1) {
        this.value1 = value1;
    }

    String getValue2() {
        return value2;
    }

    void setValue2(String value2) {
        this.value2 = value2;
    }
}