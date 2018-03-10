package org.tensorflow.demo.entities;

import org.tensorflow.demo.Classifier;

import java.util.List;

/**
 * Created by Ankit Srivastava on 10/03/18.
 */

public class RecognitionWithScore {
    private final List<Classifier.Recognition> recognitions;
    private final float[] op;

    public RecognitionWithScore(List<Classifier.Recognition> recognitions, float[] op) {
        this.recognitions = recognitions;
        this.op = op;
    }

    public List<Classifier.Recognition> getRecognitions() {
        return recognitions;
    }

    public float[] getOp() {
        return op;
    }
}
