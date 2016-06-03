package com.example.jatin.wi_bird;

import android.app.Application;

/**
 * Created by Jatin mittal on 03-Jun-16.
 */
public class MyApplication extends Application {

    public boolean earlyBluetoothState = false;
    public boolean earlyWifiState = false;

    public boolean getEarlyBluetoothState() {
        return earlyBluetoothState;
    }

    public void setEarlyBluetoothState(boolean state) {
        earlyBluetoothState = state;
    }

    public boolean getEarlyWifiState() {
        return earlyWifiState;
    }

    public void setEarlyWifiState(boolean state) {
        earlyWifiState = state;
    }



}
