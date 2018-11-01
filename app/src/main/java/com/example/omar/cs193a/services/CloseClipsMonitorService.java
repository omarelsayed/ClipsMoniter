package com.example.omar.cs193a.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CloseClipsMonitorService extends IntentService {
    public static final String ACTION_CLOSE = "close";

    public CloseClipsMonitorService() {
        super("CloseClipsMonitorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("CLOSESERVICE", "ONHANDLEINTENT");
        if (intent.getAction().equals(ACTION_CLOSE)) {
            stopService(new Intent(this, ClipsMonitorService.class));
            stopSelf();
        }
    }
}
