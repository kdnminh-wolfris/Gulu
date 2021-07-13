package com.example.gulu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class QLibraryActivity extends AppCompatActivity {

    private ArrayList<QHistoryItem> historyItems;
    private RecyclerView mRecyclerView;
    private QHistoryAdapter mAdapter;
    private ImageView deleteAllBtn;
    private MediaPlayer clickSound;
    private int btnDelayTime = 100; //miliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.library_fragment);

        clickSound = MediaPlayer.create(this, R.raw.button_click);

        deleteAllBtn = findViewById(R.id.deleteAllBtn);
        loadDecodedImage(R.id.deleteAllBtn, R.drawable.clear_all, 150, 75);

        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();
                loadDecodedImage(R.id.deleteAllBtn, R.drawable.clear_all_pressed, 150, 75);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogDeleteAll();
                        UpdateData();
                        loadDecodedImage(R.id.deleteAllBtn, R.drawable.clear_all, 150, 75);
                    }
                }, btnDelayTime);

            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        historyItems = new ArrayList<>();

        mAdapter = new QHistoryAdapter(this, historyItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Get Data
        UpdateData();
    }

    public void UpdateData(){
        Cursor cursor = MainActivity.database.GetData("SELECT * FROM History");
        historyItems.clear();
        while (cursor.moveToNext()){
            historyItems.add(new QHistoryItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getBlob(2)
            ));
        }
        mAdapter.notifyDataSetChanged();
    }

    public void DialogDelete(final int id){
        AlertDialog.Builder dialogDel = new AlertDialog.Builder(this);
        dialogDel.setMessage("Are you sure you want to clear this translation?");

        dialogDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.database.QueryData("DELETE FROM History WHERE Id = '"+ id +"' ");
                Toast.makeText(QLibraryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                UpdateData();
            }
        });
        dialogDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogDel.show();
    }

    public void DialogDeleteAll(){
        AlertDialog.Builder dialogDelAll = new AlertDialog.Builder(this);
        dialogDelAll.setMessage("Are you sure you want to clear all history");

        dialogDelAll.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.database.QueryData("DELETE FROM History");
                Toast.makeText(QLibraryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                UpdateData();
            }
        });
        dialogDelAll.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogDelAll.show();

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

}