package com.example.jatin.wi_bird;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.graphics.Typeface;
import android.net.wifi.WifiManager;
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

import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.logging.Handler;


public class MainActivity extends ActionBarActivity {

    //for debugging
    final String TAG = "Wi-bird";
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
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);

        activity = this;
        SpannableString s = new SpannableString("Bluebird");
        s.setSpan(new TypefaceSpan(this,"Classic Robot Condensed.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

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

    }
    @Override
    public void onResume() {
        super.onResume();


        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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
        if (mBound) {
            unbindService(mConnection);
            mBound = true;
            //Toast.makeText(getApplicationContext(), mBound+"", Toast.LENGTH_LONG).show();
        }
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
                        Toast.makeText(getApplicationContext(), " Bluetooth not enabled. Leaving Bluebird ", Toast.LENGTH_SHORT).show();
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
        final BluetoothDevice device = mAdapter.getRemoteDevice(address);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_LONG).show();
            }
        });


        mBtConnection = new BtConnection(this, device);
        if (mBound) {
            //mBtConnection.connect();
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        Log.d(TAG, "Connection Started...");
                        /** Bluetooth connect function returns true if connection is successful, else false. */
                        if (!mBtConnection.connect()) {
                            Toast.makeText(getApplicationContext(), " No connection established ", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), " Connection established ", Toast.LENGTH_SHORT).show();
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
        if (actionBar != null){
            SpannableString s = new SpannableString(status);
            s.setSpan(new TypefaceSpan(this,"Classic Robot Condensed.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setSubtitle(s);

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SpannableString title1 = new SpannableString(getString(R.string.bluetooth));
        title1.setSpan(new TypefaceSpan(this,"Classic Robot Condensed.ttf"), 0, title1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString title2 = new SpannableString(getString(R.string.disconnect));
        title2.setSpan(new TypefaceSpan(this,"Classic Robot Condensed.ttf"), 0, title2.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        MenuItem menuItem1 = menu.findItem(R.id.bluetooth);
        MenuItem menuItem2 = menu.findItem(R.id.disconnect);

        menuItem1.setTitle(title1);
        menuItem2.setTitle(title2);

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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            Toast.makeText(getApplication(), " Fetching paired devices... ", Toast.LENGTH_LONG).show();

                        }

                        public void onFinish() {
                            //Launch the DeviceListActivity to see devices
                            Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                        }
                    }.start();
                }
            });
        }

        if(id == R.id.disconnect) {
            mBtConnection.disconnect();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    Toast.makeText(getApplication(), " Disconnected ", Toast.LENGTH_LONG).show();
                }
            });
        }



        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //exitByBackKey();
            showAlertDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

   /* protected void exitByBackKey() {
        final boolean a = ((MyApplication) this.getApplication()).getEarlyBluetoothState();
        final boolean b = ((MyApplication) this.getApplication()).getEarlyWifiState();
        SpannableString exit = new SpannableString("Do you want to exit application?");
        exit.setSpan(new TypefaceSpan(this,"Classic Robot Condensed.ttf"), 0, exit.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        new AlertDialog.Builder(this)
                .setMessage(exit)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

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
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }*/

    private void showAlertDialog(){
        final boolean a = ((MyApplication) this.getApplication()).getEarlyBluetoothState();
        final boolean b = ((MyApplication) this.getApplication()).getEarlyWifiState();
        SpannableString exit = new SpannableString("Do you want to exit application?");
        exit.setSpan(new TypefaceSpan(this,"Classic Robot Condensed.ttf"), 0, exit.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(exit);

        builder .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertdialog = builder.create();
        alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Typeface c = Typeface.createFromAsset(getAssets(), "fonts/Classic Robot Condensed.ttf");
                (alertdialog.getButton(Dialog.BUTTON_POSITIVE)).setTypeface(c);
                (alertdialog.getButton(Dialog.BUTTON_NEGATIVE)).setTypeface(c);
            }
        });
        alertdialog.show();
    }

}
