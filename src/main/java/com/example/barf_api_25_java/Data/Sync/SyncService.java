package com.example.barf_api_25_java.Data.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SyncService extends Service {
    private static SyncAdapter sSyncAdapter = null;
    private static final Object aSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (aSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
