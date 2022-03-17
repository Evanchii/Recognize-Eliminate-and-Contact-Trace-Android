package com.react.reactapp;


import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private HashMap<String, HashMap<String, String>> mData;
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time, date, message;
        private ImageView iv;

        public ViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.itmNotif_txtTime);
            date = view.findViewById(R.id.itmNotif_txtDate);
            message = view.findViewById(R.id.itmNotif_txtMessage);
            iv = view.findViewById(R.id.itmNotif_imgIcon);
        }
    }

    public NotificationsAdapter(Context context, HashMap<String, HashMap<String, String>> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_notification, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String key = (String) (mData.keySet().toArray())[position];
        Log.d("NAdap(60)", key);
        Log.d("NAdap(61)", (String) (mData.get(key).keySet().toArray())[0]);

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Integer.parseInt(key) * 1000L);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        String time = DateFormat.format("h:m:s a", cal).toString();

        viewHolder.time.setText(time);
        viewHolder.date.setText(date);
        viewHolder.message.setText(mData.get(key).get("message"));
        switch (mData.get(key).get("type")) {
            case "health-alert":
                viewHolder.iv.setImageResource(R.drawable.ic_health_alert);
                break;
            case "app-status":
                viewHolder.iv.setImageResource(R.drawable.ic_app_status);
                break;
            case "health-status":
                viewHolder.iv.setImageResource(R.drawable.ic_health_status);
                break;
            default:
                viewHolder.iv.setImageResource(R.drawable.ic_generic);
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}

