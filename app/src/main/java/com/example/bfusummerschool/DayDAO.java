package com.example.bfusummerschool;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DayDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDay(Day day);

    @Query("SELECT * FROM days WHERE cohort =:cohort")
    List<Day> selectDays(String cohort);

    @Delete
    void deleteDay(Day day);

}
