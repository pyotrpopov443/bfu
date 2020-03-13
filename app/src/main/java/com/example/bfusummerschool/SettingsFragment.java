package com.example.bfusummerschool;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private Switch darkSwitch;
    private Spinner cohortSpinner;

    private SettingsCallback callback;

    public SettingsFragment(){}

    SettingsFragment(boolean isDarkMode, String cohort){
        Bundle args = new Bundle();
        args.putBoolean(Constants.DARK_MODE, isDarkMode);
        args.putString(Constants.COHORT, cohort);
        setArguments(args);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if(context instanceof SettingsCallback) {
            callback = (SettingsCallback) context;
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        darkSwitch = view.findViewById(R.id.dark_switch);
        cohortSpinner = view.findViewById(R.id.cohort_spinner);
        assert getArguments() != null;
        darkSwitch.setChecked(getArguments().getBoolean(Constants.DARK_MODE));
        darkSwitch.setOnCheckedChangeListener((compoundButton, b) -> callback.onThemeChanged(darkSwitch.isChecked()));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.cohorts, android.R.layout.simple_spinner_dropdown_item);
        cohortSpinner.setAdapter(adapter);
        cohortSpinner.setSelection(adapter.getPosition(getArguments().getString(Constants.COHORT)));
        int iCurrentSelection = cohortSpinner.getSelectedItemPosition();
        cohortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (iCurrentSelection != i) {
                    callback.onCohortChanged(cohortSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    interface SettingsCallback {

        void onThemeChanged(boolean isDarkMode);

        void onCohortChanged(String cohort);

    }

}