package com.example.cleanupourspace_v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private Button button;

    private Button mCaptureBtn;
    private ImageView mImageView;
    private byte[] image;

    public Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //Initialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Camera Selected
        bottomNavigationView.setSelectedItemId(R.id.camera);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener((new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.camera:
                        return true;
                    case R.id.Profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        }));
        button = (Button) findViewById(R.id.analyze_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInference();
            }
        });

        mImageView = findViewById(R.id.image_view);
        mCaptureBtn = findViewById(R.id.capture_image_btn);

        //button click
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //if system os is newer than marshmallow, request a runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {
                        //permission not enabled, make request
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show request popup
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        //permission granted
                        openCamera();
                    }
                } else {
                    //system os older than marshmallow
                    openCamera();
                }

            }


        });


    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(camIntent, IMAGE_CAPTURE_CODE);

    }
    //handling permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Active when user hits allow or deny from permission request popup
        switch(requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission from popup is positive
                    openCamera();
                } else {
                    //permission denied
                    Toast.makeText(this, "Access DENIED", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image is taken

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //set image captures to ImageView
            mImageView.setImageURI(image_uri);
        }
    }

    /** Called when the user taps the Take a Photo button */
    public void openInference() {
        Intent intent = new Intent(this, InferenceActivity.class);
        intent.setData(image_uri);
        startActivity(intent);
    }
}