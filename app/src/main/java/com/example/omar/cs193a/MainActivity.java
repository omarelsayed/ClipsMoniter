package com.example.omar.cs193a;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerAdapter.ClickListener {
    RecyclerView mRecyclerView;
    MyRecyclerAdapter myRecyclerAdapter;

    public static ArrayList<String> words = new ArrayList<String>() {{
        add("English");
        add("Arabic");
        add("Italian");
        add("English");
        add("Arabic");
        add("Italian");
        add("English");
        add("Arabic");
        add("Italian");
    }};
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        myRecyclerAdapter = new MyRecyclerAdapter(this, words);
        myRecyclerAdapter.setListener(this);

        mRecyclerView = findViewById(R.id.recycler);
        /*mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));*/
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(myRecyclerAdapter);

    }

    @Override
    public void onItemCLick(int position) {
        Toast.makeText(this, "Clicked : " + words.get(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongCLick(final int position) {
        Toast.makeText(this, "LongClicked : " + words.get(position), Toast.LENGTH_SHORT).show();
        PopupMenu popupMenu = new PopupMenu(this, mRecyclerView.getChildAt(position));
        popupMenu.inflate(R.menu.recycler_pop);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        words.remove(position);
                        myRecyclerAdapter.notifyItemRemoved(position);
                        myRecyclerAdapter.notifyItemRangeChanged(position, words.size(), null);
                        Toast.makeText(MainActivity.this, "POPED", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_edit:
                        // TODO
                        break;
                }
                return true;
            }
        });


    }
}
