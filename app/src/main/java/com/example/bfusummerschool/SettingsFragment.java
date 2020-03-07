package com.example.bfusummerschool;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private SettingsCallback callback;

    SettingsFragment(boolean isDarkMode){
        Bundle args = new Bundle();
        args.putBoolean("isDarkMode", isDarkMode);
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
        Switch darkSwitch = view.findViewById(R.id.dark_switch);
        darkSwitch.setChecked(getArguments().getBoolean("isDarkMode"));
        darkSwitch.setOnCheckedChangeListener((compoundButton, b) -> callback.onThemeChanged(darkSwitch.isChecked()));
    }

    interface SettingsCallback {

        void onThemeChanged(boolean isDarkMode);

    }
}