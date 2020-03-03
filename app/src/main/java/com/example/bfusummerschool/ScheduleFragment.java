package com.example.bfusummerschool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ScheduleFragment extends Fragment implements ScheduleExpandableListAdapter.OnEventClickCallback {

    private ScheduleExpandableListAdapter.OnEventClickCallback callback;
    private ScheduleExpandableListAdapter scheduleExpandableListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ExpandableListView schedule = view.findViewById(R.id.schedule);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReferenceSchedule = mDatabase.getReference(MainActivity.language);

        scheduleExpandableListAdapter = new ScheduleExpandableListAdapter();
        schedule.setAdapter(scheduleExpandableListAdapter);

        scheduleExpandableListAdapter.setCallback(this);

        mReferenceSchedule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinkedHashMap<String, List<String>> days = new LinkedHashMap<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    List<String> events = new ArrayList<>();
                    for(DataSnapshot keyNode1: keyNode.getChildren()) {
                        Event event = keyNode1.getValue(Event.class);
                        String eventData = event.getWhen() + "\n"
                                + event.getWhere() + "\n"
                                + event.getWhich();
                        if(event.getWho() != null) eventData +=  "\n" + event.getWho();
                        events.add(eventData);
                        //RoomOrm will be here
                    }
                    days.put(keyNode.getKey(), events);
                }
                scheduleExpandableListAdapter.setDays(days);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        return view;
    }

    @Override
    public void onEventClick(String event) {
        callback.onEventClick(event);
    }

    public void setCallback(ScheduleExpandableListAdapter.OnEventClickCallback callback) {
        this.callback = callback;
    }
}