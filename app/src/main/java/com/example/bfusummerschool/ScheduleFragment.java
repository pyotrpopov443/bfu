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

public class ScheduleFragment extends Fragment {

    private ProgressBar loadingSchedule;
    private SwipeRefreshLayout refresh;

    private String cohort;
    private ExpandableListAdapter scheduleExpandableListAdapter;

    private DatabaseReference referenceSchedule;

    public ScheduleFragment() {}

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

        cohort = getArguments().getString(Constants.COHORT);
        loadingSchedule = view.findViewById(R.id.loading_schedule);

        ExpandableListView scheduleListView = view.findViewById(R.id.schedule_expandable_list_view);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        referenceSchedule = mDatabase.getReference("schedule/" + cohort);

        scheduleExpandableListAdapter = new ExpandableListAdapter(getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Constants.DARK_MODE, false), View.GONE);
        scheduleListView.setAdapter(scheduleExpandableListAdapter);

        refresh = view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this::load);

        load();
    }

    private void load(){
        loadingSchedule.setVisibility(ProgressBar.VISIBLE);
        refresh.setRefreshing(false);
        if (connected()) {
            scheduleExpandableListAdapter.setData(new LinkedHashMap<>());
            AsyncTask.execute(() -> {
                List<Day> days = DBHelper.getInstance().getDayDAO().selectDays(cohort);
                for (Day day : days) {
                    DBHelper.getInstance().getDayDAO().deleteDay(day);
                }
            });
            referenceSchedule.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LinkedHashMap<String, List<String>> daysData = new LinkedHashMap<>();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        List<String> events = new ArrayList<>();
                        for (DataSnapshot keyNode1 : keyNode.getChildren()) {
                            Event event = keyNode1.getValue(Event.class);
                            assert event != null;
                            String eventData = event.getTime();
                            eventData += "\n" + event.getPlace();
                            eventData += "\n" + event.getEvent();
                            if (event.getProfessor() != null) eventData += "\n" + event.getProfessor();
                            if (event.getAssistant() != null) eventData += "\n" + event.getAssistant();
                            events.add(eventData);
                        }
                        Day day = new Day();
                        day.setId(keyNode.getKey() + cohort);
                        day.setDate(keyNode.getKey());
                        day.setCohort(cohort);
                        day.setEvents(events);
                        AsyncTask.execute(() -> DBHelper.getInstance().getDayDAO().insertDay(day));
                        daysData.put(day.getDate(), day.getEvents());
                    }
                    scheduleExpandableListAdapter.setData(daysData);
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
                for (Day day : days) {
                    daysData.put(day.getDate(), day.getEvents());
                }
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    scheduleExpandableListAdapter.setData(daysData);
                    loadingSchedule.setVisibility(ProgressBar.INVISIBLE);
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
