package com.example.cleanupourspace_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
        if (!tc.isInitialized()) {
            tc.loadModel(this, "model.tflite");
            tc.loadLabels(this, "labels.txt");
        }

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
                SharedPreferences accumPoints = getApplicationContext().getSharedPreferences("ACCUM_POINTS", 0);
                SharedPreferences.Editor edit = accumPoints.edit();
                int points = 0;
                int trashCount = accumPoints.getInt("totalCnt", 0);
                switch (maxEntry.getKey()) {
                    case "metal":
                        points = accumPoints.getInt("metal", 0) + 5;
                        edit.putInt("metal", points);
                        edit.putInt("totalCnt", trashCount += 1);
                        edit.apply();
                        break;
                    case "trash":
                        points = accumPoints.getInt("trash", 0) + 2;
                        edit = accumPoints.edit();
                        edit.putInt("trash", points);
                        edit.putInt("totalCnt", trashCount += 1);
                        edit.apply();
                        break;
                    case "plastic":
                        points = accumPoints.getInt("plastic", 0) + 5;
                        edit = accumPoints.edit();
                        edit.putInt("plastic", points);
                        edit.putInt("totalCnt", trashCount += 1);
                        edit.apply();
                        break;
                    case "cardboard":
                        points = accumPoints.getInt("cardboard", 0) + 3;
                        edit = accumPoints.edit();
                        edit.putInt("cardboard", points);
                        edit.putInt("totalCnt", trashCount += 1);
                        edit.apply();
                        break;
                    case "glass":
                        points = accumPoints.getInt("glass", 0) + 10;
                        edit = accumPoints.edit();
                        edit.putInt("glass", points);
                        edit.putInt("totalCnt", trashCount += 1);
                        edit.apply();
                        break;
                    case "paper":
                        points = accumPoints.getInt("paper", 0) + 1;
                        edit = accumPoints.edit();
                        edit.putInt("paper", points);
                        edit.putInt("totalCnt", trashCount += 1);
                        edit.apply();
                        break;
                    default:
                        break;
                }

                // Update UI components
                TextView litterType = (TextView) findViewById(R.id.type);
                litterType.setText("Type of Litter: " + maxEntry.getKey());
                TextView litterPoints = (TextView) findViewById(R.id.stats);
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