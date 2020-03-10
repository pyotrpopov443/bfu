package com.example.bfusummerschool;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "syllabi")
public class Syllabus {

    @PrimaryKey
    @NonNull
    private String course;
    private String description;
    private String language;

    @NonNull
    public String getCourse() {
        return course;
    }

    public void setCourse(@NonNull String course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
