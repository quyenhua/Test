package com.myproject.huutam.test;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Quyen Hua on 5/8/2017.
 */

public class CustomGridMission extends ArrayAdapter {
    private Context context;
    private int layout;
    List<MissionItem> missionItemList;

    public CustomGridMission(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.layout = resource;
        this.missionItemList = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            view = new View(context);
            view = inflater.inflate(layout, null);
            TextView tvLevel = (TextView) view.findViewById(R.id.tvLevel);
            ImageView imgStar = (ImageView) view.findViewById(R.id.imgStar);
            RelativeLayout mission = (RelativeLayout) view.findViewById(R.id.relMissionItem);
            tvLevel.setText(missionItemList.get(position).getLevel() + "");
            imgStar.setImageResource(missionItemList.get(position).getImageStar());
            mission.setBackgroundResource(missionItemList.get(position).getBackground());
        }
        else{
            view = convertView;
        }
        return view;
    }
}
