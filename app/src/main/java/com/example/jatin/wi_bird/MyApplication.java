/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: MyApplication.java
 * Functions: getEarlyBluetoothState(), setEarlyBluetoothState(boolean), setIsControlActivityLaunchedFirst(boolean),
 *            getIsControlActivityLaunchedFirst()
 *Global Variables: earlyBluetoothState, isControlActivityLaunchedFirst
 *
 */
package com.example.jatin.wi_bird;

import android.app.Application;
/*
 * Class Name: MyApplication
 * Logic: This application stores the different states for entire application
 * Example Call: ((MyApplication) this.getApplication()).getIsControlActivityLaunchedFirst()
 */
public class MyApplication extends Application {

    public boolean earlyBluetoothState = false;
    public boolean isControlActivityLaunchedFirst = true;

    public boolean getEarlyBluetoothState() {
        return earlyBluetoothState;
    }

    public void setEarlyBluetoothState(boolean state) {
        earlyBluetoothState = state;
    }

    public void setIsControlActivityLaunchedFirst(boolean isFirst) {isControlActivityLaunchedFirst = isFirst; }

    public boolean getIsControlActivityLaunchedFirst() {return isControlActivityLaunchedFirst; }



}
