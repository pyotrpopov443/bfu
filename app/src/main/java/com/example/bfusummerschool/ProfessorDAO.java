package com.example.bfusummerschool;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProfessorDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfessor(Professor professor);

    @Query("SELECT * FROM professors WHERE language =:language")
    List<Professor> selectProfessor(String language);

}
