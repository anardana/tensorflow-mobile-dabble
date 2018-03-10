package org.tensorflow.demo.entities;

/**
 * Created by Ankit Srivastava on 09/03/18.
 */

public final class ObjectWithScore {
    private final String fileName;
    private final double[] attributes;
    private final ClothCategory category;

    public ObjectWithScore(String fileName, double[] attributes, ClothCategory category) {
        this.fileName = fileName;
        this.attributes = attributes;
        this.category = category;
    }

    public String getFileName() {
        return fileName;
    }

    public double[] getAttributes() {
        return attributes;
    }

    public ClothCategory getCategory() {
        return category;
    }
}
