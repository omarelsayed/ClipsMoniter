package com.example.omar.cs193a.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.omar.cs193a.R;
import com.example.omar.cs193a.adapters.SearchResultsRecyclerAdaptor;

import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SearchResultsRecyclerAdaptor mSearchResultsRecyclerAdaptor;
    private Context mContext;
    private  ArrayList<String> data = new ArrayList<String>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        Collections.fill(data, query);
        mRecyclerView.setAdapter(new SearchResultsRecyclerAdaptor(mContext, data));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }
}

