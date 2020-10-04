package com.example.cleanupourspace_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class InferenceActivity extends AppCompatActivity {
    private TrashClassifier tc;
    private Button button;
    private ImageView mImageView;
    private int points = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inference);

        mImageView = findViewById(R.id.image_view2);
        Uri uri = getIntent().getData();
        try {
            InputStream stream = getContentResolver().openInputStream(uri);
            if (stream == null) return;
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            mImageView.setImageBitmap(bitmap);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize classifier
        tc = TrashClassifier.getInstance();
        tc.loadModel(this, "model.tflite");
        tc.loadLabels(this, "labels.txt");

        // Classify image
        if (tc.isInitialized()) {
            tc.preProcessImage(((BitmapDrawable)mImageView.getDrawable()).getBitmap());
            Map<String, Float> results = tc.analyzeImage();
            if (!results.isEmpty() && results != null)
            {
                // Get the classification/score
                Map.Entry<String, Float> maxEntry = null;

                for (Map.Entry<String, Float> entry : results.entrySet())
                {
                    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                    {
                        maxEntry = entry;
                    }
                }

                // Assign points based on classification
                switch (maxEntry.getKey()) {
                    case "metal":
                        points = 5;
                        break;
                    case "trash":
                        points = 2;
                        break;
                    case "plastic":
                        points = 5;
                        break;
                    case "cardboard":
                        points = 3;
                        break;
                    case "glass":
                        points = 10;
                        break;
                    case "paper":
                        points = 1;
                        break;
                    default:
                        break;
                }

                // Update UI components
                TextView litterType = (TextView) findViewById(R.id.type);
                litterType.setText("Type of Litter: " + maxEntry.getKey());
                TextView litterPoints = (TextView) findViewById(R.id.points);
                litterPoints.setText("Awarded Points: " + String.valueOf(points));
            }
        }

        //Initialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener((new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.camera:
                        startActivity(new Intent(getApplicationContext(),CameraActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        }));
    }
}