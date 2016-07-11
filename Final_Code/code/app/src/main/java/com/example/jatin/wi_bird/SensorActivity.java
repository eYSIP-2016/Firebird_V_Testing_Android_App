/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: SensorActivity.java
 * Functions: onCreate(Bundle), onCreateOptionsMenu(Menu), onOptionsItemSelected(MenuItem)
 * Global Variables: toolbar, prox, sharp, white, df, i
 */
package com.example.jatin.wi_bird;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
/*
 *
 * Class Name: SensorActivity
 * Logic: creates three buttons to navigate to the SharpSensorActivity, ProximitySensorActivity or to WhiteLineSensorActivity
 * Example Call: new SensorActivity()
 *
 */

public class SensorActivity extends ActionBarActivity {

    //to hold the toolbar object
    private Toolbar toolbar;
    //to bind to the UI button component
    Button prox, sharp, white;
    //to hold the NavigationDrawerFragment object
    NavigationDrawerFragment df;
    //to store the Intent object to intent to the other activity
    Intent i;
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
        setContentView(R.layout.activity_sensor);
        Log.d("Wi-bird", "Checking...");
        //binds to the different UI components
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        prox = (Button) findViewById(R.id.button_prox);
        sharp = (Button) findViewById(R.id.button_sharp);
        white = (Button) findViewById(R.id.button_white);
        //sets the font of the ActionBAr title
        SpannableString s = new SpannableString("Sensor Values");
        s.setSpan(new TypefaceSpan(this, "Classic Robot Condensed.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //sets the font to the text on Button
        Typeface c = Typeface.createFromAsset(getAssets(), "fonts/Classic Robot Condensed.ttf");
        sharp.setTypeface(c);
        prox.setTypeface(c);
        white.setTypeface(c);
        //sets the ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(s);
        //sets the navigation drawer
        df = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        //intents to ProximitySensorActivity on clicking this button
        prox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(SensorActivity.this, ProximitySensorActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        //intents to SharpSensorActivity on clicking this button
        sharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(SensorActivity.this, SharpSensorActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        //intents to WhiteLineSensorActivity on clicking this button
        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(SensorActivity.this, WhiteLineSensorActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
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
        //intents to Control Activity on clicking the home icon in Action Bar
        if (id == R.id.action_home_sensor) {

            Intent i = new Intent(SensorActivity.this, ControlActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
