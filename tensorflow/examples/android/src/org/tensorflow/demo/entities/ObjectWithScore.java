package org.tensorflow.demo.entities;

/**
 * Created by Ankit Srivastava on 09/03/18.
 */

public final class ObjectWithScore {
    private final String name;
    private final double checks;
    private final double fullSleeves;
    private final double halfSleeves;
    private final double plain;
    private final double mandarin;

    public ObjectWithScore(String name, double checks, double fullSleeves, double halfSleeves, double plain, double mandarin) {
        this.name = name;
        this.checks = checks;
        this.fullSleeves = fullSleeves;
        this.halfSleeves = halfSleeves;
        this.plain = plain;
        this.mandarin = mandarin;
    }

    public String getName() {
        return name;
    }

    public double getChecks() {
        return checks;
    }

    public double getFullSleeves() {
        return fullSleeves;
    }

    public double getHalfSleeves() {
        return halfSleeves;
    }

    public double getPlain() {
        return plain;
    }

    public double getMaindarin() {
        return mandarin;
    }
}
