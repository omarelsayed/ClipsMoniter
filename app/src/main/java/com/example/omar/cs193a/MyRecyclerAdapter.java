package com.example.omar.cs193a;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private List<Clip> data;
    private ClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView clip_card;
        TextView clip_date;
        TextView clip_content;

        public MyViewHolder(View itemView) {
            super(itemView);
            clip_card = itemView.findViewById(R.id.clip_card);
            clip_date = itemView.findViewById(R.id.clip_date);
            clip_content = itemView.findViewById(R.id.clip_content);
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
        Clip clip = data.get(position);

        holder.clip_date.setText(clip.getDate());
        holder.clip_content.setText(clip.getContent());

        holder.clip_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onItemLongCLick(position);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    interface ClickListener {
        void onItemLongCLick(int position);
    }
}
