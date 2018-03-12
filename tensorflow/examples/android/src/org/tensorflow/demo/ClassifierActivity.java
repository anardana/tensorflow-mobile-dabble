/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.demo;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.tensorflow.demo.OverlayView.DrawCallback;
import org.tensorflow.demo.entities.ObjectWithDetection;
import org.tensorflow.demo.entities.ObjectWithScore;
import org.tensorflow.demo.entities.RecognitionWithScore;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import java.util.Set;
import java.util.Vector;

public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
    protected static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final Logger LOGGER = new Logger();
    // These are the settings for the original v1 Inception model. If you want to
    // use a model that's been produced from the TensorFlow for Poets codelab,
    // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
    // INPUT_NAME = "Mul", and OUTPUT_NAME = "final_result".
    // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
    // the ones you produced.
    //
    // To use v3 Inception model, strip the DecodeJpeg Op from your retrained
    // model first:
    //
    // python strip_unused.py \
    // --input_graph=<retrained-pb-file> \
    // --output_graph=<your-stripped-pb-file> \
    // --input_node_names="Mul" \
    // --output_node_names="final_result" \
    // --input_binary=true
    private static final int INPUT_SIZE = 299;//224;
    private static final int IMAGE_MEAN = 128;//117;
    private static final float IMAGE_STD = 128.0f;//1;
    private static final String INPUT_NAME = "Mul:0";//"input";
    private static final String OUTPUT_NAME = "final_result";//"output";
    private static final String MODEL_FILE = "file:///android_asset/retrained_graph.pb";
    //tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/retrained_labels.txt";//imagenet_comp_graph_label_strings.txt";
    private static final boolean MAINTAIN_ASPECT = true;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(1920, 1080);
    private static final float TEXT_SIZE_DIP = 10;
    private static final float MODEL_EUCLIDIAN_DISTANCE_THRESHOLD = 20f;
    private static float MODEL_RECOGNITION_CONFIDENCE_THRESHOLD = .7f;
    private RecognitionScoreView recognitionScoreView;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;
    private long lastProcessingTimeMs;
    private Integer sensorOrientation;
    private TensorFlowImageClassifier classifier;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private BorderedText borderedText;

    private double[] toDoubleArray(float[] arr) {
        if (arr == null) return null;
        int n = arr.length;
        double[] ret = new double[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (double) arr[i];
        }
        return ret;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        classifier =
                TensorFlowImageClassifier.create(
                        getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME);

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888);

        frameToCropTransform = ImageUtils.getTransformationMatrix(
                previewWidth, previewHeight,
                INPUT_SIZE, INPUT_SIZE,
                sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        renderDebug(canvas);
                    }
                });
    }

    @Override
    protected void processImage() {
        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }
        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        final long startTime = SystemClock.uptimeMillis();

                        final RecognitionWithScore result = classifier.recognizeWithOp(croppedBitmap);
                        LOGGER.i("Detect: %s", result.getRecognitions());
                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        if (recognitionScoreView == null) {
                            recognitionScoreView = (RecognitionScoreView) findViewById(R.id.results);
                        }
                        if (result.getRecognitions().get(0).getConfidence() > MODEL_RECOGNITION_CONFIDENCE_THRESHOLD) {

                            ObjectWithDetection imageName = calculateClosestImage(result.getOp(), result.getRecognitions().get(0));
                            LOGGER.i("Image found: %s \t %s \t %s", imageName.getOws().getFileName(), imageName.getOws().getCategory(), imageName.getEuclidianDistance());
                            if (imageName.getEuclidianDistance() < MODEL_EUCLIDIAN_DISTANCE_THRESHOLD)
                                recognitionScoreView.setResults(result.getRecognitions(), imageName);
                            else
                                recognitionScoreView.setResults(result.getRecognitions(), null);

                        } else
                            recognitionScoreView.setResults(null, null);
                        recognitionScoreView.postInvalidate();
                        requestRender();
                        readyForNextImage();
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                    }
                });
    }

    private ObjectWithDetection calculateClosestImage(float[] op, Classifier.Recognition recognition) {
        long startTime = System.currentTimeMillis();
        ObjectWithDetection response = null;
//        Collections.reverse(Arrays.asList(op));
        double[] op1 = toDoubleArray(op);
        double distance = Double.MAX_VALUE;
        Set<ObjectWithScore> images = Constants.objectWithScores;
        EuclideanDistance ed = new EuclideanDistance();
        for (ObjectWithScore image : images) {
            if (image.getCategory().toString().toLowerCase().equals(recognition.getTitle().toLowerCase())) {
                double d = ed.compute(image.getAttributes(), op1);
                if (d < distance) {
                    response = new ObjectWithDetection(image, d);
                    distance = d;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        LOGGER.i("Ankit-Time for calculating closest image: %s", (endTime - startTime));
        return response;
    }

    @Override
    public void onSetDebug(boolean debug) {
        classifier.enableStatLogging(debug);
    }

    private void renderDebug(final Canvas canvas) {
        if (!isDebug()) {
            return;
        }
        final Bitmap copy = cropCopyBitmap;
        if (copy != null) {
            final Matrix matrix = new Matrix();
            final float scaleFactor = 2;
            matrix.postScale(scaleFactor, scaleFactor);
            matrix.postTranslate(
                    canvas.getWidth() - copy.getWidth() * scaleFactor,
                    canvas.getHeight() - copy.getHeight() * scaleFactor);
            canvas.drawBitmap(copy, matrix, new Paint());

            final Vector<String> lines = new Vector<String>();
            if (classifier != null) {
                String statString = classifier.getStatString();
                String[] statLines = statString.split("\n");
                for (String line : statLines) {
                    lines.add(line);
                }
            }

            lines.add("Frame: " + previewWidth + "x" + previewHeight);
            lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
            lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
            lines.add("Rotation: " + sensorOrientation);
            lines.add("Inference time: " + lastProcessingTimeMs + "ms");

            borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
        }
    }
}
