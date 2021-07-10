package com.example.gulu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView btnCamera;
    private ImageView btnGallery;
    private ImageView btnLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCamera.setImageResource(R.drawable.camera_pressed);
                openCameraActivity();
                btnCamera.setImageResource(R.drawable.camera);
            }
        });

        btnGallery = findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGallery.setImageResource(R.drawable.gallery_pressed);
                openGalleryActivity();
                btnGallery.setImageResource(R.drawable.gallery);
            }
        });

        btnLibrary = findViewById(R.id.btn_library);
        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLibrary.setImageResource(R.drawable.library_pressed);
                openLibraryActivity();
                btnLibrary.setImageResource(R.drawable.library);
            }
        });
    }

    private void openLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

    private void openCameraActivity() {
    }

    private void openGalleryActivity() {
    }

    private void openLibraryActivity() {
    }
}