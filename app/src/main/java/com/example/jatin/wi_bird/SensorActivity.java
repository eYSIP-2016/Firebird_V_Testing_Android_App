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
import android.text.Spannable;
import android.text.SpannableString;
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


    String[] SensorTextView={"Sharp Sensor 1","Sharp Sensor 2","Sharp Sensor 3","Sharp Sensor 4","Sharp Sensor 5",
            "Proximity Sensor 1","Proximity Sensor 2",
            "Proximity Sensor 3","Proximity Sensor 4","Proximity Sensor 5","Proximity Sensor 6","Proximity Sensor 7",
            "Proximity Sensor 8","White Line Sensor 1","White Line Sensor 2","White Line Sensor 3"};

    TextView[] t = new TextView[16];
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


        t[0] = (TextView)findViewById(R.id.proximity_sensor_value1);
        t[1] = (TextView)findViewById(R.id.proximity_sensor_value2);
        t[2] = (TextView)findViewById(R.id.proximity_sensor_value3);
        t[3] = (TextView)findViewById(R.id.proximity_sensor_value4);
        t[4] = (TextView)findViewById(R.id.proximity_sensor_value5);
        t[5] = (TextView)findViewById(R.id.proximity_sensor_value6);
        t[6] = (TextView)findViewById(R.id.proximity_sensor_value7);
        t[7] = (TextView)findViewById(R.id.proximity_sensor_value8);
        t[8] = (TextView)findViewById(R.id.white_sensor_value1);
        t[9] = (TextView)findViewById(R.id.white_sensor_value2);
        t[10] = (TextView)findViewById(R.id.white_sensor_value3);
        t[11] = (TextView)findViewById(R.id.sharp_sensor_value1);
        t[12] = (TextView)findViewById(R.id.sharp_sensor_value2);
        t[13] = (TextView)findViewById(R.id.sharp_sensor_value3);
        t[14] = (TextView)findViewById(R.id.sharp_sensor_value4);
        t[15] = (TextView)findViewById(R.id.sharp_sensor_value5);


        //listView = (ListView) findViewById(R.id.list);
        SpannableString s = new SpannableString("Sensor Values");
        s.setSpan(new TypefaceSpan(this,"Classic Robot Condensed.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(s);

        si = new ArrayList<SensorItem>();
        /*for(int i=0; i<16;i++)
        {

            SensorItem s=new SensorItem(sName[i],sValue[i],imgid[i]);
            si.add(s);
        }
        array = new SensorItem[16];*/



        //SensorAdapter adapter=new SensorAdapter(this,R.layout.listview_each_item,si.toArray(array));
        //listView.setAdapter(adapter);

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
        readSensors = true;


    }

    /**
     *
     * Function Name: onResume
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
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        readSensors = true;
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
        long time= 60;

        public void run()
        {
            // gets the sensor values till the time fragment is visible and is connected to the bluetooth service
            while (isBound() && readSensors)
            {
                try
                {
                    for(int i=0;i<16;i++)
                    {
                        Thread.sleep(time);
                        mBtConnection.sendData(dataValuesSend[i]);
                        // reads the sensors value
                        m = mBtConnection.readData();
                        //Log.d(TAG, "Sensors readThread ir1" + m);
                        //final int finalI = i;

                        final int a=i;
                       /* runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //t[a].setText(t[a].getText().toString().trim()+" "+String.valueOf(m)+" mm");
                                t[a].setText(String.valueOf(m));
                                Log.d(TAG, "Sensors readThread ir1 update");
                            }
                        });*/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                t[a].setText(SensorTextView[a]+": "+String.valueOf(m));
                            }
                        });
                        Thread.sleep(time);
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
            stopThread();
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
    public void stopThread(){
        if(Thread.currentThread() != null)
        {

            Thread.currentThread().interrupt();
            mBound = false;

        }
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
            //Intent intent = new Intent(SensorActivity.this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.setClassName(this,"com.example.jatin.wi_bird.MainActivity");
            //startActivity(intent);
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
