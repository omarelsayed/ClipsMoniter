package com.example.omar.cs193a.activities;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.omar.cs193a.R;
import com.example.omar.cs193a.adapters.MyRecyclerAdapter;
import com.example.omar.cs193a.database.ClipsDB;
import com.example.omar.cs193a.model.Clip;
import com.example.omar.cs193a.services.ClipsMonitorService;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.ClipsRecyclerClickListener, SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private ClipsDB mClipsDB;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Intent ClipsMonitorServiceIntent;
    private CoordinatorLayout mCoordinator;
    private ClipboardManager mClipboardManager;
    private ArrayList<Clip> clips = new ArrayList<>();
    private boolean multiSelect;
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallBacks;

    // TODO -> SearchView
    private SearchView mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        mContext = this;

        actionModeCallBacks = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                multiSelect = true;
                actionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                mode.setTitle(String.valueOf(selectedItems.size()) + " Selected");
                if (selectedItems.isEmpty()) {
                    menu.clear();
                } else if (selectedItems.size() == 1) {
                    getMenuInflater().inflate(R.menu.menu_recycler_actions_single, menu);

                } else {
                    getMenuInflater().inflate(R.menu.menu_recycler_actions_multible, menu);

                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_copy:
                        if (selectedItems.size() > 0) {
                            Clip clip = clips.get(selectedItems.get(0));
                            mClipboardManager.setPrimaryClip(new ClipData("", new String[]{"text/plain"}, new ClipData.Item(clip.getContent())));
                        }
                        showSnack("Copied");
                        break;
                    case R.id.action_delete:
                        for (Integer integer : selectedItems) {
                            Clip clip = clips.get(integer);
                            mClipsDB.removeClip(clip);
                        }
                        showSnack(String.valueOf(selectedItems.size()) + " Clips Deleted");
                        break;
                    case R.id.action_share:
                        shareClip();
                        break;
                }
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                multiSelect = false;
                selectedItems.clear();
                updateRecycler();
            }

            private void shareClip() {
                startActivity(Intent.createChooser(createShareIntent(), "Share Clip "));
            }

            @NonNull
            private Intent createShareIntent() {
                UrlValidator urlValidator = new UrlValidator();
                Intent shareIntent = null;
                String data = clips.get(selectedItems.get(0)).getContent();
                if (urlValidator.isValid(data)) {
                    shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                } else {
                    shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/*");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, clips.get(selectedItems.get(0)).getContent());
                }
                return shareIntent;
            }
        };


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
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(myRecyclerAdapter);

    }

    @Override
    public void onItemCLick(final View view, final int position) {
        selectItem(view, position);
    }

    @Override
    public void onItemLongCLick(View view, int position) {
        startSupportActionMode(actionModeCallBacks);
        selectItem(view, position);
    }

    private void selectItem(View view, Integer position) {
        if (multiSelect) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position);
                view.setBackgroundColor(Color.WHITE);
            } else {
                selectedItems.add(position);
                view.setBackgroundColor(Color.LTGRAY);
            }
            actionMode.invalidate();
        }
    }

    public void updateRecycler() {
        clips.clear();
        clips.addAll(mClipsDB.getClips());
        myRecyclerAdapter.notifyDataSetChanged();
        if (!selectedItems.isEmpty()) {
            actionMode.finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecycler();
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

        // TODO -> SearchView
        mSearchView = new SearchView(mContext);
        menu.findItem(R.id.action_search).setActionView(mSearchView);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(mContext, SearchActivity.class)));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                stopService(ClipsMonitorServiceIntent);
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case R.id.action_change_recycler_layout:
                if (item.getTitle().equals(getString(R.string.list_layout))) {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    item.setTitle(getString(R.string.grid_layout));
                    item.setIcon(R.drawable.ic_grid);
                } else {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
                    item.setTitle(getString(R.string.list_layout));
                    item.setIcon(R.drawable.ic_list);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSnack(String text) {
        Snackbar.make(mCoordinator, text, Snackbar.LENGTH_LONG).show();
    }
}
