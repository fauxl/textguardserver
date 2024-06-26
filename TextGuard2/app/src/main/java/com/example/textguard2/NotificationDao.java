package com.example.textguard2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notifications")
    List<Notification> getAllNotifications();

    @Insert
    void insert(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("DELETE FROM notifications")
    void deleteAll();
}
