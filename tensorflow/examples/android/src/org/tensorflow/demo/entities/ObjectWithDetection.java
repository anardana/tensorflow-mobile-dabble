package org.tensorflow.demo.entities;

/**
 * Created by Ankit Srivastava on 10/03/18.
 */

public final class ObjectWithDetection {
    private final ObjectWithScore ows;
    private final double euclidianDistance;

    public ObjectWithDetection(ObjectWithScore ows, double euclidianDistance) {
        this.ows = ows;
        this.euclidianDistance = euclidianDistance;
    }

    public ObjectWithScore getOws() {
        return ows;
    }

    public double getEuclidianDistance() {
        return euclidianDistance;
    }
}
