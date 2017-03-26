package com.aliakseipilko.syncswitch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncSwitchService extends Service {
    public SyncSwitchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
