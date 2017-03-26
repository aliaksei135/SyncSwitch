package com.aliakseipilko.syncswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class BootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean startonBoot = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("startOnBoot", false);
        if(startonBoot) {
            Intent i = new Intent(context, SyncSwitchService.class);
            context.startService(i);
        }
    }
}
