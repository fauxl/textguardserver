package com.example.textguard2;


import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    private final Set<String> processedNotifications = new HashSet<>();

    // Questo è un prototipo di una lista c cc
    private final Set<String> appList = new HashSet<>(Arrays.asList("com.instagram.android", "org.telegram.messenger", "com.google.android.apps.messaging")); // "com.whatsapp"
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String packageName = sbn.getPackageName();


        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        int numberFrom = notification.number;
        String date = dateFormat.format(new Date(notification.when));
        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence textCharSeq = extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = textCharSeq != null ? textCharSeq.toString() : "";


        // int notificationID = (int) ((notification.when) % Integer.MAX_VALUE);

        String clearUniqueNotificationKey = packageName + ":" + title + ":" + text + ":" + date;
        String UniqueID = UniqueIdGenerator(clearUniqueNotificationKey);


        // Non mostrare notifiche irrilevanti come quelle che ci ricordano quanti messaggi non abbiamo ancora letto
        try {
            if (title.contains("new messages") || title.contains("WhatsApp")) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        // Verifica se la notifica è già stata processata
        if (appList.contains(packageName)){
            if (!processedNotifications.contains(UniqueID)) {
                Log.d(TAG, "Notification Posted - Package: " + packageName);
                Log.i(TAG, "ID: " + UniqueID);
                Log.i(TAG, "From: " + numberFrom);
                Log.i(TAG, "Date: " + date);
                Log.i(TAG, "Title: " + title);
                Log.i(TAG, "Text: " + text);
                processedNotifications.add(UniqueID);
            }
        }
    }

    public static String UniqueIdGenerator(String inputString) {
        try{
            // Crea un'istanza di MessageDigest con SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calcola l'hash della stringa di input
            byte[] hashBytes = digest.digest(inputString.getBytes(StandardCharsets.UTF_8));

            // Converti il byte array in una stringa esadecimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Restituisce la stringa esadecimale come ID univoco
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        // Opzionalmente, puoi rimuovere le notifiche elaborate da qui se necessario
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence textCharSeq = extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = textCharSeq != null ? textCharSeq.toString() : "";

        String uniqueNotificationKey = packageName + ":" + title + ":" + text;
        processedNotifications.remove(uniqueNotificationKey);

        Log.d(TAG, "Notification Removed - Package: " + packageName);
    }
}
