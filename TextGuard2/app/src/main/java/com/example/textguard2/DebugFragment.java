package com.example.textguard2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DebugFragment extends Fragment {
    private static final String TAG = "Debug";
    private static final int PERMISSIONS_REQUEST_CODE = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debug, container, false);



        Button sendLogsButton = view.findViewById(R.id.button_send_logs);
        sendLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogs();
            }
        });

        Button btnReadSms = view.findViewById(R.id.btn_read_sms);
        btnReadSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_CODE);
                } else {
                    readSmsMessages();
                }
            }
        });

        Button btnReadCallLog = view.findViewById(R.id.btn_read_call_log);
        btnReadCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_CODE);
                } else {
                    readCallLog();
                }
            }
        });

        Button btnReadContacts = view.findViewById(R.id.btn_read_contacts);
        btnReadContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_CODE);
                } else {
                    getContacts();
                }
            }
        });

        return view;
    }

    private void readSmsMessages() {
        String TAG = "SMSReader";
        Uri inboxUri = Telephony.Sms.Inbox.CONTENT_URI;
        String[] projection = new String[]{
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
        };

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Cursor cursor = getActivity().getContentResolver().query(inboxUri, projection, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(Telephony.Sms._ID);
            int addressIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);
            int dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE);

            while (cursor.moveToNext()) {
                String id = cursor.getString(idIndex);
                String address = cursor.getString(addressIndex);
                String body = cursor.getString(bodyIndex);
                long dateMillis = cursor.getLong(dateIndex);
                String date = dateFormat.format(new Date(dateMillis));

                Log.i(TAG, "SMS ID: " + id + ", From: " + address + ", Content: " + body + ", Date: " + date);
            }
            cursor.close();
        } else {
            Log.d(TAG, "No SMS messages found.");
        }
    }

    private void readCallLog() {
        String TAG = "CallListener";
        Set<String> processedCallIds = new HashSet<>();

        Cursor managedCursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (managedCursor != null) {
            int idIndex = managedCursor.getColumnIndex(CallLog.Calls._ID);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {
                String callId = managedCursor.getString(idIndex);
                if (!processedCallIds.contains(callId)) {
                    processedCallIds.add(callId);

                    String phoneNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dirCode = Integer.parseInt(callType);

                    switch (dirCode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            Log.i(TAG, "To: " + phoneNumber +  ", Date: " + callDayTime + ", Duration: " + callDuration + " sec");
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            Log.i(TAG, "From: " + phoneNumber +  ", Date: " + callDayTime + ", Duration: " + callDuration + " sec");
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            Log.i(TAG, "Missed From: " + phoneNumber +  ", Date: " + callDayTime + ", Duration: " + callDuration + " sec");
                            break;

                        case CallLog.Calls.REJECTED_TYPE:
                            dir = "REJECTED";
                            Log.i(TAG, "REJECTED From: " + phoneNumber +  ", Date: " + callDayTime + ", Duration: " + callDuration + " sec");
                            break;
                    }
                }
            }
            managedCursor.close();
        }
    }

    private void getContacts() {
        List<String> contacts = new ArrayList<>();

        // Usa un ContentResolver per interrogare i contatti
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(name + ": " + number);
            }
            cursor.close();
        }

        // Per esempio, logga i contatti ottenuti
        for (String contact : contacts) {
            Log.i("Contact", contact);
        }
    }

    private void sendLogs() {
        // Crea alcuni log di esempio
        List<LogEntry> logEntries = new ArrayList<>();
        logEntries.add(new LogEntry("2024-06-19T14:56:00Z", "ExampleApp", "Error", "An example error occurred", "High"));
        logEntries.add(new LogEntry("2024-06-19T15:00:00Z", "ExampleApp", "Info", "This is an informational message", "Low"));

        Map<String, List<LogEntry>> logsMap = new HashMap<>();
        logsMap.put("example_logs", logEntries);

        Logs logs = new Logs(logsMap);

        // Invia i log al server
        LogSender logSender = new LogSender();
        logSender.sendLogs(logs);
    }


}
