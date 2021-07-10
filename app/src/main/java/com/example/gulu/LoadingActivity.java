package com.example.gulu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class LoadingActivity extends AppCompatActivity {
    private ImageView guluImageView;
    private ImageView loadingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        guluImageView = findViewById(R.id.imv_gulu);
        setGuluText();

        loadingImageView = findViewById(R.id.imv_loading);
        setLoadingText();
    }

    private void setLoadingText() {
        final Integer[] loadingImages = {R.drawable.loading_0, R.drawable.loading_1, R.drawable.loading_2, R.drawable.loading_3};
        final Handler loadingHandler = new Handler();
        Runnable runnable = new Runnable() {
            int loadingImgIndex = 0;
            public void run() {
                loadingImageView.setImageResource(loadingImages[loadingImgIndex]);
                loadingImgIndex++;
                if (loadingImgIndex >= loadingImages.length)
                    loadingImgIndex = 0;
                loadingHandler.postDelayed(this, 500);
            }
        };
        loadingHandler.postDelayed(runnable, 500);
    }

    public void setGuluText() {
        final Integer[] loadingImages = {R.drawable.gulu_0, R.drawable.gulu_1, R.drawable.gulu_2, R.drawable.gulu_3, R.drawable.gulu_4};
        final Handler loadingHandler = new Handler();
        Runnable runnable = new Runnable() {
            int loadingImgIndex = 0;
            public void run() {
                guluImageView.setImageResource(loadingImages[loadingImgIndex]);
                loadingImgIndex++;
                if (loadingImgIndex >= loadingImages.length)
                    loadingImgIndex = 0;
                loadingHandler.postDelayed(this, 500);
            }
        };
        loadingHandler.postDelayed(runnable, 500);
    }
}