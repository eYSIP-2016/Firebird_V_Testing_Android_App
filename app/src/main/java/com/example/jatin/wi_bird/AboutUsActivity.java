/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: AboutUsActivity.java
 * Functions: onCreate(Bundle), onStart(), onResume(), onStop(),onServiceConnected(ComponentName, IBinder),
 *            onServiceDisconnected(ComponentName), onCreateOptionsMenu(Menu), onOptionsItemSelected(MenuItem)
 *
 * Global Variables: toolbar, df, mBtConnection, mBound, activity
 *
 */

package com.example.jatin.wi_bird;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
/*
 *
 * Class Name: AboutUsActivity
 * Logic: This activity tells about the e-YSIP
 * Example Call: new AboutUsActivity()
 *
 */

public class AboutUsActivity extends ActionBarActivity {

    //to hold Toolbar object
    private Toolbar toolbar;
    //to hold NavigationDrawerFragment object
    NavigationDrawerFragment df;
    //to store obejct of btconnection
    BtConnection mBtConnection;
    //check if bounded to service or not
    boolean mBound = false;
    //hold the reference of the current activity
    public static Activity activity;

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
        setContentView(R.layout.activity_main_appbar);
        //holding current object reference
        activity = this;
        //set the font of Toolbar title
        SpannableString s = new SpannableString("About Us");
        s.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(s);

        // setting up navigation drawer
        df = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


    }

    /**
     * Function Name: onStart
     * Input: None
     * Output: binds the activity with the local bluetooth service
     * Logic: After bluetooth has been enabled, binds to the local service by calling its bindService method.
     * Example Call: Called automatically when the activity becomes visible
     */

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, BtConnection.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    /**
     *
     * Function Name: onResume
     * Input: None
     * Output: binds to the local service
     * Logic: After bluetooth has been enabled, binds to the local service by calling its bindService method
     * Example Call: called automatically when the activity becomes visible and user can interact with it
     *
     */
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
     * Output: Unbinds this activity from the bluetooth service
     * Logic: calls the method unbindService(BtConnection) to unbind from the bluetooth service
     * Example Call: called automatically when the activity is not visible
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = true;
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /*
         * Function Name: onServiceConnected
         * Input: name -->	The concrete component name of the service that has been connected.
         *        service --> The IBinder of the Service's communication channel, which you can now make calls on.
         * Output: bounds to the local service
         * Logic:  bound to LocalService, cast the IBinder and get LocalService instance by calling getService method
         * Example Call: called automatically when a connection to the Service has been established, with the IBinder of the communication channel to the Service.
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BtConnection.LocalBinder binder = (BtConnection.LocalBinder) service;
            mBtConnection = binder.getService();
            mBound = true;
        }

        /**
         *
         * Function Name: onServiceDisconnected
         * Input: name --> The concrete component name of the service whose connection has been lost.
         * Output: sets the variable mBound to false to indicate that activity is not connected to the service
         * Logic: Called when a connection to the Service has been lost. This typically happens when the process hosting the service has crashed or been killed.
         * This does not remove the ServiceConnection itself -- this binding to the service will remain active, and you will receive a call to
         * onServiceConnected(ComponentName, IBinder) when the Service is next running.
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
        getMenuInflater().inflate(R.menu.menu_aboutus, menu);
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

        if (id == R.id.action_home_aboutus) {
            Intent i = new Intent(AboutUsActivity.this, ControlActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
