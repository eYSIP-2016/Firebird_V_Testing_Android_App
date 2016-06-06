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
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class SensorActivity extends ActionBarActivity {
    String conn_blue = "Connect via Bluetooth", conn_wifi = "Connect via Wi-fi", dis = "Disconnect";
    private Toolbar toolbar;
    BluetoothAdapter mAdapter;
    WifiManager wifi;
    ListView listView;
    List<SensorItem> si;
    String[] dataValuesSend={"h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w"};
    //for debugging
    final String TAG = "Wi-bird";

    String[] sName={"IR Proximity 1","IR Proximity 2","IR Proximity 3",
            "IR Proximity 4","IR Proximity 5","IR Proximity 6","IR Proximity 7","IR Proximity 8"
            ,"Left White Line Sensor",
            "Middle White Line Sensor","Right Right Line Sensor","Sharp IR 1","Sharp IR 2",
            "Sharp IR 3","Sharp IR 4","Sharp IR 5"};
    String[] sValue={"value","value","value","value","value","value","value",
            "value","value","value","value","value","value","value","value","value"};
    Integer[] imgid={R.drawable.ic_proximity_sensor,
            R.drawable.ic_proximity_sensor,
            R.drawable.ic_proximity_sensor,
            R.drawable.ic_proximity_sensor,
            R.drawable.ic_proximity_sensor,
            R.drawable.ic_proximity_sensor,
            R.drawable.ic_proximity_sensor,
            R.drawable.ic_proximity_sensor,
            R.drawable.ic_white_light_sensor,
            R.drawable.ic_white_light_sensor,
            R.drawable.ic_white_light_sensor,
            R.drawable.ic_sharp_sensor,
            R.drawable.ic_sharp_sensor,
            R.drawable.ic_sharp_sensor,
            R.drawable.ic_sharp_sensor,
            R.drawable.ic_sharp_sensor};

    //to store object of BtConnection
    BtConnection mBtConnection;

    //stores whether activity is bound to service or not
    boolean mBound = false, readSensors = false;

    SensorItem[] array;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Log.d("Wi-bird", "Checking...");
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        listView = (ListView) findViewById(R.id.list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        si = new ArrayList<SensorItem>();
        for(int i=0; i<16;i++)
        {

            SensorItem s=new SensorItem(sName[i],sValue[i],imgid[i]);
            si.add(s);
        }
        array = new SensorItem[16];



        SensorAdapter adapter=new SensorAdapter(this,R.layout.listview_each_item,si.toArray(array));
        listView.setAdapter(adapter);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mAdapter.isEnabled());
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ((MyApplication) this.getApplication()).setEarlyWifiState(wifi.isWifiEnabled());
    }

    /**
     *
     * Function Name: onStart
     * Input: None
     * Output: binds the SensorReading fragment with the local bluetooth service
     * Logic: Calls the activity BtConnection and binds this activity with the BtConnection by calling bindService method
     * Example Call: Called automatically when the fragment becomes visible
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);



    }

    /**
     *
     * Function Name: onResumne
     * Input: None
     * Output: sets the screen orientation in Portrait mode and set the variable readSensors to true to start reading the value of sensors
     * Logic: calls the setRequestedOrientation(Orientation) method to set the screen orientation
     * Example Call: called automatically when the fragment becomes visible and user can interact with it
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        readSensors = true;
    }

    /**
     *
     * Function Name: onPause
     * Input: None
     * Output: sensor readings are stopped
     * Logic: sets the variable readSensors to false
     * Example Call: called automatically when the fragment goes in  inactive mode
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        readSensors = false;
    }

    /**
     *
     * Function Name: Sharp_GP2D12_estimation
     * Input: analog value of sharp ir sensor
     * Output: distance of object from robot in unit mm
     * Logic: distance = (int)(10.00*(2799.6*(1.00/(Math.pow(value,1.1546)))))
     * Example Call: Sharp_GP2D12_estimation (int value)
     *
     */
    int Sharp_GP2D12_estimation (int value){
        float distance;
        int distanceInt;
        distance = (int)(10.00*(2799.6*(1.00/(Math.pow(value,1.1546)))));
        distanceInt = (int)distance;
        if(distanceInt>800)
        {
            distanceInt=800;
        }
        return distanceInt;

    }

    // Thread to read the value of sensors
    private class ReadThread implements Runnable
    {
        // stores the value read from the input stream
        int m;
        // delay between two sensor values
        long t= 60;

        public void run()
        {
            // gets the sensor values till the time fragment is visible and is connected to the bluetooth service
            while (isBound() && readSensors)
            {
                try
                {
                    for(int i=0;i<16;i++)
                    {
                        Thread.sleep(t);
                        mBtConnection.sendData(dataValuesSend[i]);
                        // reads the sensors value
                        m = mBtConnection.readData();
                        Log.d(TAG, "Sensors readTghread ir1" + m);
                        //final int finalI = i;
                        array[i].sensor_value=String.valueOf(m);
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.

                                Log.d(TAG, "Sensors readTghread ir1 update");
                            }
                        });*/
                        Thread.sleep(t);
                    }


                } catch (InterruptedException ex) {
                    Log.e(TAG, "Exception ", ex);
                }
                catch (Exception ex)
                { Log.e(TAG, "Exception ", ex);
                }
            }
        }
    }

    /**
     *
     * Function Name: onStop
     * Input: None
     * Output: Unbinds this fragment from the bluetooth service
     * Logic: calls the method unbindService(BtConnection) to unbind from the bluetooth service
     * Example Call: called automatically when the fragment is not visible
     *
     */
    @Override
    public void onStop()
    {
        super.onStop();

        // Unbind from the service
        if (mBound)
        {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection()
    {

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
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BtConnection.LocalBinder binder = (BtConnection.LocalBinder) service;
            mBtConnection = binder.getService();
            mBound = true;
            new Thread(new ReadThread()).start();


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
     *
     * Function Name: isBound
     * Input: None
     * Output: boolean value which represents whether teh device is connected to robot or not
     * Logic: Checks whether a bluetooth connection has been established or not by checking the value of mBtConnection object. If it is connected then mBound
     *        is set to true else false
     * Example Call: isBound()
     *
     */
    public boolean isBound()
    {
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

        /*if (id == R.id.action_home2) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }*/
        if(id==android.R.id.home)
        {
            Intent intent = new Intent(SensorActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
