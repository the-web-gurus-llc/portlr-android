package com.example.nomanahmed.portlr.Adapters;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nomanahmed.portlr.Activities.RegistrationListActivity;
import com.example.nomanahmed.portlr.Activities.RegistrationTimeActivity;
import com.example.nomanahmed.portlr.Model.TimeLog;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;

import java.util.ArrayList;

import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_TIMELOG;
import static com.example.nomanahmed.portlr.Utils.Global.selected_time_log;

public class TimeListItemCustomListAdapter extends BaseAdapter {
    private ArrayList<TimeLog> listData;
    private LayoutInflater layoutInflater;

    private RegistrationListActivity activity;

    public TimeListItemCustomListAdapter(Context aContext, ArrayList<TimeLog> listData) {
        this.listData = listData;
        activity = (RegistrationListActivity) aContext;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.time_list_row, null);
            holder = new ViewHolder();
            holder.projectName = (TextView) v.findViewById(R.id.projectNameTV);
            holder.taskName = (TextView) v.findViewById(R.id.taskNameTV);
            holder.hoursTV = (TextView) v.findViewById(R.id.hoursTV);
            holder.editBtn = v.findViewById(R.id.editBtn);

            holder.editBtn.setTag(position);
            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (Integer) view.getTag();
                    TimeLog log = listData.get(pos);
                    Intent intent = new Intent(activity, RegistrationTimeActivity.class);
                    selected_time_log = log;
                    activity.startActivity(intent);
                }
            });

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.projectName.setText(listData.get(position).projectName);
        holder.taskName.setText(listData.get(position).taskName);
        String tempHour = DateTimeUtils.getLocalTimeFromServer(listData.get(position).hours);
        holder.hoursTV.setText(tempHour);

        return v;
    }
    static class ViewHolder {
        TextView projectName;
        TextView taskName;
        TextView hoursTV;
        ImageView editBtn;
    }
}