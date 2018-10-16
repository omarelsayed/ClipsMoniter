package com.example.omar.cs193a;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.omar.cs193a.database.ClipsDB;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.ClickListener, /*ClipboardManager.OnPrimaryClipChangedListener,*/ SwipeRefreshLayout.OnRefreshListener {
    private static final String NOTIF_CHANNEL_ID = "Clips_main_notif_id";
    private static final int NOTIF_ACTION_PENDING_REQUEST = 0;
    private static final int NOTIF_ID = 1995;

    private ClipsDB mClipsDB;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private NotificationManager mNotificationManager;
    private ClipboardManager mClipboardManager;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Clip> clips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        startService(new Intent(this, ClipsMonitorService.class));

        mClipsDB = new ClipsDB(mContext);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mClipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        mRecyclerView = findViewById(R.id.recycler);

        mSwipeRefreshLayout = findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        clips = mClipsDB.getClips();

        myRecyclerAdapter = new MyRecyclerAdapter(mContext, clips);
        myRecyclerAdapter.setListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(myRecyclerAdapter);

    }


    @Override
    public void onItemLongCLick(final int position) {
        Clip clip = clips.get(position);
        mClipboardManager.setPrimaryClip(new ClipData("", new String[]{"text/plain"}, new ClipData.Item(clip.getContent())));
        Toast.makeText(this, "Clip Copied", Toast.LENGTH_SHORT).show();
    }

    public void updateRecycler() {
        clips.clear();
        clips.addAll(mClipsDB.getClips());
        myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        // TODO Update Recycler
        updateRecycler();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
