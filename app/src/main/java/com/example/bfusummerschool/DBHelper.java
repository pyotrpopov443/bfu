package com.example.bfusummerschool;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

public class DBHelper {

    private static DBHelper instance = new DBHelper();

    private DayDatabase db;

    @Database(entities = {Day.class}, version = 1, exportSchema = false)
    static abstract class DayDatabase extends RoomDatabase {

        abstract DayDAO getDayDAO();

    }

    private DBHelper() {}

    public static DBHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        db = Room.databaseBuilder(context, DayDatabase.class, "days").build();
    }

    public DayDAO getDayDAO() {
        return db.getDayDAO();
    }
}
