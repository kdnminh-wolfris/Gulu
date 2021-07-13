package com.example.gulu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView btnCamera;
    private ImageView btnGallery;
    private ImageView btnLibrary;
    private int btnDelayTime = 100; //miliseconds
    private MediaPlayer clickSound;
    public static QDatabase database;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    String storagePermission[];
    String cameraPermission[];
    String scannedText;
    Uri imageUri;
    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //create database
        database = new QDatabase(this, "QLibrary.sqlite", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS History(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Content VARCHAR(5000), Image BLOB)");

        loadDecodedImage(R.id.gulu_logo, R.drawable.gulu_logo, 196, 100);
        loadDecodedImage(R.id.star_line, R.drawable.star_line, 271, 60);
        loadDecodedImage(R.id.btn_camera, R.drawable.camera, 211, 113);
        loadDecodedImage(R.id.btn_gallery, R.drawable.gallery, 211, 113);
        loadDecodedImage(R.id.btn_library, R.drawable.library, 211, 113);

        clickSound = MediaPlayer.create(this, R.raw.button_click);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        scannedText = new String();

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
        if (!checkCamertaPermission()) {
            requestCameraPermission();
        } else {
            pickCamera();
        }
    }

    private void openGalleryActivity() {
        if (!checkStoragePermission()){
            requestStoragePermission();
        } else {
            pickGalery();
        }
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCamertaPermission() {
        boolean resultCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean resultWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return resultCamera && resultWriteStorage;
    }

    private void pickGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean resultWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return resultWriteStorage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGalery();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = data.getData();
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE || requestCode == IMAGE_PICK_GALLERY_CODE)
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    resultUri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    //Transform bitmap -> byte[]
                    Bitmap bitmapLibrary = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmapLibrary.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                    byte[] image = byteArray.toByteArray();

                    TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!recognizer.isOperational()) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = recognizer.detect(frame);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < items.size(); ++i) {
                            TextBlock myItem = items.valueAt(i);
                            stringBuilder.append(myItem.getValue());
                            stringBuilder.append("\n");
                        }
                        scannedText = stringBuilder.toString();
                        MainActivity.database.INSERT_HISTORY(stringBuilder.toString(), image);
                    }
                    openTranslateActivity();
                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openTranslateActivity() {
        Intent intentTranslate = new Intent(this, TranslateActivity.class);
        intentTranslate.putExtra("text", scannedText);
        intentTranslate.putExtra("image", resultUri.toString());
        startActivity(intentTranslate);
    }

    @Override
    public void onBackPressed() {

    }

    private void openLibraryActivity() {
        Intent intentLibrary = new Intent(this, QLibraryActivity.class);
        startActivity(intentLibrary);
    }
}