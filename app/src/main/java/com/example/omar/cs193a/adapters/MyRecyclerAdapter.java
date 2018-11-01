package com.example.omar.cs193a.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omar.cs193a.R;
import com.example.omar.cs193a.model.Clip;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private List<Clip> data;
    private ClipsRecyclerClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView clip_card;
        TextView clip_date;
        TextView clip_content;
        /*Button clip_more;*/

        public MyViewHolder(View itemView) {
            super(itemView);
            clip_card = itemView.findViewById(R.id.clip_card);
            clip_date = itemView.findViewById(R.id.clip_date);
            clip_content = itemView.findViewById(R.id.clip_content);
            /*clip_more = itemView.findViewById(R.id.clip_more);*/
        }
    }

    public MyRecyclerAdapter(Context ctx, List<Clip> data) {
        mContext = ctx;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.itemView.setBackgroundColor(Color.WHITE);

        Clip clip = data.get(position);

        holder.clip_date.setText(clip.getDate());
        holder.clip_content.setText(clip.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemCLick(holder.itemView, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongCLick(holder.itemView, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setListener(ClipsRecyclerClickListener listener) {
        this.listener = listener;
    }

    public interface ClipsRecyclerClickListener {
        void onItemCLick(View view, int position);

        void onItemLongCLick(View view, int position);
    }

}
