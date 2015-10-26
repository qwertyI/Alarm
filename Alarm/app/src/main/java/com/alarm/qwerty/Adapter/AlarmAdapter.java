package com.alarm.qwerty.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.alarm.qwerty.Activity.AlarmActivity.Alarm;
import com.alarm.qwerty.R;

import java.util.List;

/**
 * Created by wei on 2015/10/26.
 */
public class AlarmAdapter extends ArrayAdapter<Alarm> {

    private int resourceId;

    public AlarmAdapter(Context context, int id, List<Alarm> object){
        super(context, id, object);
        resourceId = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group){
        Alarm alarm = getItem(position);
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.alarm_item_tv);
            viewHolder.isOpen = (Switch) view.findViewById(R.id.alarm_switch_btn);
            view.setTag(viewHolder);
        }else {
            view.getTag();
        }
        viewHolder.time.setText(String.format("%s:%s", alarm.GetHour(), alarm.GetMinute()));
        viewHolder.isOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.i("path", "打开");
                }else {
                    Log.i("path", "关闭");
                }
            }
        });
        return view;
    }

    class ViewHolder{
        TextView time;
        Switch isOpen;
    }

}
