package com.example.bfusummerschool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView schedule;

    final static String RU = "RU";
    final static String EN = "EN";
    String language = RU;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceSchedule;
    ScheduleExpandableListAdapter scheduleExpandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schedule = findViewById(R.id.schedule);

        mDatabase = FirebaseDatabase.getInstance();
        mReferenceSchedule = mDatabase.getReference(language);

        mReferenceSchedule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinkedHashMap<String, List<String>> days = new LinkedHashMap<>();
                for(DataSnapshot keyNode1 : dataSnapshot.getChildren()){
                    List<String> events = new ArrayList<>();
                    for(DataSnapshot keyNode: keyNode1.getChildren()) {
                        Event event = keyNode.getValue(Event.class);
                        String eventData = event.getWhen() + "\n"
                                + event.getWhere() + "\n"
                                + event.getWhich();
                        if(event.getWho() != null) eventData +=  "\n" + event.getWho();
                        events.add(eventData);
                    }
                    days.put(keyNode1.getKey(), events);
                }
                scheduleExpandableListAdapter.setDays(days);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        scheduleExpandableListAdapter = new ScheduleExpandableListAdapter();
        schedule.setAdapter(scheduleExpandableListAdapter);
    }

}