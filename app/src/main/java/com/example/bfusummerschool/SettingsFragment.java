package com.example.bfusummerschool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    private Switch darkSwitch;
    private Spinner cohortSpinner;

    private SettingsCallback callback;

    private DatabaseReference referenceFeedback;

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
        Button buttonFeedback = view.findViewById(R.id.btn_feedback);
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        referenceFeedback = mDatabase.getReference("feedback");
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
            public void onNothingSelected(AdapterView<?> adapterView) {}

        });

        buttonFeedback.setOnClickListener(v -> {
            if (!connected()){
                Toast.makeText(getContext(), R.string.internet, Toast.LENGTH_SHORT).show();
                return;
            }
            final EditText feedback = new EditText(getContext());
            feedback.setHint(R.string.feedback_hint);
            if (!darkSwitch.isChecked()) {
                feedback.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            feedback.setLayoutParams(lp);
            AlertDialog feedbackDialog = new AlertDialog.Builder(getActivity())
                    .setView(feedback)
                    .setPositiveButton(R.string.send, (dialog, which) -> {
                        String id = referenceFeedback.push().getKey();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
                        Date date = new Date(System.currentTimeMillis());
                        referenceFeedback.child(id).child("text").setValue(feedback.getText().toString());
                        referenceFeedback.child(id).child("cohort").setValue(cohortSpinner.getSelectedItem().toString());
                        referenceFeedback.child(id).child("time").setValue(formatter.format(date));
                        Toast.makeText(getContext(), R.string.sended, Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton(R.string.clear, null)
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            feedbackDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v1 -> feedback.setText(""));
            if(!darkSwitch.isChecked()) {
                feedbackDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                feedbackDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                feedbackDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });

    }

    private boolean connected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    interface SettingsCallback {

        void onThemeChanged(boolean isDarkMode);

        void onCohortChanged(String cohort);

    }

}