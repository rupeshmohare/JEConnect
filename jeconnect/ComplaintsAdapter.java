package com.pranavamrute.jeconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amitthakare.jeconnect.Models.ComplaintsModel;
import com.amitthakare.jeconnect.R;

import java.util.List;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.MyViewHolder> {

    Context mContext;
    List<ComplaintsModel> mData;
    OnRecyclerClickListener recyclerClickListener;

    public ComplaintsAdapter(Context mContext, List<ComplaintsModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public void setOnRecyclerClickListener(OnRecyclerClickListener recyclerClickListener) {
        this.recyclerClickListener = recyclerClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_complaints,parent,false);

        return new MyViewHolder(itemView, recyclerClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(mData.get(position).getTitle());
        holder.date.setText(mData.get(position).getDate());
        holder.status.setText(mData.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnRecyclerClickListener {
        void onRecyclerItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView date;
        private TextView status;

        public MyViewHolder(@NonNull View itemView, OnRecyclerClickListener recyclerClickListener) {
            super(itemView);

            title = itemView.findViewById(R.id.titleComplaintsLayout);
            date = itemView.findViewById(R.id.dateComplaintsLayout);
            status = itemView.findViewById(R.id.statusComplaintsLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerClickListener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            recyclerClickListener.onRecyclerItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
