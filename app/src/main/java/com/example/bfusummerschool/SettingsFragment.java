package com.example.bfusummerschool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private boolean isDarkMode;

    SettingsFragment(boolean isDarkMode){
        this.isDarkMode = isDarkMode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Switch darkSwitch = view.findViewById(R.id.dark_switch);
        darkSwitch.setChecked(isDarkMode);
        darkSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            isDarkMode = darkSwitch.isChecked();
            savePref(isDarkMode);
            getActivity().setTheme(isDarkMode ? R.style.DarkTheme : R.style.AppTheme);
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
        });
    }

    private void savePref(boolean value) {
        SharedPreferences.Editor ed = getActivity().getPreferences(MODE_PRIVATE).edit();
        ed.putBoolean("darkMode", value);
        ed.apply();
    }

}