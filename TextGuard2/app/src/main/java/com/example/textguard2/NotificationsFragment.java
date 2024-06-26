package com.example.textguard2;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NotificationsFragment extends Fragment {
    private AppDatabase db;
    private NotificationsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = AppDatabase.getDatabase(getContext());
        new LoadNotificationsTask(db, notifications -> {
            adapter = new NotificationsAdapter(notifications);
            recyclerView.setAdapter(adapter);
        }).execute();

        // Inserisci notifiche fake
        new InsertFakeNotificationsTask(db, getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null)).execute();

        // Imposta il pulsante per eliminare tutte le notifiche
        Button deleteAllButton = view.findViewById(R.id.button_delete_all);
        deleteAllButton.setOnClickListener(v -> deleteAllNotifications());


        TextView fragmentTitle = view.findViewById(R.id.fragment_title);
        fragmentTitle.setText("Active Warning");

    }

    private void deleteAllNotifications() {
        new DeleteAllNotificationsTask(db, () -> {
            // Ricarica la lista delle notifiche dopo aver eliminato tutto
            new LoadNotificationsTask(db, notifications -> {
                adapter = new NotificationsAdapter(notifications);
                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            }).execute();
        }).execute();
    }

    private static class LoadNotificationsTask extends AsyncTask<Void, Void, List<Notification>> {
        private AppDatabase db;
        private OnNotificationsLoadedListener listener;

        LoadNotificationsTask(AppDatabase db, OnNotificationsLoadedListener listener) {
            this.db = db;
            this.listener = listener;
        }

        @Override
        protected List<Notification> doInBackground(Void... voids) {
            return db.notificationDao().getAllNotifications();
        }

        @Override
        protected void onPostExecute(List<Notification> notifications) {
            listener.onNotificationsLoaded(notifications);
        }
    }

    private static class InsertFakeNotificationsTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        private Cursor callLogCursor;

        InsertFakeNotificationsTask(AppDatabase db, Cursor callLogCursor) {
            this.db = db;
            this.callLogCursor = callLogCursor;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            NotificationDao notificationDao = db.notificationDao();
            Random random = new Random();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            // Recupera chiamate a caso dal registro delle chiamate in entrata
            List<String> incomingCalls = new ArrayList<>();
            if (callLogCursor != null) {
                int numberIndex = callLogCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int typeIndex = callLogCursor.getColumnIndex(CallLog.Calls.TYPE);
                int dateIndex = callLogCursor.getColumnIndex(CallLog.Calls.DATE);

                while (callLogCursor.moveToNext()) {
                    int callType = callLogCursor.getInt(typeIndex);
                    if (callType == CallLog.Calls.INCOMING_TYPE) {
                        String number = callLogCursor.getString(numberIndex);
                        long dateMillis = callLogCursor.getLong(dateIndex);
                        String date = dateFormat.format(new Date(dateMillis));
                        incomingCalls.add(number + " at " + date);
                    }
                }
                callLogCursor.close();
            }

            // Creare notifiche fake
            for (int i = 0; i < 2 && !incomingCalls.isEmpty(); i++) {
                int randomIndex = random.nextInt(incomingCalls.size());
                String callInfo = incomingCalls.get(randomIndex);

                Notification notification = new Notification();
                notification.setTitle("Alert!");
                notification.setContent("Suspicious activity detected from " + callInfo);

                Notification notification2 = new Notification();
                notification2.setTitle("Warning!");
                notification2.setContent("Suspicious activity detected from " + callInfo);

                // Inserire notifiche fake nel database
                notificationDao.insert(notification);
                notificationDao.insert(notification2);
            }

            return null;
        }
    }

    private static class DeleteAllNotificationsTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        private OnNotificationsDeletedListener listener;

        DeleteAllNotificationsTask(AppDatabase db, OnNotificationsDeletedListener listener) {
            this.db = db;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.notificationDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listener.onNotificationsDeleted();
        }
    }

    interface OnNotificationsLoadedListener {
        void onNotificationsLoaded(List<Notification> notifications);
    }

    interface OnNotificationsDeletedListener {
        void onNotificationsDeleted();
    }
}
