package org.tensorflow.demo.entities;

/**
 * Created by Ankit Srivastava on 09/03/18.
 */

public enum AttributeMapping {
    CHECKS("Checks"),
    FULL_SLEEVES("Full Sleeve"),
    HALF_SLEEVES("Half Sleeve"),
    MANDARIN("Mandarin"),
    PLAIN("Plain");

    private final String value;

    AttributeMapping(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
