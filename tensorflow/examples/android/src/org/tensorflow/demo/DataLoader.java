package org.tensorflow.demo;

import android.content.Context;

import org.tensorflow.demo.entities.ClothCategory;
import org.tensorflow.demo.entities.ObjectWithScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ankit Srivastava on 09/03/18.
 */

public final class DataLoader {
    private final Set<ObjectWithScore> objectWithScore;
    private final Map<String, Map<String, String>> objectDataFromRetailMe;
    private Context context;
    public DataLoader(Context context) throws IOException {
        this.context = context;
        this.objectWithScore = populateObjectsWithScore();
        this.objectDataFromRetailMe = populateObjectDataFromCsv();
    }

    public Map<String, Map<String, String>> getObjectDataFromRetailMe() {
        return objectDataFromRetailMe;
    }

    private Map<String, Map<String, String>> populateObjectDataFromCsv() {
        Map<String, Map<String, String>> o = new HashMap<>();

        String[] fileLines = readFromFile("object.csv").split("[\\r\\n]+");
        String[] headers = fileLines[0].split(",");

        for (int i = 1; i < fileLines.length; i++) {
            String[] tokens = fileLines[i].split(",");
            Map<String, String> tuples = new LinkedHashMap<>();
            for (int j = 0; j < tokens.length; j++) {
                tuples.put(headers[j], tokens[j]);
            }
            o.put(tokens[0], tuples);
        }
        return o;
    }

    private String readFromFile(String myFile) {
        StringBuilder aBuffer = new StringBuilder();
        try {
            BufferedReader myReader = new BufferedReader(new InputStreamReader(context.getAssets().open(myFile)));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer.append(aDataRow).append("\n");
            }
            myReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = aBuffer.toString();
        return s.substring(0, s.length() - 1);
    }

    private Set<ObjectWithScore> populateObjectsWithScore() throws IOException {
        Set<ObjectWithScore> o = new HashSet<>();

        String[] bottleNeckDirs = context.getAssets().list("bottlenecks");
        for (String bottleNeckDir : bottleNeckDirs) {
            ClothCategory category = ClothCategory.valueOf(bottleNeckDir);
            String[] clothesBottleneck = context.getAssets().list("bottlenecks/" + category);
            for (String cloth : clothesBottleneck) {
                String fileName = cloth.substring(0, cloth.indexOf(".jpg") + 4);
                String[] scores = readFromFile("bottlenecks/" + category + "/" + cloth).split(",");
                double[] scoresD = new double[2048];
                int i = 0;
                for (String score : scores) {
                    scoresD[i] = Double.parseDouble(score);
                    i++;
                }
                ObjectWithScore ows = new ObjectWithScore(fileName, scoresD, category);
                o.add(ows);
            }
        }

        return o;
    }

    Set<ObjectWithScore> getObjectWithScore() {
        return objectWithScore;
    }
}
