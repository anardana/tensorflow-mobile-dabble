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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

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
    private final Paint headerPaint;
    private final Paint bgPaint;
    private List<Recognition> results;
    private ObjectWithDetection image;


    public RecognitionScoreView(final Context context, final AttributeSet set) {
        super(context, set);

        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        fgPaint = new Paint();
        fgPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        fgPaint.setTextSize(textSizePx);

        headerPaint = new Paint();
        headerPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        headerPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

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

        canvas.drawPaint(bgPaint);

        Set customPositionedAttributes = new HashSet();
        customPositionedAttributes.add("description");
        customPositionedAttributes.add("id");
        if (image != null) {
            String fileName = image.getOws().getFileName();
            String key = fileName.substring(0, fileName.indexOf(".jpg"));
            Map<String, String> data = Constants.objectDataFromRetailMe.get(key);

            canvas.drawText(data.get("description") + " (" + data.get("id") + ")", 10, 70, headerPaint);

            for (String left : data.keySet()) {
                if (!customPositionedAttributes.contains(left)) {
                    canvas.drawText(left.toUpperCase() + ": " + data.get(left), x, y + 100, fgPaint);
                    y += fgPaint.getTextSize() * 1.5f;
                }
            }
        }

        y = 900;

        if (results != null) {
            for (final Recognition recog : results) {
                canvas.drawText(recog.getTitle().toUpperCase() + ": " + (int) (recog.getConfidence() * 100) + "%", x, y + 100, fgPaint);
                y += fgPaint.getTextSize() * 1.5f;
            }
        }
    }
}
