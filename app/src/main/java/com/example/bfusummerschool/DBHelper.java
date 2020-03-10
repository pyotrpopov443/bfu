package com.example.bfusummerschool;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

public class DBHelper {

    private static DBHelper instance = new DBHelper();

    private DayDatabase dayDatabase;
    private SyllabusDatabase syllabusDatabase;

    @Database(entities = {Day.class}, version = 1, exportSchema = false)
    static abstract class DayDatabase extends RoomDatabase {

        abstract DayDAO getDayDAO();

    }

    @Database(entities = {Syllabus.class}, version = 1, exportSchema = false)
    static abstract class SyllabusDatabase extends RoomDatabase {

        abstract SyllabusDAO getSyllabusDAO();

    }

    private DBHelper() {}

    public static DBHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        dayDatabase = Room.databaseBuilder(context, DayDatabase.class, "days").build();
        syllabusDatabase = Room.databaseBuilder(context, SyllabusDatabase.class, "syllabi").build();
    }

    public DayDAO getDayDAO() {
        return dayDatabase.getDayDAO();
    }

    public SyllabusDAO getSyllabusDAO() {
        return syllabusDatabase.getSyllabusDAO();
    }

}
