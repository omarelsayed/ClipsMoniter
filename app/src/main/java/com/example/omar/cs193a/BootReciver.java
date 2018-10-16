package com.example.omar.cs193a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ClipsMonitorService.class));
    }

}
