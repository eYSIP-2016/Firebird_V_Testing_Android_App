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
    public boolean isVelocityActivityLaunchedFirst = true;
    public int leftSeekbarValue = 255;
    public int rightSeekbarValue = 255;


    public boolean getEarlyBluetoothState() {
        return earlyBluetoothState;
    }

    public void setEarlyBluetoothState(boolean state) {
        earlyBluetoothState = state;
    }

    public void setIsControlActivityLaunchedFirst(boolean isFirst) {isControlActivityLaunchedFirst = isFirst; }

    public boolean getIsControlActivityLaunchedFirst() {return isControlActivityLaunchedFirst; }

    public void setIsVelocityActivityLaunchedFirst(boolean isFirst) {isVelocityActivityLaunchedFirst = isFirst; }

    public boolean getIsVelocityActivityLaunchedFirst() {return isVelocityActivityLaunchedFirst; }

    public void setLeftSeekbarValue(int value){leftSeekbarValue = value; }

    public void setRightSeekbarValue(int value){rightSeekbarValue = value; }

    public int getLeftSeekbarValue(){return leftSeekbarValue; }

    public int getRightSeekbarValue(){return rightSeekbarValue; }



}
