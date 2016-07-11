/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: DeviceListActivity.java
 * Functions: onCreate(Bundle)
 * Global Variables: EXTRA_DEVICE_ADDRESS, mBtAdapter, mNewDevicesArrayAdapter
 */
package com.example.jatin.wi_bird;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Set;
/*
 * Class Name: DeviceListActivity
 * Logic: This activity creates the list of all the paired bluetooth devices
 * Example Call: new DeviceListActivity()
 */

public class DeviceListActivity extends ActionBarActivity {
    //return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    //holds the BluetoothAdapter object
    private BluetoothAdapter mBtAdapter;
    //to hold the SpannableString object
    SpannableString pb;
    //to hold the ProgressDialog object
    ProgressDialog progress;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

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
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);
        //creating and assigning the ProgressDialog object
        progress = new ProgressDialog(DeviceListActivity.this);
        pb = new SpannableString(" Connecting... ");
        pb.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, pb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        ArrayAdapter<String> pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }
    /**
     * Function Name: onPause
     * Input: None
     * Output: dismisses the progressbar
     * Logic: calls the method progress.dismiss()
     * Example Call: called automatically when the activity goes in  inactive mode
     */
    @Override
    public void onPause() {
        super.onPause();
        progress.dismiss();
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                   progress.setMessage(pb);
                   progress.show();
                }
            });

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
}
