package org.tensorflow.demo;

import org.tensorflow.demo.entities.ObjectWithScore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ankit Srivastava on 09/03/18.
 */

public final class DataLoader {
    private static final DataLoader instance = new DataLoader(populateObjectsWithScore());
    private final Set<ObjectWithScore> objectWithScore;

    private DataLoader(Set<ObjectWithScore> objectWithScore) {
        this.objectWithScore = objectWithScore;
    }

    private static Set<ObjectWithScore> populateObjectsWithScore() {
        Set<ObjectWithScore> o = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("assets://object.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                o.add(
                        new ObjectWithScore(
                                tokens[0],
                                Double.parseDouble(tokens[1]),
                                Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3]),
                                Double.parseDouble(tokens[4]),
                                Double.parseDouble(tokens[5])
                        )
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static DataLoader getInstance() {
        return instance;
    }

    public Set<ObjectWithScore> getObjectWithScore() {
        return objectWithScore;
    }
}
