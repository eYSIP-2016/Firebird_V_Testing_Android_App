package com.example.jatin.wi_bird;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.logging.Handler;


public class MainActivity extends ActionBarActivity {

    //for debugging
    final String TAG = "Wi-bird";

    String conn_blue = "Connect via Bluetooth", conn_wifi = "Connect via Wi-fi", dis = "Disconnect";
    private Toolbar toolbar;
    NavigationDrawerFragment df;

    WifiManager wifi;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //Bluetooth related objects
    BluetoothAdapter mAdapter = null;
    BtConnection mBtConnection;

    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        df = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mAdapter.isEnabled());
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ((MyApplication) this.getApplication()).setEarlyWifiState(wifi.isWifiEnabled());
    }

    /**
     * Function Name: onStart
     * Input: None
     * Output: If bluetooth has not been enabled then it enables the bluetooth and binds the activity with the local bluetooth service
     * Logic: Calls the isEnabled() method of BluetoothAdapter class to check whether bluetooth is enabled or not. If bluetooth is not enabled then it sends a
     * request to enable the bluetooth. After bluetooth has been enabled then binds to the local service by calling its bindService method.
     * Example Call: Called automatically when the fragment becomes visible
     */
    @Override
    public void onStart() {
        super.onStart();


        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        /*if(mBtConnection.connect())
        {
            setStatus("Connected to " + mBtConnection.mDevice.getName());
        }*/
    }



    /**
     * Function Name: onStop
     * Input: None
     * Output: Unbinds this fragment from the bluetooth service
     * Logic: calls the method unbindService(BtConnection) to unbind from the bluetooth service
     * Example Call: called automatically when the fragment is not visible
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
       /* if (mBound) {
            unbindService(mConnection);
            mBound = false;
            Toast.makeText(getApplicationContext(), mBound+"", Toast.LENGTH_LONG).show();
        }*/
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /*
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
           // Toast.makeText(getApplicationContext(), mBound+"", Toast.LENGTH_LONG).show();

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
                        connectDevice(data);
                    }
                    break;
                case REQUEST_ENABLE_BT:
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_CANCELED) {
                        // SensorItem did not enable Bluetooth or an error occurred
                        Toast.makeText(getApplicationContext(), "Bluetooth not enabled. Leaving Wi-bird", Toast.LENGTH_SHORT).show();
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
        BluetoothDevice device = mAdapter.getRemoteDevice(address);
        Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();

        mBtConnection = new BtConnection(this, device);
        if (mBound) {
            //mBtConnection.connect();
            try {
                Log.d(TAG, "Connection Started...");

                /** Bluetooth connect function returns true if connection is successful, else false. */
                if (!mBtConnection.connect()) {
                    Toast.makeText(this, " No connection established ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, " Connection established ", Toast.LENGTH_SHORT).show();
                    setStatus("Connected to " + device.getName());
                }
                Log.d(TAG, "Connection Successful");
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
        if (actionBar != null)
            actionBar.setSubtitle(status);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bluetooth) {
            if (!mAdapter.isEnabled()) {
                mAdapter.enable();
            }
            new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {
                    Toast.makeText(getApplication(),"fetching paired devices...",Toast.LENGTH_LONG).show();

                }

                public void onFinish() {
                    //Launch the DeviceListActivity to see devices
                    Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }.start();
        }
        if(id == R.id.wifi) {
            if (!wifi.isWifiEnabled()) {
                wifi.setWifiEnabled(true);
            }
        }
        if(id == R.id.disconnect) {
            mBtConnection.disconnect();
            Toast.makeText(getApplication(), "Disconnected",Toast.LENGTH_LONG).show();
        }



        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        final boolean a = ((MyApplication) this.getApplication()).getEarlyBluetoothState();
        final boolean b = ((MyApplication) this.getApplication()).getEarlyWifiState();

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (!a) {
                            mAdapter.disable();
                        }
                        if (!b) {
                            wifi.setWifiEnabled(false);
                        }
                        finish();
                        //  close();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }

}
