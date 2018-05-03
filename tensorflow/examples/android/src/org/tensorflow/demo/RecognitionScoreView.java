/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tensorflow.demo.Classifier.Recognition;
import org.tensorflow.demo.entities.ObjectWithDetection;

import java.util.List;
import java.util.Map;

public class RecognitionScoreView extends RelativeLayout {

    private View recognitionScoreView, scoreView;
    private TextView description;
    private TextView sellThroughtValue;
    private TextView imuValue;
    private TextView marginValue;
    private TextView buyQuantityValue;
    private TextView titleConfidence;
    private TextView distance;

    public RecognitionScoreView(Context context) {
        super(context);
        init();
    }

    public RecognitionScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecognitionScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RecognitionScoreView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        recognitionScoreView = inflate(getContext(), R.layout.recognition_score_view, this);
        scoreView = findViewById(R.id.scoreView);
        this.description = (TextView) findViewById(R.id.description);
        this.sellThroughtValue = (TextView) findViewById(R.id.sell_through_value);
        this.imuValue = (TextView) findViewById(R.id.imu_value);
        this.marginValue = (TextView) findViewById(R.id.margin_value);
        this.buyQuantityValue = (TextView) findViewById(R.id.buy_quantity_value);
        this.distance = (TextView) findViewById(R.id.distance);
        this.titleConfidence = (TextView) findViewById(R.id.titleConfidence);
    }

    public void setResults(List<Recognition> results, ObjectWithDetection image) {

        if (image != null) {
            String fileName = image.getOws().getFileName();
            String key = fileName.substring(0, fileName.indexOf(".jpg"));
            Map<String, String> data = Constants.objectDataFromRetailMe.get(key);

            description.setText(data.get("description"));
            sellThroughtValue.setText(data.get("Sell Through %"));
            marginValue.setText(data.get("Margin %"));
            buyQuantityValue.setText(data.get("Buy Qty"));
            imuValue.setText(data.get("IMU%"));
            scoreView.setVisibility(VISIBLE);
        }else{
            scoreView.setVisibility(GONE);
        }

        if (results != null) {
            for (final Recognition recog : results) {
                titleConfidence.setText(recog.getTitle().toUpperCase() + ": " + (int) (recog.getConfidence() * 100));
            }
            if (image != null)
                distance.setText("Distance: " + image.getEuclidianDistance());
        }
    }


}
