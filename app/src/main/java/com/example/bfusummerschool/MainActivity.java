package com.example.bfusummerschool;

import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final static String RU = "RU";
    final static String EN = "EN";
    String language;

    ScheduleExpandableListAdapter scheduleExpandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        language = setLanguage();

        ExpandableListView schedule = findViewById(R.id.schedule);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReferenceSchedule = mDatabase.getReference(language);

        scheduleExpandableListAdapter = new ScheduleExpandableListAdapter();
        schedule.setAdapter(scheduleExpandableListAdapter);

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
                    }
                    days.put(keyNode.getKey(), events);
                }
                scheduleExpandableListAdapter.setDays(days);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private String setLanguage(){
        String lang;
        if (Locale.getDefault().getDisplayLanguage().equals("русский")){
            lang = RU;
        }else {
            lang = EN;
        }
        return lang;
    }

}