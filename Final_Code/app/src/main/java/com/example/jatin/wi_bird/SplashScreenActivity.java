package com.example.jatin.wi_bird;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 *
 * Class Name: SplashScreen
 * Logic: shows the starting screen of the app
 * Example Call: new SplashScreen()
 *
 */
public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2500;
    BluetoothAdapter mAdapter;
    //Animation
    Animation animFadeIn;
    WifiManager wifi;
    TextView title;

    /**
     *
     * Function Name: onCreate
     * Input: savedInstanceState --> If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently
     *        supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * Output: sets up the splash screen
     * Logic: Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate the activity's UI,
     *        using findViewById(int) to programmatically interact with widgets in the UI
     * Example Call: Called automatically when the activity is created
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        title = (TextView) findViewById(R.id.title);

        // load the animation
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        // start the animation

        title.startAnimation(animFadeIn);
        new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, ControlActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mAdapter.isEnabled());

    }

}

