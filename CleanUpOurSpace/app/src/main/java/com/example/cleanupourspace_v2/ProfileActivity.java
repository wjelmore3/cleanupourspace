package com.example.cleanupourspace_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Camera Selected
        bottomNavigationView.setSelectedItemId(R.id.Profile);

        // Get stat text display items
        TextView pointCntT = (TextView) findViewById(R.id.points);
        TextView trashCntT = (TextView) findViewById(R.id.totalCount);
        TextView trashT = (TextView) findViewById(R.id.trash);
        TextView paperT = (TextView) findViewById(R.id.paper);
        TextView plasticT = (TextView) findViewById(R.id.plastic);
        TextView cardboardT = (TextView) findViewById(R.id.cardboard);
        TextView metalT = (TextView) findViewById(R.id.metal);
        TextView glassT = (TextView) findViewById(R.id.glass);

        // Update points/trash count
        SharedPreferences accumPoints = getApplicationContext().getSharedPreferences("ACCUM_POINTS", 0);

        int trashCnt = accumPoints.getInt("totalCnt", 0);
        int trash = accumPoints.getInt("trash", 0);
        int paper = accumPoints.getInt("paper", 0);
        int plastic = accumPoints.getInt("plastic", 0);
        int cardboard = accumPoints.getInt("cardboard", 0);
        int metal = accumPoints.getInt("metal", 0);
        int glass = accumPoints.getInt("glass", 0);
        int totalPoints = glass + metal + cardboard + plastic + paper + trash;

        // Update text labels
        pointCntT.setText("Points: " + String.valueOf(totalPoints));
        trashCntT.setText("Total Trash Picked Up: " + String.valueOf(trashCnt));
        trashT.setText("Trash: " + String.valueOf(trash));
        paperT.setText("Paper: " + String.valueOf(paper));
        plasticT.setText("Plastic: " + String.valueOf(plastic));
        cardboardT.setText("Cardboard: " + String.valueOf(cardboard));
        metalT.setText("Metal: " + String.valueOf(metal));
        glassT.setText("Glass: " + String.valueOf(glass));


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
                        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Profile:
                        return true;
                }
                return false;
            }
        }));
    }
}