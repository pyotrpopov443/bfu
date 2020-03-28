package com.example.bfusummerschool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class SyllabiFragment extends Fragment {

    private ProgressBar loadingSyllabi;
    private SwipeRefreshLayout refresh;

    private String language;
    private ExpandableListAdapter syllabiExpandableListAdapter;

    private DatabaseReference referenceSyllabi;

    public SyllabiFragment(){}

    SyllabiFragment(String language) {
        Bundle args = new Bundle();
        args.putString(Constants.LANGUAGE, language);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_syllabi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        language = getArguments().getString(Constants.LANGUAGE);
        loadingSyllabi = view.findViewById(R.id.loading_syllabi);

        ExpandableListView syllabiListView = view.findViewById(R.id.syllabi_expandable_list_view);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        referenceSyllabi = mDatabase.getReference("syllabi/" + language);

        syllabiExpandableListAdapter = new ExpandableListAdapter(getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Constants.DARK_MODE, false), View.GONE);
        syllabiListView.setAdapter(syllabiExpandableListAdapter);

        refresh = view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this::load);

        load();
    }

    private void load(){
        loadingSyllabi.setVisibility(ProgressBar.VISIBLE);
        refresh.setRefreshing(false);
        if (connected()) {
            syllabiExpandableListAdapter.setData(new LinkedHashMap<>());
            AsyncTask.execute(() -> {
                List<Syllabus> syllabi = DBHelper.getInstance().getSyllabusDAO().selectSyllabus(language);
                for (Syllabus syllabus : syllabi) {
                    DBHelper.getInstance().getSyllabusDAO().deleteSyllabus(syllabus);
                }
            });
            referenceSyllabi.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LinkedHashMap<String, List<String>> professorsData = new LinkedHashMap<>();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        List<String> courses = new ArrayList<>();
                        StringBuilder description = new StringBuilder();
                        for (DataSnapshot keyNode1 : keyNode.getChildren()) {
                            description.append(keyNode1.getValue()).append("\n");
                        }
                        description = new StringBuilder(description.substring(0, description.length() - 1));
                        courses.add(description.toString());
                        Syllabus syllabus = new Syllabus();
                        syllabus.setCourse(Objects.requireNonNull(keyNode.getKey()));
                        syllabus.setDescription(courses.get(0));
                        syllabus.setLanguage(language);
                        AsyncTask.execute(() -> DBHelper.getInstance().getSyllabusDAO().insertSyllabus(syllabus));
                        professorsData.put(syllabus.getCourse(), courses);
                    }
                    syllabiExpandableListAdapter.setData(professorsData);
                    loadingSyllabi.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            AsyncTask.execute(() -> {
                LinkedHashMap<String, List<String>> professorsData = new LinkedHashMap<>();
                List<Syllabus> syllabi = DBHelper.getInstance().getSyllabusDAO().selectSyllabus(language);
                for (Syllabus syllabus : syllabi) {
                    List<String> description = new ArrayList<>();
                    description.add(syllabus.getDescription());
                    professorsData.put(syllabus.getCourse(), description);
                }
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    syllabiExpandableListAdapter.setData(professorsData);
                    loadingSyllabi.setVisibility(ProgressBar.INVISIBLE);
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
