package com.example.bfusummerschool;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DayDAO {

    @Insert
    void insertDay(Day day);

    @Query("SELECT * FROM days WHERE cohort =:cohort")
    List<Day> selectDays(String cohort);

    @Update
    void updateDay(Day day);

}
