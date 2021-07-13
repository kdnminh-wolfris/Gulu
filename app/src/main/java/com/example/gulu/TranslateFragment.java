package com.example.gulu;
// source : https://github.com/googlesamples/mlkit
import android.annotation.SuppressLint;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class TranslateFragment extends Fragment {
    private boolean btnOrientation = false;

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.translate_fragment, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView switchButton = view.findViewById(R.id.buttonSwitchLang);
        final TextInputEditText srcTextView = view.findViewById(R.id.sourceText);
        final TextView targetTextView = view.findViewById(R.id.targetText);
        final Spinner sourceLangSelector = view.findViewById(R.id.sourceLangSelector);
        final Spinner targetLangSelector = view.findViewById(R.id.targetLangSelector);
        final ImageView scanningImage = view.findViewById(R.id.scanningImage);

        TranslateActivity translateActivity =(TranslateActivity) getActivity();
        String textResultFromImage = translateActivity.getTextResultFromImage();
        final TranslateViewModel viewModel = new ViewModelProvider(this).get(TranslateViewModel.class);
        if (textResultFromImage.length() > 0){
            srcTextView.setText(textResultFromImage);
            viewModel.sourceText.postValue(textResultFromImage);
        }
        // Get available language list and set up source and target language spinners
        // with default selections.
        final ArrayAdapter<TranslateViewModel.Language> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        viewModel.getAvailableLanguages());
        sourceLangSelector.setAdapter(adapter);
        targetLangSelector.setAdapter(adapter);
        sourceLangSelector.setSelection(adapter.getPosition(new TranslateViewModel.Language("en")));
        targetLangSelector.setSelection(adapter.getPosition(new TranslateViewModel.Language("vi")));
        sourceLangSelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setProgressText(targetTextView);
                        viewModel.sourceLang.setValue(adapter.getItem(position));
                        TranslateViewModel.Language language =
                                adapter.getItem(sourceLangSelector.getSelectedItemPosition());
                        if (!viewModel.getAvailableLanguages().contains(language)){
                            viewModel.downloadLanguage(language);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        targetTextView.setText("");
                    }
                });
        targetLangSelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setProgressText(targetTextView);
                        viewModel.targetLang.setValue(adapter.getItem(position));
                        TranslateViewModel.Language language =
                                adapter.getItem(sourceLangSelector.getSelectedItemPosition());
                        if (!viewModel.getAvailableLanguages().contains(language)){
                            viewModel.downloadLanguage(language);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        targetTextView.setText("");
                    }
                });

        loadDecodedImage(R.id.buttonSwitchLang, R.drawable.switch_lang, 85, 85);
        switchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProgressText(targetTextView);
                        int sourceLangPosition = sourceLangSelector.getSelectedItemPosition();
                        sourceLangSelector.setSelection(targetLangSelector.getSelectedItemPosition());
                        targetLangSelector.setSelection(sourceLangPosition);
                        if (btnOrientation)
                            loadDecodedImage(R.id.buttonSwitchLang, R.drawable.switch_lang, 85, 85);
                        else
                            loadDecodedImage(R.id.buttonSwitchLang, R.drawable.switch_lang_reverse, 85, 85);
                        btnOrientation = !btnOrientation;
                    }
                });

        // Translate input text as it is typed
        srcTextView.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        setProgressText(targetTextView);
                        viewModel.sourceText.postValue(s.toString());
                    }
                });
        viewModel.translatedText.observe(
                getViewLifecycleOwner(),
                new Observer<TranslateViewModel.ResultOrError>() {
                    @Override
                    public void onChanged(TranslateViewModel.ResultOrError resultOrError) {
                        if (resultOrError.error != null) {
                            srcTextView.setError(resultOrError.error.getLocalizedMessage());
                        } else {
                            targetTextView.setText(resultOrError.result);
                        }
                    }
                });

        // Update sync toggle button states based on downloaded models list.
        viewModel.availableModels.observe(
                getViewLifecycleOwner(),
                new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable List<String> translateRemoteModels) {
                    }
                });

        targetTextView.setMovementMethod(new ScrollingMovementMethod());
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.text_border);
        targetTextView.setBackground(new BitmapDrawable(getResources(), bitmap));targetTextView.setBackground(new BitmapDrawable(getResources(), bitmap));
        srcTextView.setBackground(new BitmapDrawable(getResources(), bitmap));targetTextView.setBackground(new BitmapDrawable(getResources(), bitmap));

        Uri imageUri = translateActivity.getImageUri();
        scanningImage.setImageURI(imageUri);
    }

    private void setProgressText(TextView tv) {
        tv.setText(getContext().getString(R.string.translate_progress));
    }

    private void loadDecodedImage(int imageViewId, int imageId, int width, int height) {
        ImageView imageView = getView().findViewById(imageViewId);
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