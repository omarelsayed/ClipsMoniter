package com.example.omar.cs193a;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.omar.cs193a.database.ClipsDB;


public class ClipsMonitorService extends IntentService implements ClipboardManager.OnPrimaryClipChangedListener {

    private static final String NOTIF_CHANNEL_ID = "Clips_main_notif_id";
    private static final int NOTIF_ACTION_PENDING_REQUEST = 0;
    private static final int ONGOING_NOTIF_ID = 1995;

    private ClipsDB mClipsDB;
    private NotificationManager mNotificationManager;
    private ClipboardManager mClipboardManager;


    public ClipsMonitorService() {
        super("ClipsMonitorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mClipsDB = new ClipsDB(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(new NotificationChannel(NOTIF_CHANNEL_ID, "Clips Ongoing Notification Channel", NotificationManager.IMPORTANCE_DEFAULT));

        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIF_ACTION_PENDING_REQUEST, new Intent(this, MainActivity.class), Intent.FILL_IN_ACTION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_menu_help)
                .setContentTitle("Clips Moniter")
                .setContentText("Clips is Running")
                .setContentIntent(pendingIntent);

        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (mClipboardManager != null) {
            mClipboardManager.addPrimaryClipChangedListener(this);
        }

        startForeground(ONGOING_NOTIF_ID, mNotificationBuilder.build());


    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


    @Override
    public void onPrimaryClipChanged() {
        ClipData clipData = mClipboardManager.getPrimaryClip();
        String clip = null;
        if (clipData != null) {
            clip = clipData.getItemAt(0).getText().toString();
        }
        mClipsDB.addClip(clip);
    }
}