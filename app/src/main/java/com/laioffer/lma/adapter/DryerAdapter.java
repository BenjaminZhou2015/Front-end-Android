package com.laioffer.lma.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.laioffer.lma.R;
import com.laioffer.lma.model.Machine;
import com.laioffer.lma.model.User;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DryerAdapter extends RecyclerView.Adapter<DryerAdapter.ViewHolder> {
    private List<Machine> dryers;
    User user;
    private Context context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public TextView endTime;
        public ImageView icon;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            txtHeader = (TextView) v.findViewById(R.id.dryer_sn);
            txtFooter = (TextView) v.findViewById(R.id.dryer_status);
            icon = (ImageView)v.findViewById(R.id.icon_dryer);
            endTime = (TextView) v.findViewById(R.id.dryer_time);
        }
    }



    // Provide a suitable constructor (depends on the kind of dataset)
    public DryerAdapter(List<Machine> dryers) {
        this.dryers = dryers;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DryerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.dryer_list_item, parent, false);
        DryerAdapter.ViewHolder vh = new DryerAdapter.ViewHolder(v);
        context = parent.getContext();
        user = User.getInstance(context);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DryerAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Machine dryer = dryers.get(position);

        final String name = dryer.getSN();
        String cur_status = null;
        if (dryer.getIsAvailable() == "true") {
            cur_status = "Available";
        } else if (dryer.getUserID().equals(user.getId())) {
            cur_status = "In use";
        } else {//reversed machines
            cur_status = "Reserved";
        }
        final String status = cur_status;
        String estimated_endTime = null;
        switch (status) {
            case "Available" :
                estimated_endTime = "";
                break;
            case "In use" :
                if(dryer.getUserID().equals(user.getId())) {
                    holder.txtFooter.setTextColor(Color.parseColor("#FF7F50"));
                    holder.icon.setImageResource(R.drawable.using_ic_dryer);
                }
                estimated_endTime = getEndTime(dryer.getStartTime(), user.getLocation().getDefaultRunningTime()); //after finished, calculate the pick up time //warning
                break;
            case "Reserved":
                if(dryer.getUserID().equals(user.getId())) {
                    holder.txtFooter.setTextColor(Color.parseColor("#FF7F50"));
                    holder.icon.setImageResource(R.drawable.using_ic_dryer);
                }
                estimated_endTime = getEndTime(dryer.getStartTime(), user.getLocation().getDefaultRunningTime() + user.getLocation().getDefaultPickupTime()); //location.defaultRunningTime - helper.millisToMinutes(Date.now() - dryer.startTime) + location.defaultPickupTime;
                break;
        }
        final String endTime = estimated_endTime;

        holder.txtHeader.setText("ID: " + name);
        holder.txtFooter.setText(status);
        holder.endTime.setText(endTime);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dryers.size();
    }


    private String getEndTime(String startTime, int time) {
        Log.d("washer" , "dryer start time" + startTime);
        Log.d("washer" , "minutes" + time);
        OffsetDateTime odt = OffsetDateTime.parse(startTime);
        OffsetDateTime end_odt = odt.plusMinutes(time);
        Log.d("washer" , "output is " + end_odt.getHour() + ":" + end_odt.getMinute());
        return end_odt.getHour() + ":" + end_odt.getMinute();
    }

}
