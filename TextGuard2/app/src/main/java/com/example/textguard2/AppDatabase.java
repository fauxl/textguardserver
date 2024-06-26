package com.example.textguard2;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {EmergencyContact.class, Notification.class}, version = 2) // Incrementa la versione a 2
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract EmergencyContactDao emergencyContactDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "textguard_database")
                            .fallbackToDestructiveMigration() // Usa la migrazione distruttiva per questo esempio
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
