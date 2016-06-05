package com.example.jatin.wi_bird;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Trapti mittal on 04-Jun-16.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorAdapter extends ArrayAdapter<SensorItem> {

    private final Activity context;
    private SensorItem[] sensorItem;
    private int resource;

    public SensorAdapter(Activity context, int resource, SensorItem[] sensorItem) {
        super(context,resource, sensorItem);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.sensorItem = sensorItem;
        this.resource = resource;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(resource, null,true);

        TextView sen_name = (TextView) rowView.findViewById(R.id.sensor_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.thumbnail);
        TextView sen_value = (TextView) rowView.findViewById(R.id.sensor_val);

        sen_name.setText(sensorItem[position].sensor_name);
        imageView.setImageResource(sensorItem[position].imageView);
        sen_value.setText(sensorItem[position].sensor_value);
        return rowView;

    };
}