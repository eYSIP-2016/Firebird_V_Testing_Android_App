package com.example.jatin.wi_bird;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SharpSensorActivity extends ActionBarActivity {

    private Toolbar toolbar;
    BluetoothAdapter mAdapter;
    WifiManager wifi;
    NavigationDrawerFragment df;
    private ArrayList<BarEntry> entries;
    int m;
    YAxis yAxis;
    private String[] dataValuesSend = {"s", "t", "u", "v", "w"};
    //for debugging
    final String TAG = "Bluebird";
    BarChart chart;
    //to store object of BtConnection
    BtConnection mBtConnection;
    //stores whether activity is bound to service or not
    private boolean mBound = false, readSensors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharp_sensor);
        chart = (BarChart) findViewById(R.id.bar_chart_sharp);
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        SpannableString s = new SpannableString("Sensor Values");
        s.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(s);

        df = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        //setDataSet();
        chart.setScaleEnabled(false);
        yAxis = chart.getAxisLeft();
        yAxis.setAxisMaxValue(1000f);
        chart.getAxisRight().setEnabled(false);
        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("");
        //chart.animateXY(1000, 1000);
        chart.invalidate();

    }

    private BarDataSet getDataSet() {
        entries = new ArrayList<>();

        entries.add(new BarEntry(0f, 0));
        entries.add(new BarEntry(0f, 1));
        entries.add(new BarEntry(0f, 2));
        entries.add(new BarEntry(0f, 3));
        entries.add(new BarEntry(0f, 4));


        BarDataSet barDataSet = new BarDataSet(entries, "Sharp Sensor Values(1-5)");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        return barDataSet;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("1");
        xAxis.add("2");
        xAxis.add("3");
        xAxis.add("4");
        xAxis.add("5");
        return xAxis;
    }

    /**
     * Function Name: onStart
     * Input: None
     * Output: binds the SensorReading fragment with the local bluetooth service
     * Logic: Calls the activity BtConnection and binds this activity with the BtConnection by calling bindService method
     * Example Call: Called automatically when the fragment becomes visible
     */
    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(this, "onStart Method called", Toast.LENGTH_SHORT).show();
        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        readSensors = true;


    }

    /**
     * Function Name: onResume
     * Input: None
     * Output: sets the screen orientation in Portrait mode and set the variable readSensors to true to start reading the value of sensors
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     */
    @Override
    public void onResume() {
        //Toast.makeText(this, "onResume Method called", Toast.LENGTH_SHORT).show();
        super.onResume();
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        readSensors = true;
    }

    /**
     * Function Name: onPause
     * Input: None
     * Output: sensor readings are stopped
     * Logic: sets the variable readSensors to false
     * Example Call: called automatically when the fragment goes in  inactive mode
     */
    @Override
    public void onPause() {
        // Toast.makeText(this, "onPause Method called", Toast.LENGTH_SHORT).show();
        super.onPause();
        readSensors = false;
    }


    /**
     * Function Name: Sharp_GP2D12_estimation
     * Input: analog value of sharp ir sensor
     * Output: distance of object from robot in unit mm
     * Logic: distance = (int)(10.00*(2799.6*(1.00/(Math.pow(value,1.1546)))))
     * Example Call: Sharp_GP2D12_estimation (int value)
     */
    int Sharp_GP2D12_estimation(int value) {
        float distance;
        int distanceInt;
        distance = (int) (10.00 * (2799.6 * (1.00 / (Math.pow(value, 1.1546)))));
        distanceInt = (int) distance;
        if (distanceInt > 800) {
            distanceInt = 800;
        }
        return distanceInt;

    }

    // Thread to read the value of sensors
    private class ReadThread implements Runnable {


        public void run() {
            // gets the sensor values till the time fragment is visible and is connected to the bluetooth service
            sharpReadValues();
        }

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
        //Toast.makeText(this, "onStop Method called", Toast.LENGTH_SHORT).show();
        // Unbind from the service
        if (mBound) {
            //stopThread();
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }

    public void sharpReadValues() {
        // stores the value read from the input stream

        // delay between two sensor values
        long time = 30;
        while (isBound() && readSensors) {
            try {

                for (int i = 0; i < 5; i++) {


                    //Toast.makeText(SharpSensorActivity.this, "sharp"+ i+ "thread", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.valueOf(i));
                    mBtConnection.sendData(dataValuesSend[i]);
                    // reads the sensors value
                    m = mBtConnection.readData();

                    final int a = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            entries.get(a).setVal(Sharp_GP2D12_estimation(m));
                        }
                    });
                    Thread.sleep(60);
                }

            } catch (InterruptedException ex) {
                Log.e(TAG, "Exception ", ex);
            } catch (Exception ex) {
                Log.e(TAG, "Exception ", ex);
            }
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
         * Output: bounds to the local service and starts reading the sensors value
         * Logic:  bound to LocalService, cast the IBinder and get LocalService instance by calling getservice method. Also starts reading
         *         sensors by calling (new ReadThread()).start() method
         * Example Call: called automatically when a connection to the Service has been established, with the IBinder of the communication channel to the Service.
         *
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BtConnection.LocalBinder binder = (BtConnection.LocalBinder) service;
            mBtConnection = binder.getService();
            mBound = true;
            new Thread(new SharpSensorActivity.ReadThread()).start();


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
     * Function Name: isBound
     * Input: None
     * Output: boolean value which represents whether teh device is connected to robot or not
     * Logic: Checks whether a bluetooth connection has been established or not by checking the value of mBtConnection object. If it is connected then mBound
     * is set to true else false
     * Example Call: isBound()
     */
    public boolean isBound() {
        if (mBtConnection != null) {
            if (mBtConnection.getStream() != null)
                mBound = true;
            else
                mBound = false;

        }
        return mBound;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home_sensor) {

            Intent i = new Intent(SharpSensorActivity.this, ControlActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}