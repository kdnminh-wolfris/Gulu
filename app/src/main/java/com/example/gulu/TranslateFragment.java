package com.example.gulu;
// source : https://github.com/googlesamples/mlkit
import android.annotation.SuppressLint;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class TranslateFragment extends Fragment {

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
        final Button switchButton = view.findViewById(R.id.buttonSwitchLang);
        final ToggleButton sourceSyncButton = view.findViewById(R.id.buttonSyncSource);
        final ToggleButton targetSyncButton = view.findViewById(R.id.buttonSyncTarget);
        final TextInputEditText srcTextView = view.findViewById(R.id.sourceText);
        final TextView targetTextView = view.findViewById(R.id.targetText);
        final TextView downloadedModelsTextView = view.findViewById(R.id.downloadedModels);
        final Spinner sourceLangSelector = view.findViewById(R.id.sourceLangSelector);
        final Spinner targetLangSelector = view.findViewById(R.id.targetLangSelector);

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
        targetLangSelector.setSelection(adapter.getPosition(new TranslateViewModel.Language("es")));
        sourceLangSelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setProgressText(targetTextView);
                        viewModel.sourceLang.setValue(adapter.getItem(position));
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
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        targetTextView.setText("");
                    }
                });

        switchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProgressText(targetTextView);
                        int sourceLangPosition = sourceLangSelector.getSelectedItemPosition();
                        sourceLangSelector.setSelection(targetLangSelector.getSelectedItemPosition());
                        targetLangSelector.setSelection(sourceLangPosition);
                    }
                });

        // Set up toggle buttons to delete or download remote models locally.
        sourceSyncButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TranslateViewModel.Language language =
                                adapter.getItem(sourceLangSelector.getSelectedItemPosition());
                        if (isChecked) {
                            viewModel.downloadLanguage(language);
                        } else {
                            viewModel.deleteLanguage(language);
                        }
                    }
                });
        targetSyncButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TranslateViewModel.Language language =
                                adapter.getItem(targetLangSelector.getSelectedItemPosition());
                        if (isChecked) {
                            viewModel.downloadLanguage(language);
                        } else {
                            viewModel.deleteLanguage(language);
                        }
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
                        String output =
                                getContext().getString(R.string.downloaded_models_label, translateRemoteModels);
                        downloadedModelsTextView.setText(output);
                        sourceSyncButton.setChecked(
                                translateRemoteModels.contains(
                                        adapter.getItem(sourceLangSelector.getSelectedItemPosition()).getCode()));
                        targetSyncButton.setChecked(
                                translateRemoteModels.contains(
                                        adapter.getItem(targetLangSelector.getSelectedItemPosition()).getCode()));
                    }
                });
    }

    private void setProgressText(TextView tv) {
        tv.setText(getContext().getString(R.string.translate_progress));
    }
}