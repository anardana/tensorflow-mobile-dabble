package org.tensorflow.demo;

import android.content.Context;

import org.tensorflow.demo.entities.ClothCategory;
import org.tensorflow.demo.entities.ObjectWithScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ankit Srivastava on 09/03/18.
 */

public final class DataLoader {
    private final Set<ObjectWithScore> objectWithScore;
    private Context context;

    public DataLoader(Context context) throws IOException {
        this.context = context;
        this.objectWithScore = populateObjectsWithScore();
    }

    private String readFromFile(String myFile) {
        StringBuilder aBuffer = new StringBuilder();
        try {
            BufferedReader myReader = new BufferedReader(new InputStreamReader(context.getAssets().open(myFile)));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer.append(aDataRow);
            }
            myReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aBuffer.toString();
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
