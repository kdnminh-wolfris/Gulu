package com.example.gulu;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class TranslateActivity extends AppCompatActivity {
    private String textResultFromImage;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity);
        getSupportActionBar().hide();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TranslateFragment.newInstance())
                .commitNow();
        textResultFromImage = getIntent().getStringExtra("text");
        imageUri = Uri.parse(getIntent().getStringExtra("image"));
    }

    public String getTextResultFromImage() {
        return textResultFromImage;
    }

    public Uri getImageUri() { return imageUri; }
}
