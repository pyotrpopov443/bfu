package com.example.bfusummerschool;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "days")
@TypeConverters({ListConverter.class})
public class Day {
    @PrimaryKey
    @NonNull
    private List<String> events;
    private String date;
    private String cohort;

    @NonNull
    public List<String> getEvents() {
        return events;
    }

    public void setEvents(@NonNull List<String> events) {
        this.events = events;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCohort() {
        return cohort;
    }

    public void setCohort(String cohort) {
        this.cohort = cohort;
    }
}
