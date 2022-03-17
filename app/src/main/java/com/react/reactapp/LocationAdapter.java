package com.react.reactapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>{

    private HashMap<String, HashMap<String, String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    LocationAdapter(Context context, HashMap<String, HashMap<String, String>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String key = (String) (mData.keySet().toArray())[position];
        holder.cardView.setTag(key);
        holder.time.setText(mData.get(key).get("time"));
        holder.location.setText("You have entered the " + mData.get(key).get("estName") + " " + mData.get(key).get("branch"));
    }

    @Override
    public int getItemCount() {
        Log.d("LocAdapter>", mData.keySet().toString());
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView time, location;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.itmLoc_txtTime);
            location = itemView.findViewById(R.id.itmLoc_txtLocation);
            cardView = itemView.findViewById(R.id.itmLoc_cardMain);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
