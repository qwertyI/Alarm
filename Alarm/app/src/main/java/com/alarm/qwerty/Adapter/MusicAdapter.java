package com.alarm.qwerty.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.alarm.qwerty.Activity.MusicActivity.MusicName;
import com.alarm.qwerty.R;
import java.util.List;

/**
 * Created by wei on 2015/10/23.
 */
public class MusicAdapter extends ArrayAdapter<MusicName> {

    private int resourceId;

    public MusicAdapter(Context context, int resource, List<MusicName> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group){
        MusicName musicName = getItem(position);
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.MusicName = (TextView) view.findViewById(R.id.music_name_item);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.MusicName.setText(musicName.getMusicName());
        return view;
    }

    class ViewHolder{
        TextView MusicName;
    }
}
