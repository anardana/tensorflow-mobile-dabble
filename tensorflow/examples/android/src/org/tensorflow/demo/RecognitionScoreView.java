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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import org.tensorflow.demo.Classifier.Recognition;
import org.tensorflow.demo.entities.ObjectWithDetection;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecognitionScoreView extends View implements ResultsView {
    private static final float TEXT_SIZE_DIP = 12;
    private final float textSizePx;
    private final Paint fgPaint;
    private final Paint fgPaintHeader;
    private final Paint headerPaint;
    private final Paint bgPaint;
    private List<Recognition> results;
    private ObjectWithDetection image;
    private Bitmap bitmap;


    public RecognitionScoreView(final Context context, final AttributeSet set) {
        super(context, set);

        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        fgPaint = new Paint();
        fgPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        fgPaint.setTextSize(textSizePx);
        fgPaint.setColor(Color.WHITE);

        fgPaintHeader = new Paint();
        fgPaintHeader.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        fgPaintHeader.setTextSize(textSizePx);
        fgPaintHeader.setColor(Color.WHITE);

        headerPaint = new Paint();
        headerPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        headerPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        headerPaint.setColor(Color.WHITE);

        bgPaint = new Paint();
        bgPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    public void setResults(List<Recognition> results, ObjectWithDetection image) {
        this.results = results;
        this.image = image;
    }

    @Override
    public void onDraw(final Canvas canvas) {
        final int x = 10;
        int y = (int) (fgPaint.getTextSize() * 1.5f);


        Set customPositionedAttributes = new HashSet();
        customPositionedAttributes.add("description");
        customPositionedAttributes.add("id");
        if (image != null) {
            ImageView view = (ImageView) getRootView().findViewById(R.id.imageView);
            view.setVisibility(VISIBLE);

            String fileName = image.getOws().getFileName();
            String key = fileName.substring(0, fileName.indexOf(".jpg"));
            Map<String, String> data = Constants.objectDataFromRetailMe.get(key);

            canvas.drawText(data.get("description"), 80, 1440, headerPaint);

            canvas.drawText("Sell Through", 80, 1540, fgPaintHeader);
            canvas.drawText(data.get("Sell Through %"), 80, 1600, fgPaint);


            canvas.drawText("Margin", 80, 1670, fgPaintHeader);
            canvas.drawText(data.get("Margin %"), 80, 1730, fgPaint);


            canvas.drawText("Buy Quantity", 580, 1540, fgPaintHeader);
            canvas.drawText(data.get("Buy Qty"), 580, 1600, fgPaint);


            canvas.drawText("IMU", 580, 1670, fgPaintHeader);
            canvas.drawText(data.get("IMU%"), 580, 1730, fgPaint);
        } else {
            ImageView view = (ImageView) getRootView().findViewById(R.id.imageView);
            view.setVisibility(INVISIBLE);
        }

        y = 900;

        if (results != null) {
            for (final Recognition recog : results) {
                canvas.drawText(recog.getTitle().toUpperCase() + ": " + (int) (recog.getConfidence() * 100) + "%", x, y + 100, fgPaint);
                y += fgPaint.getTextSize() * 1.5f;
            }
            if (image != null)
                canvas.drawText("Distance: " + image.getEuclidianDistance(), x, y + 100, fgPaint);
        }
    }
}
