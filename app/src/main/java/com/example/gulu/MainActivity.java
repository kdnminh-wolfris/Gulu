package com.example.gulu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView btnCamera;
    private ImageView btnGallery;
    private ImageView btnLibrary;
    private int btnDelayTime = 100; //miliseconds
    private MediaPlayer clickSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        loadDecodedImage(R.id.gulu_logo, R.drawable.gulu_logo, 196, 100);
        loadDecodedImage(R.id.star_line, R.drawable.star_line, 271, 60);
        loadDecodedImage(R.id.btn_camera, R.drawable.camera, 211, 113);
        loadDecodedImage(R.id.btn_gallery, R.drawable.gallery, 211, 113);
        loadDecodedImage(R.id.btn_library, R.drawable.library, 211, 113);

        clickSound = MediaPlayer.create(this, R.raw.button_click);

        btnCamera = findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();
                loadDecodedImage(R.id.btn_camera, R.drawable.camera_pressed, 211, 113);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openCameraActivity();
                        loadDecodedImage(R.id.btn_camera, R.drawable.camera, 211, 113);
                    }
                }, btnDelayTime);
            }
        });

        btnGallery = findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();
                loadDecodedImage(R.id.btn_gallery, R.drawable.gallery_pressed, 211, 113);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openGalleryActivity();
                        loadDecodedImage(R.id.btn_gallery, R.drawable.gallery, 211, 113);
                    }
                }, btnDelayTime);
            }
        });

        btnLibrary = findViewById(R.id.btn_library);
        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();
                loadDecodedImage(R.id.btn_library, R.drawable.library_pressed, 211, 113);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openLibraryActivity();
                        loadDecodedImage(R.id.btn_library, R.drawable.library, 211, 113);
                    }
                }, btnDelayTime);
            }
        });
    }

    private void loadDecodedImage(int imageViewId, int imageId, int width, int height) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setImageBitmap(decodeSampleBitmapFromResource(getResources(), imageId, width, height));
    }

    private Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void openLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

    private void openCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void openGalleryActivity() {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    private void openLibraryActivity() {
    }
}