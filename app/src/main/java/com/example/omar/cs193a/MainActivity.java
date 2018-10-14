package com.example.omar.cs193a;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.omar.cs193a.database.ClipsDB;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.ClickListener, ClipboardManager.OnPrimaryClipChangedListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String NOTIF_CHANNEL_ID = "Clips_main_notif_id";
    private static final int NOTIF_ACTION_PENDING_REQUEST = 0;
    private static final int NOTIF_ID = 0;

    private ClipsDB mClipsDB;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private NotificationManager mNotificationManager;
    private ClipboardManager mClipboardManager;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<String> clips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mClipsDB = new ClipsDB(mContext);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(this);
        mRecyclerView = findViewById(R.id.recycler);
        mSwipeRefreshLayout = findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(new NotificationChannel(NOTIF_CHANNEL_ID, "Clips Ongoing Notification Channel", NotificationManager.IMPORTANCE_DEFAULT));
        }


        PendingIntent notifPendingIntent = PendingIntent.getActivity(mContext, NOTIF_ACTION_PENDING_REQUEST, new Intent(mContext, MainActivity.class), Intent.FILL_IN_ACTION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(mContext, NOTIF_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_menu_help)
                .setContentTitle("Clips Moniter")
                .setContentText("Clips is Running")
                .setContentIntent(notifPendingIntent);
        mNotificationManager.notify(NOTIF_ID, mNotificationBuilder.build());

        clips = mClipsDB.getClips();

        myRecyclerAdapter = new MyRecyclerAdapter(mContext, clips);
        myRecyclerAdapter.setListener(this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(myRecyclerAdapter);

    }

    @Override
    public void onItemCLick(int position) {
        Toast.makeText(this, "Clicked : " + clips.get(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongCLick(final int position) {
        Toast.makeText(this, "Long Clicked Clicked : " + clips.get(position), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPrimaryClipChanged() {
        String clip = mClipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        mClipsDB.addClip(clip);
    }


    public void updateRecycler() {
        clips.clear();
        clips.addAll(mClipsDB.getClips());
        myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        updateRecycler();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
