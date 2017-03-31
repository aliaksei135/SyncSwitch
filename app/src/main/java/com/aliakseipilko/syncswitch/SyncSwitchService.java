package com.aliakseipilko.syncswitch;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class SyncSwitchService extends Service {

    private static final String TAG = "SyncSwitchService";

    private SharedPreferences prefs;

    private Handler handler;
    private Runnable onTask;
    private Runnable offTask;

    public SyncSwitchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isEnabled = prefs.getBoolean("isEnabled", true);
        if (!isEnabled) {
            stopSelf();
        } else {
            Log.d(TAG, "Started Service");

            final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            final AccountManager accountManager = AccountManager.get(this);

            handler = new Handler(getMainLooper());
            offTask = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Running offTask");

                    //Default sync duration of 5 mins
                    long syncDuration = prefs.getLong("syncDuration", 300000);

                    //Disable wifi, assuming fallback to mobile data
                    wifiManager.setWifiEnabled(false);

                    //Request System to sync all accounts
                    final String AUTHORITY = "com.example.android.datasync.provider";
                    Account[] accounts = accountManager.getAccounts();
                    ContentResolver.setMasterSyncAutomatically(true);
                    for (Account account : accounts) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                        ContentResolver.requestSync(account, AUTHORITY, bundle);
                    }

                    handler.postDelayed(onTask, syncDuration);
                }
            };
            onTask = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Running onTask");

                    //Default sync interval of 30 mins
                    long syncInterval = prefs.getLong("syncInterval", 1800000);

                    // Re enable wifi
                    wifiManager.setWifiEnabled(true);

                    handler.postDelayed(offTask, syncInterval);
                }
            };
            handler.post(onTask);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service stopped");

        // Clean up thread handler
        handler.removeCallbacksAndMessages(null);
    }
}
