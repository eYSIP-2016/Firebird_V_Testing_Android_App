package com.example.jatin.wi_bird;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Toast;


public class VelocityActivity extends ActionBarActivity {
    String conn_blue="Connect via Bluetooth",conn_wifi="Connect via Wi-fi",dis="Disconnect";
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
    SeekBar leftMotorVelocitySeekBar, rightMotorVelocitySeekBar;

    // Text box used to enter the velocity of both the wheels
    EditText leftMotorVelocityText, rightMotorVelocityText;

    // sets the velocity of robot
    Button setButton;

    // stores the left and right motor velocity
    int leftMotorVelocity, rightMotorVelocity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_velocity);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mAdapter.isEnabled());
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ((MyApplication) this.getApplication()).setEarlyWifiState(wifi.isWifiEnabled());
    }


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


        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
