package com.example.omar.cs193a;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.omar.cs193a.database.ClipsDB;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {
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
    private Intent ClipsMonitorServiceIntent;

    private ArrayList<Clip> clips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        ClipsMonitorServiceIntent = new Intent(this, ClipsMonitorService.class);
        startService(ClipsMonitorServiceIntent);

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
        /*mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));*/
        mRecyclerView.setAdapter(myRecyclerAdapter);

    }

    @Override
    public void onItemCLick(final View view, final int position) {
        final Clip clip = clips.get(position);

        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.recycler_pop);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        if(mClipsDB.removeClip(clip)) {
                            updateRecycler();
                            Toast.makeText(mContext, "DELETE", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.action_copy:
                        mClipboardManager.setPrimaryClip(new ClipData("", new String[]{"text/plain"}, new ClipData.Item(clip.getContent())));
                        Toast.makeText(mContext, "Clip Copied", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();

    }

    @Override
    public void onItemLongCLick(View view, int position) {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                stopService(ClipsMonitorServiceIntent);
                finish();
        }
        return true;
    }
}
