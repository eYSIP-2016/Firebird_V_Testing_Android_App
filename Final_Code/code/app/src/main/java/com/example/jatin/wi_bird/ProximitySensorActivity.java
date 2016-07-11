/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: ProximitySensorActivity.java
 * Functions: onCreate(Bundle), onStart(), onResume(), onStop(),isBound(),getDataSet(), getXAxisValues(),
 *           onServiceConnected(ComponentName, IBinder), onServiceDisconnected(ComponentName), onCreateOptionsMenu(Menu),
 *            onOptionsItemSelected(MenuItem), proximityReadValues()
 *
 * Global Variables: toolbar, df, mBtConnection, mBound, entries, yAxis, TAG, dataValuesSend, chart, readSensors, m
 *
 */
package com.example.jatin.wi_bird;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
/*
 *
 * Class Name: ProximitySensorActivity
 * Logic: This activity gets the proximity sensor values from teh robot using background bluetooth service and sets the values in
 *        bar chart
 * Example Call: new ProximitySensorActivity()
 *
 */
public class ProximitySensorActivity extends ActionBarActivity {
    //to hold the Toolbar object
    private Toolbar toolbar;
    //to hold the NavigationDrawerFragment object
    NavigationDrawerFragment df;
    //to store the values of the proximity sensors
    private ArrayList<BarEntry> entries;
    //to store the YAxis
    YAxis yAxis;
    //stores the data obtained from robot
    int m;
    //for debugging
    final String TAG = "Bluebird";
    //holds the data send to robot to get the respective values of sensors
    private String[] dataValuesSend = {"h", "i", "j", "k", "l", "m", "n", "o"};
    //used to bind with the UI barchart component
    BarChart chart;
    //to store object of BtConnection
    BtConnection mBtConnection;
    //stores whether activity is bound to service or not
    private boolean mBound = false, readSensors = false;

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
        setContentView(R.layout.activity_proximity_sensor);
        //binds with the UI barchart component
        chart = (BarChart) findViewById(R.id.bar_chart_proximity);
        //binds with the UI toolbar component
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        //sets the font to the appbar title
        SpannableString s = new SpannableString("Sensor Values");
        s.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //sets up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(s);
        //sets up navigation drawer
        df = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        //disable the scaling of chart
        chart.setScaleEnabled(false);
        //get and stores the LeftAxis
        yAxis = chart.getAxisLeft();
        yAxis.setAxisMaxValue(300f);
        chart.getAxisRight().setEnabled(false);
        //set the initial data to the barchart(each value equal to zero)
        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("");
        chart.invalidate();

    }

    /**
     * Function Name: getDataSet
     * Input: data --> None
     * Output: returns the initial data set to barchart
     * Logic: creates the arraylist object and add the eight entries with default zero values for each entry
     * Example Call: getDataSet()
     */
    private BarDataSet getDataSet() {
        //creates the ArrayList object and stores the reference to the variable 'entries'
        entries = new ArrayList<>();
        //add the default values of the chart to be displayed
        entries.add(new BarEntry(0f, 0));
        entries.add(new BarEntry(0f, 1));
        entries.add(new BarEntry(0f, 2));
        entries.add(new BarEntry(0f, 3));
        entries.add(new BarEntry(0f, 4));
        entries.add(new BarEntry(0f, 5));
        entries.add(new BarEntry(0f, 6));
        entries.add(new BarEntry(0f, 7));

        //set the barDataSet with the tag to be shown below the bar chart
        BarDataSet barDataSet = new BarDataSet(entries, "Proximity Sensor Values(1-8)");
        //makes the barchart column colorful
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        return barDataSet;
    }
    /**
     * Function Name: getXAxisValues
     * Input: data --> None
     * Output: returns the x-axis for barchart
     * Logic: creates the arraylist object and add the eight entries
     * Example Call: getXAxisValues()
     */
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("1");
        xAxis.add("2");
        xAxis.add("3");
        xAxis.add("4");
        xAxis.add("5");
        xAxis.add("6");
        xAxis.add("7");
        xAxis.add("8");
        return xAxis;
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
        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        readSensors = true;
    }

    /**
     * Function Name: onResume
     * Input: None
     * Output: sensor reading starts
     * Logic: assigning true to readSensor variable
     * Example Call: called automatically when the activity becomes visible and user can interact with it
     */
    @Override
    public void onResume() {
        super.onResume();
        readSensors = true;
    }

    /**
     * Function Name: onPause
     * Input: None
     * Output: sensor readings are stopped
     * Logic: sets the variable readSensors to false
     * Example Call: called automatically when the activity goes in  inactive mode
     */
    @Override
    public void onPause() {
        super.onPause();
        readSensors = false;
    }

    // Thread to read the value of sensors
    private class ReadThread implements Runnable {
        public void run() {
            // gets the sensor values till the time activity is visible and is connected to the bluetooth service
            proximityReadValues();
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
        // Unbind from the service
        if (mBound) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Function Name: proximityReadValues
     * Input: None
     * Output: sends the data to the robot to get the proximity sensor values and sets the values obtained to the 'entries' ArrayList
     * Logic: sends the data till the time activity is binded to the bluetooth service and readSensors value is true
     * Example Call: proximityReadValues()
     */
    public void proximityReadValues() {
        // delay between two sensor values
        long time = 60;
        while (isBound() && readSensors) {
            try {
                        for (int i = 0; i < 8; i++) {
                        
                        //sending data
                        mBtConnection.sendData(dataValuesSend[i]);
                        // reads the sensors value
                        m = mBtConnection.readData();
                        final int a = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                // stores the value read from the input stream into the arraylist
                                entries.get(a).setVal(m);
                            }
                        });
                        Thread.sleep(time);

                }

           }  catch (InterruptedException ex) {
                Log.e(TAG, "Exception ", ex);
            }
            catch (Exception ex) {
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
            new Thread(new ProximitySensorActivity.ReadThread()).start();
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
     * Output: boolean value which represents whether the device is connected to robot or not
     * Logic: Checks whether a bluetooth connection has been established or not by checking the value of mBtConnection object. If it is connected then mBound
     *        is set to true else false
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
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
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
        //intents to Control Activity when home icon in clicked in ActionBar
        if (id == R.id.action_home_sensor) {

            Intent i = new Intent(ProximitySensorActivity.this, SensorActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
