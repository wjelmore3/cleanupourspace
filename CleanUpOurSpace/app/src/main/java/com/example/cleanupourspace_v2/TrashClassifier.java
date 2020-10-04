package com.example.cleanupourspace_v2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Map;

public class TrashClassifier {
    private ImageProcessor imageProcessor;
    private TensorImage tImage;
    private TensorBuffer probabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private MappedByteBuffer tfliteModel;
    private Interpreter tflite;
    private List<String> associatedAxisLabels;
    private boolean isInitialized;

    private static TrashClassifier instance = null;

    private TrashClassifier() {
        // Initialization code
        // Create an ImageProcessor with all ops required. For more ops, please
        // refer to the ImageProcessor Architecture section in this README.
        imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build();
        // Create a TensorImage object. This creates the tensor of the corresponding
        // tensor type (uint8 in this case) that the TensorFlow Lite interpreter needs.
        tImage = new TensorImage(DataType.UINT8);

        // Create a container for the result and specify that this is a quantized model.
        // Hence, the 'DataType' is defined as UINT8 (8-bit unsigned integer)
        probabilityBuffer = TensorBuffer.createFixedSize(new int[]{1, 6}, DataType.UINT8);

        probabilityProcessor = new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();

        isInitialized = false;
    }

    public static TrashClassifier getInstance() {
        if (instance == null) {
            instance = new TrashClassifier();
        }
        return instance;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void loadModel(Activity activity, String modelPath) {
        // Initialise the model
        try {
            tfliteModel = FileUtil.loadMappedFile(activity, modelPath);
            tflite = new Interpreter(tfliteModel);

            isInitialized = true;
        } catch (IOException e){
            Log.e("tfliteSupport", "Error reading model", e);

            isInitialized = false;
        }
    }

    public void loadLabels(Activity activity, String labelPath) {
        try {
            associatedAxisLabels = FileUtil.loadLabels(activity, labelPath);
        } catch (IOException e) {
            Log.e("tfliteSupport", "Error reading label file", e);
        }
    }

    public void preProcessImage(Bitmap bitmap) {
        // Analysis code for every frame
        // Preprocess the image
        tImage.load(bitmap);
        tImage = imageProcessor.process(tImage);
    }

    public Map<String, Float> analyzeImage() {
        // Running inference
        if (null != tflite) {
            tflite.run(tImage.getBuffer(), probabilityBuffer.getBuffer());
            // Post-processor which dequantize the result
            if (null != associatedAxisLabels) {
                // Map of labels and their corresponding probability
                TensorLabel labels = new TensorLabel(associatedAxisLabels,
                        probabilityProcessor.process(probabilityBuffer));

                // Create a map to access the result based on label
                Map<String, Float> floatMap = labels.getMapWithFloatValue();

                return floatMap;
            }
        } else {
            Log.e("tfliteSupport", "Error running inference");
        }
        return  null;
    }
}
