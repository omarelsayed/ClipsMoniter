package com.example.omar.cs193a.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omar.cs193a.R;

import java.util.ArrayList;

public class SearchResultsRecyclerAdaptor extends RecyclerView.Adapter<SearchResultsRecyclerAdaptor.SearchResultViewHolder> {


    private final Context mContext;
    private final ArrayList<String> data;

    public SearchResultsRecyclerAdaptor(Context mContext, ArrayList<String> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SearchResultViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_row, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.result.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {

        private final TextView result;

        SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.item_view_result);
        }
    }
}
