package com.example.bfusummerschool;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SyllabusDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSyllabus(Syllabus syllabus);

    @Query("SELECT * FROM syllabi WHERE language =:language")
    List<Syllabus> selectSyllabus(String language);

    @Delete
    void deleteSyllabus(Syllabus syllabus);

}
