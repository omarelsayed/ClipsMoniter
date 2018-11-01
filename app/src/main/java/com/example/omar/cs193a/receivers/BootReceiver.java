package com.example.omar.cs193a.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.omar.cs193a.services.ClipsMonitorService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ClipsMonitorService.class));
    }

}
