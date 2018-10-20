package com.example.omar.cs193a;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.example.omar.cs193a.database.ClipsDB;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private ClipsDB mClipsDB;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Intent ClipsMonitorServiceIntent;
    private CoordinatorLayout mCoordinator;
    private ClipboardManager mClipboardManager;
    private Switch mSwitch;
    boolean enabled;

    private ArrayList<Clip> clips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        mContext = this;


        ClipsMonitorServiceIntent = new Intent(this, ClipsMonitorService.class);
        startService(ClipsMonitorServiceIntent);


        mClipsDB = new ClipsDB(mContext);

        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mRecyclerView = findViewById(R.id.recycler);
        mCoordinator = findViewById(R.id.main_coordinator);

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
                        if (mClipsDB.removeClip(clip)) {
                            showSnack("Clip Deleted");
                            updateRecycler();
                        }

                        break;

                    case R.id.action_copy:
                        mClipboardManager.setPrimaryClip(new ClipData("", new String[]{"text/plain"}, new ClipData.Item(clip.getContent())));
                        showSnack("Clip Copied");
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

    public void showSnack(String text) {
        Snackbar.make(mCoordinator, text, Snackbar.LENGTH_LONG).show();
    }
}
