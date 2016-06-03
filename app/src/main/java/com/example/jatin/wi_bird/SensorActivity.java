package com.example.jatin.wi_bird;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


public class SensorActivity extends ActionBarActivity {
    String conn_blue="Connect via Bluetooth",conn_wifi="Connect via Wi-fi";
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
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

        if (id == R.id.action_home2) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_net2) {
            View menuItemView = findViewById(R.id.action_net2);
            PopupMenu popupMenu = new PopupMenu(this, menuItemView);
            popupMenu.getMenu().add(0, 0, 0, conn_blue);
            popupMenu.getMenu().add(0, 1, 1, conn_wifi);


            popupMenu.show();
            // ...
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
