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

public class MainActivity extends AppCompatActivity {
   private  Button btnTranslate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openLoadingActivity();

        setContentView(R.layout.activity_main);
        btnTranslate = (Button) findViewById(R.id.btn_translate);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTranslateActivity();
//                Intent intentTranslate = new Intent(v.getContext(), TranslateActivity.class);
//                startActivity(intentTranslate);
            }
        });

    }

    private void openLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

    private void openTranslateActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

//    public void onTranslateView(View view) {
//        Intent intentTranslate = new Intent(this, TranslateActivity.class);
//        startActivity(intentTranslate);
//    }
}