package com.example.textguard2;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 123;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS
    };

    private int currentPermissionIndex = 0;
    private Map<Integer, Fragment> fragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.navigation_home, new HomeFragment());
        fragmentMap.put(R.id.navigation_dashboard, new DashboardFragment());
        fragmentMap.put(R.id.navigation_warning, new NotificationsFragment());
        fragmentMap.put(R.id.navigation_debug, new DebugFragment());
        fragmentMap.put(R.id.navigation_contacts, new SelectContactsFragment());

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = fragmentMap.get(item.getItemId());
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    updateIcons(item.getItemId());
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home); // Imposta manualmente l'icona selezionata
            updateIcons(R.id.navigation_home); // Aggiorna le icone
        }

        checkAndRequestPermissions();

        if (isDeviceRooted()) {
            executeRootCommand("echo Hello, World!");
        } else {
            Log.e(TAG, "Il dispositivo non è rootato o l'accesso root è stato negato.");
        }
    }

    private void updateIcons(int selectedItemId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        MenuItem contactsItem = bottomNavigationView.getMenu().findItem(R.id.navigation_contacts);
        MenuItem notificationsItem = bottomNavigationView.getMenu().findItem(R.id.navigation_warning);
        MenuItem homeItem = bottomNavigationView.getMenu().findItem(R.id.navigation_home);
        MenuItem dashboardItem = bottomNavigationView.getMenu().findItem(R.id.navigation_dashboard);

        contactsItem.setIcon(selectedItemId == R.id.navigation_contacts ? R.drawable.ic_contacts_selected : R.drawable.ic_contacts);
        notificationsItem.setIcon(selectedItemId == R.id.navigation_warning ? R.drawable.ic_warning_selected : R.drawable.ic_warning);
        homeItem.setIcon(selectedItemId == R.id.navigation_home ? R.drawable.ic_home_selected : R.drawable.ic_home);
        dashboardItem.setIcon(selectedItemId == R.id.navigation_dashboard ? R.drawable.ic_dashboard_selected : R.drawable.ic_dashboard);
    }

    private boolean isDeviceRooted() {
        String[] paths = {"/system/xbin/su", "/system/bin/su", "/sbin/su", "/system/su", "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new java.io.File(path).exists()) return true;
        }
        return false;
    }

    private void executeRootCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            outputStream.writeBytes(command + "\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();

            int exitValue = process.waitFor();
            if (exitValue == 0) {
                Log.i(TAG, "Comando eseguito con successo");
                String line;
                while ((line = inputStream.readLine()) != null) {
                    Log.i(TAG, line);
                }
            } else {
                Log.e(TAG, "Errore nell'esecuzione del comando");
                String line;
                while ((line = errorStream.readLine()) != null) {
                    Log.e(TAG, line);
                }
            }

            outputStream.close();
            inputStream.close();
            errorStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Errore durante l'esecuzione del comando root", e);
        }
    }

    private void checkAndRequestPermissions() {
        if (currentPermissionIndex < PERMISSIONS.length) {
            String currentPermission = PERMISSIONS[currentPermissionIndex];
            if (ContextCompat.checkSelfPermission(this, currentPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{currentPermission}, PERMISSIONS_REQUEST_CODE);
            } else {
                currentPermissionIndex++;
                checkAndRequestPermissions();
            }
        } else {
            checkPermission(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentPermissionIndex++;
                checkAndRequestPermissions();
            } else {
                Toast.makeText(this, "Permission denied for " + permissions[0], Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNotificationServiceEnabled() {
        ComponentName cn = new ComponentName(this, NotificationListener.class);
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (flat != null) {
            return flat.contains(cn.flattenToString());
        }
        return false;
    }

    private AlertDialog buildNotificationServiceAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permesso Richiesto");
        builder.setMessage("Quest'app ha bisogno dei permessi di accesso alle notifiche per poter funzionare. Per favore abilitali nelle impostazioni.");
        builder.setPositiveButton("Vai alle Impostazioni", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        });
        builder.setNegativeButton("Annulla", (dialog, which) -> {
            // Handle cancel action if needed
        });
        return builder.create();
    }

    public void checkPermission(View view) {
        if (!isNotificationServiceEnabled()) {
            Log.d(TAG, "Notification service is not enabled, prompting user to enable");
            buildNotificationServiceAlertDialog().show();
        } else {
            Log.d(TAG, "Notification permission already granted");
            System.out.println("Notification permission already granted.");
        }
    }
}
