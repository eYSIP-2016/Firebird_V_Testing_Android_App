package com.example.jatin.wi_bird;



/**
 * Created by Trapti mittal on 04-Jun-16.
 */
public class SensorItem {
    public int imageView;
    public String sensor_name;
    public String sensor_value;

    public SensorItem(String sensor_name, String sensor_value, int imageView) {
        this.sensor_name = sensor_name;
        this.sensor_value = sensor_value;
        this.imageView = imageView;
    }
}
