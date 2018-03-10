package org.tensorflow.demo.entities;

/**
 * Created by Ankit Srivastava on 09/03/18.
 */

public enum ClothCategory {
    LongGown("Long Gown"),
    menShirt("Men's Shirt"),
    polotshirt("Polo T-Shirt"),
    vestWaistCoat("Vest Waist Coat");

    private final String value;

    ClothCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
