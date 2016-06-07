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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class ControlActivity extends ActionBarActivity {
    String conn_blue = "Connect via Bluetooth", conn_wifi = "Connect via Wi-fi", dis = "Disconnect";
    private Toolbar toolbar;
    BluetoothAdapter mAdapter;
    //for debugging
    final String TAG = "Wi-bird";
    //to store object of BtConnection
    BtConnection mBtConnection;
    //stores whether activity is bound to service or not
    boolean mBound = false;
    WifiManager wifi;
    private Button forward, forward_100, backward, backward_100, stop, buzzer_on, buzzer_off,
            rotate_left, rotate_left_90, rotate_right, rotate_right_90, rotate_back;
    //variables for stopping the different threads
    private boolean bForward, bBackward, bRotateLeft_90, bRotateRight_90, bBuzzerOn, bBuzzerOFF,
            bForward_100, bBackward_100, bRotateLeft, bRotateRight, bRotateBack;


    //Constants for various motions
    final String moveForward = "a";
    final String moveBackward = "b";
    final String rotateLeft = "c";
    final String rotateRight = "d";
    final String moveStop = "e";
    final String makeBuzzerOn = "f";
    final String makeBuzzerOff = "g";
    final String moveForward100 = "C";
    final String moveBackward100 = "D";
    final String rotateLeft90 = "E";
    final String rotateRight90 = "F";
    final String rotateBack = "G";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        forward = (Button) findViewById(R.id.forward);
        //forward_100 = (Button) findViewById(R.id.forward_100);
        backward = (Button) findViewById(R.id.backward);
        //backward_100 = (Button) findViewById(R.id.backward_100);
        stop = (Button) findViewById(R.id.stop);
        buzzer_on = (Button) findViewById(R.id.buzzer_on);
        buzzer_off = (Button) findViewById(R.id.buzzer_off);
        rotate_left = (Button) findViewById(R.id.rotate_left);
        //rotate_left_90 = (Button) findViewById(R.id.rotate_left_90);
        rotate_right = (Button) findViewById(R.id.rotate_right);
        //rotate_right_90 = (Button) findViewById(R.id.rotate_right_90);
        //rotate_back = (Button) findViewById(R.id.rotate_back);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mAdapter.isEnabled());
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ((MyApplication) this.getApplication()).setEarlyWifiState(wifi.isWifiEnabled());
    }

    /**
     * Function Name: onStart
     * Input: None
     * Output: binds the  MotionControlFragment with the local bluetooth service
     * Logic: Calls the activity BtConnection and binds this activity with the BtConnection by calling bindService method
     * Example Call: Called automatically when the fragment becomes visible
     */
    @Override
    public void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(ControlActivity.this, BtConnection.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    /**
     * Function Name: onResume
     * Input: None
     * Output: sets the screen orientation to Portrait mode and initializes the forward, back, right, left and pause variable for various motions
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation and initializes forward, back, right, left and pause variables to false
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     */
    @Override
    public void onResume() {
        super.onResume();

        //sets up the motion screen and assign listeners to various images and buttons
        setUpMotionInterface();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        stopAllThreads();
        Log.d(TAG, "Custom Mode onResume()");
    }

    /**
     * Function Name: onPause
     * Input: None
     * Output: stops all the motions
     * Logic: sets all the variables defining the motion equals to false
     * Example Call: called automatically when the fragment goes in  inactive mode
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Custom Mode onPause()");
        stopAllThreads();
        mBtConnection.sendData(moveStop);
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

        /*
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


    /**
     * Function Name: setUpMotionInterface
     * Input: void
     * Output: assigns listeners to various motion buttons and starts the motion when the button is clicked
     * Logic: when the forward, backward, left, right button is clicked then it starts teh forward, backward, left and right thread respectively to start the motion.
     * Example Call: setUpMotionInterface()
     */
    private void setUpMotionInterface() {
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBound()) {
                    stopAllThreads();
                    bForward = true;
                    Thread forwardThread = new Thread(new ForwardThread());
                    forwardThread.setPriority(1);
                    forwardThread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();

            }
        });
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bBackward = true;
                    Thread backwardThread = new Thread(new BackwardThread());
                    backwardThread.setPriority(1);
                    backwardThread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bRotateRight = true;
                    Thread rightThread = new Thread(new RightThread());
                    rightThread.setPriority(1);
                    rightThread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bRotateLeft = true;
                    Thread leftThread = new Thread(new LeftThread());
                    leftThread.setPriority(1);
                    leftThread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    mBtConnection.sendData(moveStop);
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });

        buzzer_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bBuzzerOn = true;
                    Thread buzzerOnThread = new Thread(new BuzzerOnThread());
                    buzzerOnThread.setPriority(1);
                    buzzerOnThread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });

        buzzer_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bBuzzerOFF = true;
                    Thread buzzerOffThread = new Thread(new BuzzerOffThread());
                    buzzerOffThread.setPriority(1);
                    buzzerOffThread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
       /* rotate_left_90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bRotateLeft_90 = true;
                    Thread left_90Thread = new Thread(new Left_90Thread());
                    left_90Thread.setPriority(1);
                    left_90Thread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        rotate_right_90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bRotateRight_90 = true;
                    Thread right_90Thread = new Thread(new Right_90Thread());
                    right_90Thread.setPriority(1);
                    right_90Thread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        forward_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bForward_100 = true;
                    Thread forward_100Thread = new Thread(new Forward_100Thread());
                    forward_100Thread.setPriority(1);
                    forward_100Thread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        backward_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bBackward_100 = true;
                    Thread backward_100Thread = new Thread(new Backward_100Thread());
                    backward_100Thread.setPriority(1);
                    backward_100Thread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        rotate_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    stopAllThreads();
                    bRotateBack = true;
                    Thread rotateBackThread = new Thread(new RotateBackThread());
                    rotateBackThread.setPriority(1);
                    rotateBackThread.start();
                } else
                    Toast.makeText(getApplicationContext(), "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    // Thread to make the bot move forward
    private class ForwardThread implements Runnable {
        public void run() {
            while (bForward) {
                try {
                    mBtConnection.sendData(moveForward);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot move backward
    private class BackwardThread implements Runnable {
        public void run() {
            while (bBackward) {
                try {
                    mBtConnection.sendData(moveBackward);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot move left
    private class LeftThread implements Runnable {
        public void run() {
            while (bRotateLeft) {
                try {
                    mBtConnection.sendData(rotateLeft);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot move right
    private class RightThread implements Runnable {
        public void run() {
            while (bRotateRight) {
                try {
                    mBtConnection.sendData(rotateRight);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to turn On the buzzer
    private class BuzzerOnThread implements Runnable {
        public void run() {
            while (bBuzzerOn) {
                try {
                    mBtConnection.sendData(makeBuzzerOn);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }

            }
        }
    }

    // Thread to turn Off the buzzer
    private class BuzzerOffThread implements Runnable {
        public void run() {
            while (bBuzzerOFF) {
                try {
                    mBtConnection.sendData(makeBuzzerOff);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

   /* // Thread to make the bot rotate left 90
    private class Left_90Thread implements Runnable {
        public void run() {
            while (bRotateLeft_90) {
                try {
                    mBtConnection.sendData(rotateLeft90);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot rotate right 90
    private class Right_90Thread implements Runnable {
        public void run() {
            while (bRotateRight_90) {
                try {
                    mBtConnection.sendData(rotateRight90);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to  make the bot move forward 100
    private class Forward_100Thread implements Runnable {
        public void run() {
            while (bForward_100) {
                try {
                    mBtConnection.sendData(moveForward100);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot move backward 100
    private class Backward_100Thread implements Runnable {
        public void run() {
            while (bBackward_100) {
                try {
                    mBtConnection.sendData(moveBackward100);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    // Thread to make the bot rotate back
    private class RotateBackThread implements Runnable {
        public void run() {
            while (bRotateBack) {
                try {
                    mBtConnection.sendData(rotateBack);

                } catch (Exception ex) {
                    Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }*/


    public void stopAllThreads() {
        bForward = bBackward = bRotateLeft_90 = bRotateRight_90 = bBuzzerOFF = bBuzzerOn =
                bForward_100 = bBackward_100 = bRotateLeft = bRotateRight = bRotateBack = false;
    }

    /**
     * Function Name: isBound
     * Input: None
     * Output: boolean value which represents whether teh device is connected to robot or not
     * Logic: Checks whether a bluetooth connection has been established or not by checking the value of mBtConnection object. If it is connected then mBound
     * is set to true else false
     * Example Call: isBound()
     */
    public boolean isBound() {
        if (mBtConnection.getStream() != null)
            mBound = true;
        else
            mBound = false;
        return mBound;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            Intent intent = new Intent(ControlActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
