package com.example.bfusummerschool;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private Switch darkSwitch;
    private Spinner cohortSpinner;

    private SettingsCallback callback;

    SettingsFragment(boolean isDarkMode, int cohort){
        Bundle args = new Bundle();
        args.putBoolean("isDarkMode", isDarkMode);
        args.putInt("cohort", cohort);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if(context instanceof SettingsCallback) {
            callback = (SettingsCallback) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        darkSwitch = view.findViewById(R.id.dark_switch);
        cohortSpinner = view.findViewById(R.id.cohort_spinner);
        darkSwitch.setChecked(getArguments().getBoolean("isDarkMode"));
        darkSwitch.setOnCheckedChangeListener((compoundButton, b) -> callback.onThemeChanged(darkSwitch.isChecked()));
        cohortSpinner.setSelection(getArguments().getInt("cohort"));
        int iCurrentSelection = cohortSpinner.getSelectedItemPosition();
        cohortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (iCurrentSelection != i) {
                    callback.onCohortChanged(cohortSpinner.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    interface SettingsCallback {

        void onThemeChanged(boolean isDarkMode);
        void onCohortChanged(int cohort);

    }

}