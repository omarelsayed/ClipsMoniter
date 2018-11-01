package com.example.omar.cs193a.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.omar.cs193a.R;
import com.example.omar.cs193a.activities.MainActivity;
import com.example.omar.cs193a.database.ClipsDB;
import com.example.omar.cs193a.model.Clip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ClipsMonitorService extends IntentService implements ClipboardManager.OnPrimaryClipChangedListener {

    private static final String NOTIF_CHANNEL_ID = "Clips_main_notif_id";
    private static final int NOTIF_ACTION_PENDING_REQUEST = 0;
    private static final int ONGOING_NOTIF_ID = 1995;
    private static final int CLOSE_SERVICE_PENDING_REQ = 1;

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
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(new NotificationChannel(NOTIF_CHANNEL_ID, "Clips Ongoing Notification Channel", NotificationManager.IMPORTANCE_DEFAULT));

        }
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, NOTIF_ACTION_PENDING_REQUEST, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeServiceIntent = new Intent(this, CloseClipsMonitorService.class);
        closeServiceIntent.setAction(CloseClipsMonitorService.ACTION_CLOSE);
        PendingIntent closeServicePendingIntent = PendingIntent.getService(this, CLOSE_SERVICE_PENDING_REQ, closeServiceIntent, 0);


        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_copy)
                .setContentTitle("Clipboard Monitor")
                .setContentText("Monitor is Running")
                .setContentIntent(mainActivityPendingIntent)
                .addAction(R.drawable.ic_exit_dark, "Close", closeServicePendingIntent);

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
    public void onDestroy() {
        mClipboardManager.removePrimaryClipChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onPrimaryClipChanged() {
        String clipContent;
        String clipDate;
        Clip newClip = null;

        ClipData clipData = mClipboardManager.getPrimaryClip();
        if (clipData != null) {
            clipContent = clipData.getItemAt(0).getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a, d MMM yyyy", Locale.getDefault());
            clipDate = dateFormat.format(Calendar.getInstance().getTime());
            newClip = new Clip(clipContent, clipDate);
        }
        mClipsDB.addClip(newClip);
    }


}
