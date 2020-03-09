package com.example.bfusummerschool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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

public class ScheduleFragment extends Fragment {

    private ProgressBar loadingSchedule;

    private String cohort;
    private ScheduleExpandableListAdapter scheduleExpandableListAdapter;

    ScheduleFragment(String cohort) {
        Bundle args = new Bundle();
        args.putString(Constants.COHORT, cohort);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        cohort = getArguments().getString("cohort");
        loadingSchedule = view.findViewById(R.id.loading_schedule);
        loadingSchedule.setVisibility(ProgressBar.VISIBLE);

        ExpandableListView schedule = view.findViewById(R.id.schedule);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReferenceSchedule = mDatabase.getReference("schedule/" + cohort);

        scheduleExpandableListAdapter = new ScheduleExpandableListAdapter();
        schedule.setAdapter(scheduleExpandableListAdapter);

        if (connected()) {
            mReferenceSchedule.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LinkedHashMap<String, List<String>> daysData = new LinkedHashMap<>();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        List<String> events = new ArrayList<>();
                        for (DataSnapshot keyNode1 : keyNode.getChildren()) {
                            Event event = keyNode1.getValue(Event.class);
                            assert event != null;
                            String eventData = event.getWhen() + "\n"
                                    + event.getWhere() + "\n"
                                    + event.getWhich();
                            if (event.getWho() != null) eventData += "\n" + event.getWho();
                            events.add(eventData);
                        }
                        Day day = new Day();
                        day.setDate(keyNode.getKey());
                        day.setEvents(events);
                        day.setCohort(cohort);
                        AsyncTask.execute(() -> DBHelper.getInstance().getDayDAO().insertDay(day));

                        daysData.put(day.getDate(), day.getEvents());
                    }
                    scheduleExpandableListAdapter.setDays(daysData);
                    loadingSchedule.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            AsyncTask.execute(() -> {
                LinkedHashMap<String, List<String>> daysData = new LinkedHashMap<>();
                List<Day> days = DBHelper.getInstance().getDayDAO().selectDays(cohort);
                for (Day d : days) {
                    List<String> events = new ArrayList<>(d.getEvents());
                    Day day = new Day();
                    day.setDate(d.getDate());
                    day.setEvents(events);
                    daysData.put(day.getDate(), day.getEvents());
                }
                getActivity().runOnUiThread(() -> {
                    scheduleExpandableListAdapter.setDays(daysData);
                    loadingSchedule.setVisibility(ProgressBar.INVISIBLE);
                });
            });

        }
    }

    private boolean connected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

}
