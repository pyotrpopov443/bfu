package com.example.bfusummerschool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import ru.snowmaze.expandablelistview.ExpandableListView;

public class AdministrationFragment extends Fragment {

    private ProgressBar loadingAdministration;

    private String language;
    private ExpandableListAdapter administrationExpandableListAdapter;

    public AdministrationFragment(){}

    AdministrationFragment(String language) {
        Bundle args = new Bundle();
        args.putString(Constants.LANGUAGE, language);
        setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_administration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        language = getArguments().getString(Constants.LANGUAGE);
        loadingAdministration = view.findViewById(R.id.loading_administration);
        loadingAdministration.setVisibility(ProgressBar.VISIBLE);

        ExpandableListView scheduleListView = view.findViewById(R.id.administration_expandable_list_view);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference referenceAdministration = mDatabase.getReference("administration");

        administrationExpandableListAdapter = new ExpandableListAdapter(getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Constants.DARK_MODE, false), View.VISIBLE);
        scheduleListView.setAdapter(administrationExpandableListAdapter);

        if (connected()) {
            referenceAdministration.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LinkedHashMap<String, List<String>> professorsData = new LinkedHashMap<>();
                    List<String> photos = new ArrayList<>();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        List<String> courses = new ArrayList<>();
                        StringBuilder bio = new StringBuilder();
                        Professor professor = new Professor();
                        for (DataSnapshot keyNode1 : keyNode.child(language).getChildren()) {
                            bio.append(keyNode1.getValue()).append("\n");
                            professor.setName(Objects.requireNonNull(keyNode1.getKey()));
                        }
                        bio = new StringBuilder(bio.substring(0, bio.length() - 1));
                        courses.add(bio.toString());
                        professor.setBio(bio.toString());
                        professor.setLanguage(language);
                        professor.setPhoto(keyNode.getKey());
                        AsyncTask.execute(() -> DBHelper.getInstance().getProfessorDao().insertProfessor(professor));
                        photos.add(professor.getPhoto());
                        professorsData.put(professor.getName(), courses);
                    }
                    administrationExpandableListAdapter.setData(professorsData, photos, connected());
                    loadingAdministration.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            AsyncTask.execute(() -> {
                LinkedHashMap<String, List<String>> professorsData = new LinkedHashMap<>();
                List<Professor> professors = DBHelper.getInstance().getProfessorDao().selectProfessor(language);
                List<String> photos = new ArrayList<>();
                for (Professor professor : professors) {
                    List<String> bio = new ArrayList<>();
                    bio.add(professor.getBio());
                    photos.add(professor.getPhoto());
                    professorsData.put(professor.getName(), bio);
                }
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    administrationExpandableListAdapter.setData(professorsData, photos, connected());
                    loadingAdministration.setVisibility(ProgressBar.INVISIBLE);
                });
            });
        }
    }

    private boolean connected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

}
