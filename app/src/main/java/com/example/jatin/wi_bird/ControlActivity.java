/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: ControlActivity.java
 * Functions: onCreate(Bundle), onStart(), onResume(), onStop(), onPause(),onServiceConnected(ComponentName, IBinder),
 *            onServiceDisconnected(ComponentName), onActivityResult(int, int, Intent), onCreateOptionsMenu(Menu),
 *            onOptionsItemSelected(MenuItem), connectDevice(Intent), setStatus(String), setUpMotionInterface(),
 *            stopAllThreads(), isBound(),  onKeyDown(int, KeyEvent), showAlertDialog()
 * Global Variables: toolbar, mAdapter, TAG, mBtConnection, device, mBound, progress, pb, REQUEST_CONNECT_DEVICE,
  *            REQUEST_ENABLE_BT, buzzer_status, forward, backward, stop, buzzer_on, buzzer_off,
               rotate_left, rotate_right, df, activity, moveForward, moveBackward, rotateLeft, rotateRight, moveStop,makeBuzzerOn,
               makeBuzzerOff
 *
 */
package com.example.jatin.wi_bird;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/*
 *
 * Class Name: ControlActivity
 * Logic: This activity controls the motion of Robot through bluetooth connection
 * Example Call: new ControlActivity()
 *
 */

public class ControlActivity extends ActionBarActivity {

    //to hold Toolbar object
    private Toolbar toolbar;
    //to hold the BluetoothAdapter object
     BluetoothAdapter mAdapter;
    //for debugging
    final String TAG = "Bluebird";
    //to store object of BtConnection
    BtConnection mBtConnection;
    //to hold the BluetoothDevice object
    BluetoothDevice device;
    //stores whether activity is bound to service or not
    private boolean mBound = false;
    //to hold the ProgressDialog object
    ProgressDialog progress;
    //to hold the SpannableString object
    SpannableString pb;
    //Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    //to bind to the TextView UI component
    TextView buzzer_status;
    // to bind to the various robot control UI ImageView
    private ImageView forward, backward, stop, buzzer_on, buzzer_off,
            rotate_left, rotate_right;
    //to hold the NavigationDrawerFragment object
    NavigationDrawerFragment df;
    //to hold the current activity reference
    public static Activity activity;
    //Constants for various motions
    private final String moveForward = "1";
    private final String moveBackward = "2";
    private final String rotateLeft = "3";
    private final String rotateRight = "4";
    private final String moveStop = "5";
    private final String makeBuzzerOn = "6";
    private final String makeBuzzerOff = "7";

    /**
     *
     * Function Name: onCreate
     * Input: savedInstanceState --> If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently
     *        supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * Output: sets up the ControlActivity
     * Logic: Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate the activity's UI,
     *        using findViewById(int) to programmatically interact with widgets in the UI
     * Example Call: Called automatically when the activity is created
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        //binding to UI components
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        forward = (ImageView) findViewById(R.id.forward);
        backward = (ImageView) findViewById(R.id.backward);
        stop = (ImageView) findViewById(R.id.stop);
        buzzer_on = (ImageView) findViewById(R.id.buzzer_on);
        buzzer_off = (ImageView) findViewById(R.id.buzzer_off);
        rotate_left = (ImageView) findViewById(R.id.rotate_left);
        rotate_right = (ImageView) findViewById(R.id.rotate_right);
        buzzer_status = (TextView) findViewById(R.id.buzzer_status);
        //setting font
        SpannableString s = new SpannableString("Control Options");
        s.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pb = new SpannableString(" Fetching paired devices... ");
        pb.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, pb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //holding reference of current activity
        activity = this;
        //seting up navigation drawer icon
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setting title with different font
        getSupportActionBar().setTitle(s);
        //creating and assigning the ProgressDialog object
        progress = new ProgressDialog(ControlActivity.this);
        //setting up navigation drawer
        df = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        //setting initial visibility of the buttons
        buzzer_on.setVisibility(View.VISIBLE);
        buzzer_off.setVisibility(View.INVISIBLE);
        //storing the bluetooth adapter
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //enabling the bluetooth if not already enabled
        if (!mAdapter.isEnabled()) {
            mAdapter.enable();
        }
        //automatically asks for connection to paired bluetooth devices when this activity is launched first time in the app
        if (((MyApplication) this.getApplication()).getIsControlActivityLaunchedFirst()) {
            ((MyApplication) this.getApplication()).setIsControlActivityLaunchedFirst(false);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            progress.setMessage(pb);
                            progress.show();

                        }

                        public void onFinish() {
                            //Launch the DeviceListActivity to see devices
                            progress.dismiss();
                            Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                        }
                    }.start();
                }
            });
        }
    }

    /**
     * Function Name: onStart
     * Input: None
     * Output: binds the ControlActivity with the local bluetooth service
     * Logic: Calls the Service BtConnection and binds this activity with the BtConnection by calling bindService method
     * Example Call: Called automatically when the activity becomes visible
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ControlActivity onStart()");
        // Bind to LocalService
        Intent intent = new Intent(ControlActivity.this, BtConnection.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Function Name: onResume
     * Input: None
     * Output: sets up motion Interface
     * Logic: calling  setUpMotionInterface() method
     * Example Call: called automatically when the activity becomes visible and user can interact with it
     */
    @Override
    public void onResume() {
        super.onResume();
        //sets up the motion screen and assign listeners to various images and buttons
        setUpMotionInterface();
        Log.d(TAG, "ControlActivity onResume()");
    }

     /**
     * Function Name: onPause
     * Input: None
     * Output: stops all the motions
     * Logic: sets all the variables defining the motion equals to false
     * Example Call: called automatically when the activity goes in  inactive mode
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Control Activity onPause()");
        try {
            mBtConnection.sendData(moveStop);
        }
        catch (Exception e) {
        }
    }

    /**
     * Function Name: onStop
     * Input: None
     * Output: Unbinds this activity from the bluetooth service
     * Logic: calls the method unbindService(BtConnection) to unbind from the bluetooth service
     * Example Call: called automatically when the activity is not visible
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Custom Mode onStop()");
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
     * Function Name: onActivityResult
     * Input: requestCode --> The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * resultCode --> The integer result code returned by the child activity through its setResult().
     * data --> An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     * Output: Depending upon the requestCode it either enables the bluetooth of the device or calls a method connect(data) to make a bluetooth connection
     * with the selected device
     * Logic: called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
     * The resultCode will be RESULT_CANCELED if the activity explicitly returned that, didn't return any result, or crashed during its operation.
     * You will receive this call immediately before onResume() when your activity is re-starting.
     * Example Call: called automatically after startActivityForResult(Intent, int)
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CONNECT_DEVICE:
                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        mBound = true;
                        connectDevice(data);
                    }
                    break;
                case REQUEST_ENABLE_BT:
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_CANCELED) {
                        // SensorItem did not enable Bluetooth or an error occurred
                        Toast.makeText(activity, "Bluetooth not enabled. Leaving Bluebird", Toast.LENGTH_SHORT).show();
                        finish();
                    }
            }
        }
    }


    /**
     * Function Name: connectDevice
     * Input: data --> An Intent with DeviceListActivity#EXTRA_DEVICE_ADDRESS extra.
     * Output: makes a bluetooth connection with the selected device
     * Logic: gets the mac address of the bluetooth device from the intent passed as parameter, then it gets the BluetoothDevice object by calling the method getRemoteDevice(address)
     * and finally sends that object to the BtConnection to make connection between the device and the robot.
     * Example Call: connectDevice(Intent data)
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        device = mAdapter.getRemoteDevice(address);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                Toast.makeText(activity, "Connecting...", Toast.LENGTH_SHORT).show();
            }
        });


        mBtConnection = new BtConnection(this, device);
        if (mBound) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        Log.d(TAG, "Connection Started...");
                        /** Bluetooth connect function returns true if connection is successful, else false. */
                        if (!mBtConnection.connect()) {
                            Toast.makeText(activity, "No connection established", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(activity, "Connection established", Toast.LENGTH_SHORT).show();
                            setStatus("Connected to " + device.getName());
                        }
                        Log.d(TAG, "Connection Successful");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Connection Failed");
            }
        }
    }

    /**
     * Function Name: setStatus
     * Input: status --> String which is set on teh action bar
     * Output: sets the action bar status
     * Logic: calls the method setSubtitle to set the status
     * Example Call: setStatus("Done..")
     */
    public void setStatus(String status) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //setting the font of subtitle
            SpannableString s = new SpannableString(status);
            s.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //setting the subtile when connected to robot
            actionBar.setSubtitle(s);

        }
    }

    /**
     * Function Name: setUpMotionInterface
     * Input: void
     * Output: assigns listeners to various motion buttons and starts the motion when the button is clicked
     * Logic: when the forward, backward, left, right, buzzer on, buzzer off button is clicked, it sends the respective data
     *        to the bot
     * Example Call: setUpMotionInterface()
     */
    private void setUpMotionInterface() {
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                   try {
                        mBtConnection.sendData(moveForward);
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception ", ex);
                    }
                } else
                    Toast.makeText(activity, "Connect to Robot First", Toast.LENGTH_SHORT).show();

            }
        });
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    try {
                        mBtConnection.sendData(moveBackward);
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception ", ex);
                    }
                } else
                    Toast.makeText(activity, "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    try {
                        mBtConnection.sendData(rotateRight);
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception ", ex);
                    }
                } else
                    Toast.makeText(activity, "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    try {
                        mBtConnection.sendData(rotateLeft);
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception ", ex);
                    }
                } else
                    Toast.makeText(activity, "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    mBtConnection.sendData(moveStop);
                } else
                    Toast.makeText(activity, "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });

        buzzer_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    try {
                        mBtConnection.sendData(makeBuzzerOn);
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception ", ex);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            buzzer_on.setVisibility(View.INVISIBLE);
                            buzzer_off.setVisibility(View.VISIBLE);
                            buzzer_status.setText("BUZZER ON");
                        }
                    });
                } else
                    Toast.makeText(activity, "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });

        buzzer_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound()) {
                    try {
                        mBtConnection.sendData(makeBuzzerOff);
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception ", ex);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            buzzer_off.setVisibility(View.INVISIBLE);
                            buzzer_on.setVisibility(View.VISIBLE);
                            buzzer_status.setText("BUZZER OFF");
                        }
                    });
                } else
                    Toast.makeText(activity, "Connect to Robot First", Toast.LENGTH_SHORT).show();
            }
        });


    }



    /**
     * Function Name: isBound
     * Input: None
     * Output: boolean value which represents whether the device is connected to robot or not
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
    /**
     *
     * Function Name: onCreateOptionsMenu
     * Input: menu --> The options menu in which you place your items.
     * Output: return true for the menu to be displayed; if you return false it will not be shown.
     * Logic: Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * Example Call: This is only called once, the first time the options menu is displayed.
     *
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control, menu);
        //setting font of the pop up action bar maenu
        SpannableString title1 = new SpannableString(getString(R.string.bluetooth));
        title1.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, title1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString title2 = new SpannableString(getString(R.string.disconnect));
        title2.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, title2.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        MenuItem menuItem1 = menu.findItem(R.id.bluetooth);
        MenuItem menuItem2 = menu.findItem(R.id.disconnect);
        //setting menu item
        menuItem1.setTitle(title1);
        menuItem2.setTitle(title2);
        return true;
    }

    /**
     *
     * Function Name: onOptionsItemSelected
     * Input: menu --> The options menu in which you place your items.
     * Output: boolean Return false to allow normal menu processing to proceed, true to consume it here.
     * Logic: This hook is called whenever an item in your options menu is selected. The default implementation simply returns false to have the normal
     *        processing happen (calling the item's Runnable or sending a message to its Handler as appropriate). You can use this method for any items for which you would like to do processing without those other facilities.
     * Example Call: Derived classes should call through to the base class for it to perform the default menu handling.
     *
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //run the code if bluetooth option is clicked
        if (id == R.id.bluetooth) {
            if (!mAdapter.isEnabled()) {
                mAdapter.enable();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    new CountDownTimer(3000, 1000) {
                        //run this method till the timer is running
                        public void onTick(long millisUntilFinished) {
                            progress.setMessage(pb);
                            progress.show();
                        }
                        //run this method when the timer ends up
                        public void onFinish() {
                            //Launch the DeviceListActivity to see devices
                            progress.dismiss();
                            Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                        }
                    }.start();
                }
            });
        }
        //run the code when the disconnect option is clicked
        if (id == R.id.disconnect) {
            mBtConnection.disconnect();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    Toast.makeText(activity, "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Function Name: onKeyDown
     * Input: keyCode --> holds the code of the phone back key pressed by the user
     *        event   --> holds the KeyEvent reference
     * Output: show ALertDialog, with the option to quit the app, if the back key is pressed by the user
     * Logic: calls the method showAlertDialog() to do the above mentioned
     * Example Call: called automatically when the key is pressed
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showAlertDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * Function Name: showAlertDialog
     * Input: None
     * Output: show ALertDialog, with the option to quit the app
     * Logic: creates AlertDialog.Builder object and calls different methods to perform the required operation
     * Example Call: showAlertDialog()
     */

    private void showAlertDialog() {
        //getting the bluetooth's state before the app was launched
        final boolean a = ((MyApplication) this.getApplication()).getEarlyBluetoothState();
        //getting the value of the bool variable telling whether the Control Activity is launched first time
        final boolean b = ((MyApplication) this.getApplication()).getIsControlActivityLaunchedFirst();
        //setting font for AlertDialog message
        SpannableString exit = new SpannableString("Do you want to exit application?");
        exit.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, exit.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //creating AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
        //setting the changed font message
        builder.setMessage(exit);
        //setting what to do depending  on whether Negative or Positive button is clicked
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //disabling bluetooth if it was disabled before launching the app
                        if (!a) {
                            mAdapter.disable();
                        }
                        //setting the bool variable to true
                        if (!b) {
                            ((MyApplication) activity.getApplication()).setIsControlActivityLaunchedFirst(true);
                        }

                        finish();
                        //  close();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancels the diaog
                        dialog.cancel();
                    }
                });

        final AlertDialog alertdialog = builder.create();
        alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                //customising the font for the buttons in AlertDialog
                Typeface c = Typeface.createFromAsset(getAssets(), "fonts/Classic Robot Condensed.ttf");
                (alertdialog.getButton(Dialog.BUTTON_POSITIVE)).setTypeface(c);
                (alertdialog.getButton(Dialog.BUTTON_NEGATIVE)).setTypeface(c);
            }
        });
        alertdialog.show();
    }

}
