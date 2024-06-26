package com.example.textguard2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            Bundle bundle = intent.getExtras();
            String state = null;
            if (bundle != null) {
                state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                    String phoneNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    if (phoneNumber != null){
                        Log.i("IncomingCallReceiver", "Incoming call from: " + phoneNumber);
                    }
                }
            } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                Log.i("CALL_RECORDING", "Fuori dalla registrazione");
            }
        }
    }
}
