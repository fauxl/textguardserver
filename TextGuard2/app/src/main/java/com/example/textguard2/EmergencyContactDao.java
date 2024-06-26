package com.example.textguard2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmergencyContactDao {

    @Query("SELECT * FROM emergency_contacts")
    List<EmergencyContact> getAllContacts();

    @Insert
    void insert(EmergencyContact contact);

    @Delete
    void delete(EmergencyContact contact);
}
