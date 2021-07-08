package com.example.gulu;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TranslateActivity extends AppCompatActivity {
    private String textResultFromImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TranslateFragment.newInstance())
                .commitNow();
        textResultFromImage = getIntent().getStringExtra("result");
    }

    public String getTextResultFromImage() {
        return textResultFromImage;
    }
}
