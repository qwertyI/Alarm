package com.alarm.qwerty.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.alarm.qwerty.Activity.AlarmActivity.Alarm;
import com.alarm.qwerty.R;

import java.util.List;

/**
 * Created by qwerty on 2015/10/26.
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
//            viewHolder.delete = (Button) view.findViewById(R.id.alarm_delete_btn);
            view.setTag(viewHolder);
        }else {
            view.getTag();
        }
        viewHolder.time.setText(alarm.getTime());
//        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return view;
    }

    class ViewHolder{
        TextView time;
//        Button delete;
    }

}
