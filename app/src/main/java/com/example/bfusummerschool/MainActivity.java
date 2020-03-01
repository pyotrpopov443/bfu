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
    private LinkedHashMap<String, List<String>> days;
    private ArrayList<String> events;
    ScheduleExpandableListAdapter scheduleExpandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schedule = findViewById(R.id.schedule);

        mDatabase = FirebaseDatabase.getInstance();
        mReferenceSchedule = mDatabase.getReference(language);

        days = new LinkedHashMap<>();
        events = new ArrayList<>();
        mReferenceSchedule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode1 : dataSnapshot.getChildren()){
                    events.clear();
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
                scheduleExpandableListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        scheduleExpandableListAdapter = new ScheduleExpandableListAdapter(days);
        schedule.setAdapter(scheduleExpandableListAdapter);
    }

    private void saveMap(LinkedHashMap<String,List<String>> inputMap){
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(language).apply();
            editor.putString(language, jsonString);
            editor.commit();
        }
    }

}