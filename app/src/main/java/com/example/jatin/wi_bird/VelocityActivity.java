package com.example.jatin.wi_bird;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.triggertrap.seekarc.SeekArc;


public class VelocityActivity extends ActionBarActivity {
    String conn_blue = "Connect via Bluetooth", conn_wifi = "Connect via Wi-fi", dis = "Disconnect";
    private Toolbar toolbar;
    BluetoothAdapter mAdapter;
    WifiManager wifi;

    //for debugging
    final String TAG = "Wi-bird";

    //to store object of BtConnection
    BtConnection mBtConnection;

    //stores whether activity is bound to service or not
    boolean mBound = false;

    // select the left and right motor velocity with the help of seekbar
    SeekArc leftMotorVelocitySeekBar, rightMotorVelocitySeekBar;

    // Text box used to enter the velocity of both the wheels
    TextView leftMotorVelocityText, rightMotorVelocityText;

    // sets the velocity of robot
    Button setButton;

    // stores the left and right motor velocity
    int leftMotorVelocity, rightMotorVelocity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_velocity);

        // finds a view that was identified by the id left_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        leftMotorVelocitySeekBar = (SeekArc) findViewById(R.id.seekArc_left);
        // finds a view that was identified by the id right_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        rightMotorVelocitySeekBar = (SeekArc) findViewById(R.id.seekArc_right);
        // finds a view that was identified by the id set_button from the XML that was processed in onCreate(Bundle)
        setButton = (Button) findViewById(R.id.set_button);
        // finds a view that was identified by the id left_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        leftMotorVelocityText = (TextView) findViewById(R.id.left_velocity_text);
        // finds a view that was identified by the id right_velocity_seekbar from the XML that was processed in onCreate(Bundle)
        rightMotorVelocityText = (TextView) findViewById(R.id.right_velocity_text);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mAdapter.isEnabled());
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ((MyApplication) this.getApplication()).setEarlyWifiState(wifi.isWifiEnabled());


        // finds a view that was identified by the id left_velocity_seekbar from the XML that was processed in onCreate(Bundle)

        // assigns OnSeekBarChangeListener to leftMotorVelocitySeekBar
        leftMotorVelocitySeekBar.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            // stores the current value of seek bar
            int progress = 0;

            /**
             *
             * Function Name: onProgressChanged
             * Input: seekBar --> The SeekBar whose progress has changed
             *        progress --> The current progress level. This will be in the range 0..max where max was set by setMax(int). (The default value for max is 100.)
             *        fromUser -->  True if the progress change was initiated by the user.
             * Output: sets the leftMotorVelocityText value according to the seekbar's value
             * Logic: gets the seekbar's current value stored in the variable progressValue and sets the text of leftMotorVelocityText accordingly
             * Example Call: called automatically when the seekbar's value changes
             *
             */
            @Override
            public void onProgressChanged(SeekArc seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                leftMotorVelocityText.setText("" + progress);
            }

            /**
             *
             * Function Name: onStartTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the seekbar is touched
             *
             */
            @Override
            public void onStartTrackingTouch(SeekArc seekBar) {
            }

            /**
             *
             * Function Name: onStopTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the change in seekbar's value stops
             *
             */
            @Override
            public void onStopTrackingTouch(SeekArc seekBar) {

            }
        });


        // assigns OnSeekBarChangeListener to rightMotorVelocitySeekBar
        rightMotorVelocitySeekBar.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            // stores the current value of seek bar
            int progress = 0;

            /*
             *
             * Function Name: onProgressChanged
             * Input: seekBar --> The SeekBar whose progress has changed
             *        progress --> The current progress level. This will be in the range 0..max where max was set by setMax(int). (The default value for max is 100.)
             *        fromUser -->  True if the progress change was initiated by the user.
             * Output: sets the rightMotorVelocityText value according to the seekbar's value
             * Logic: gets the seekbar's current value stored in the variable progressValue and sets the text of rightMotorVelocityText accordingly
             * Example Call: called automatically when the seekbar's value changes
             *
             */
            @Override
            public void onProgressChanged(SeekArc seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                rightMotorVelocityText.setText("" + progress);
            }

            /*
             *
             * Function Name: onStartTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the seekbar is touched
             *
             */
            @Override
            public void onStartTrackingTouch(SeekArc seekBar) {
            }

            /**
             *
             * Function Name: onStopTrackingTouch
             * Input: seekBar --> The SeekBar in which the touch gesture began
             * Example Call: called automatically when the change in seekbar's value stops
             *
             */
            @Override
            public void onStopTrackingTouch(SeekArc seekBar) {

            }
        });


       /* // assigns TextChangedListener to leftMotorVelocityText
        leftMotorVelocityText.addTextChangedListener(new TextWatcher() {

            *//**
             *
             * Function Name: beforeTextChanged
             * Example Call: This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
             *               It is an error to attempt to make changes to s from this callback.
             *
             *//*
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            *//**
             *
             * Function Name: onTextChanged
             * Input: s --> char sequence which stores the characters
             *        start --> integer which stores the starting location from where the characters have been changed
             *        before --> integer which contains the length of the text that has been replaced
             *        count --> integer which stores the length of new characters
             * Output: sets the seek bars value according to the text entered
             * Logic: This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
             * Example Call: called automatically when the text is changing
             *
             *//*
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!leftMotorVelocityText.getText().toString().isEmpty() && leftMotorVelocityText.getText().toString().length()<4) {
                    int value = Integer.parseInt(leftMotorVelocityText.getText().toString());

                    if (value <= 255) {
                       leftMotorVelocitySeekBar.setProgress(0);
                    } else {
                        leftMotorVelocitySeekBar.setProgress(0);
                        leftMotorVelocityText.setText("0");
                    }
                }

            }


            *//**
             *
             * Function Name: afterTextChanged
             * Input: the editable text s
             * Logic: This method is called to notify you that, somewhere within s, the text has been changed.
             * Example Call: called automatically after the text has been changed
             *
             *//*
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // assigns TextChangedListener to rightMotorVelocityText
        rightMotorVelocityText.addTextChangedListener(new TextWatcher() {

            *//**
             *
             * Function Name: beforeTextChanged
             * Example Call: This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
             *               It is an error to attempt to make changes to s from this callback.
             *
             *//*
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            *//**
             *
             * Function Name: onTextChanged
             * Input: s --> char sequence which stores the characters
             *        start --> integer which stores the starting location from where the characters have been changed
             *        before --> integer which contains the length of the text that has been replaced
             *        count --> integer which stores the length of new characters
             * Output: sets the seek bars value according to the text entered
             * Logic: This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
             * Example Call: called automatically when the text is changing
             *
             *//*
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               *//* if (!rightMotorVelocityText.getText().toString().isEmpty() && rightMotorVelocityText.getText().toString().length()<4) {
                    int value = Integer.parseInt(rightMotorVelocityText.getText().toString());

                    if (value <= 255) {
                        rightMotorVelocitySeekBar.setProgress(value);
                    } else {
                        rightMotorVelocitySeekBar.setProgress(0);
                        rightMotorVelocityText.setText("0");
                    }
                }*//*
            }

            *//**
             *
             * Function Name: afterTextChanged
             * Input: the editable text s
             * Logic: This method is called to notify you that, somewhere within s, the text has been changed.
             * Example Call: called automatically after the text has been changed
             *
             *//*
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
        // assigns the OnClickListener with the setButton
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!leftMotorVelocityText.getText().toString().isEmpty() && !rightMotorVelocityText.getText().toString().isEmpty()) {
                    leftMotorVelocity = Integer.parseInt(leftMotorVelocityText.getText().toString());
                    rightMotorVelocity = Integer.parseInt(rightMotorVelocityText.getText().toString());
                }
                // if entered left velocity is less than 127 then it sends "y" to the output stream
                if (leftMotorVelocity <= 127) {
                    mBtConnection.sendData("y");
                }
                // if entered left velocity is greater than 127 then it sends "z" to the output stream
                else if (leftMotorVelocity <= 255) {
                    mBtConnection.sendData("z");
                    leftMotorVelocity = leftMotorVelocity / 2;
                }
                // sends the calculated left motor velocity to the output stream in the form of string
                mBtConnection.sendData(String.valueOf(leftMotorVelocity));
                // if entered right velocity is less than 127 then it sends "A" to the output stream
                if (rightMotorVelocity <= 127) {
                    mBtConnection.sendData("A");
                }
                // if entered right velocity is greater than 127 then it sends "B" to the output stream
                else if (rightMotorVelocity <= 255) {
                    mBtConnection.sendData("B");
                    rightMotorVelocity = rightMotorVelocity / 2;
                }
                // sends the calculated right motor velocity to the output stream in the form of string
                mBtConnection.sendData(String.valueOf((char) rightMotorVelocity));

                Intent i = new Intent(VelocityActivity.this, ControlActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    /**
     * Function Name: onStart
     * Input: None
     * Output: binds the velocity fragment with the local bluetooth service
     * Logic: Calls the activity BtConnection and binds this activity with the BtConnection by calling bindService method
     * Example Call: Called automatically when the fragment becomes visible
     */
    @Override
    public void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Function Name: onStop
     * Input: None
     * Output: Unbinds this fragment from the bluetooth service
     * Logic: calls the method unbindService(BtConnection) to unbind from the bluetooth service
     * Example Call: called automatically when the fragment is not visible
     */
    @Override
    public void onStop() {
        super.onStop();

        // Unbind from the service
        if (mBound) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Function Name: onResumne
     * Input: None
     * Output: sets the screen orientation to Portrait mode
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     */
    @Override
    public void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /**
         *
         * Function Name: onServiceConnected
         * Input: name -->	The concrete component name of the service that has been connected.
         *        service --> The IBinder of the Service's communication channel, which you can now make calls on.
         * Output: bounds to the local service
         * Logic:  bound to LocalService, cast the IBinder and get LocalService instance by calling getservice method
         * Example Call: called automatically when a connection to the Service has been established, with the IBinder of the communication channel to the Service.
         *
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BtConnection.LocalBinder binder = (BtConnection.LocalBinder) service;
            mBtConnection = binder.getService();
            mBound = true;

        }

        /**
         *
         * Function Name: onServiceDisconnected
         * Input: name --> The concrete component name of the service whose connection has been lost.
         * Output: sets the variable mBound to false to indicate that fragment is not connected to the service
         * Logic: Called when a connection to the Service has been lost. This typically happens when the process hosting the service has crashed or been killed.
         *        This does not remove the ServiceConnection itself -- this binding to the service will remain active, and you will receive a call to
         *        onServiceConnected(ComponentName, IBinder) when the Service is next running.
         * Example Call: called automatically
         *
         */
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_velocity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            Intent intent = new Intent(VelocityActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
