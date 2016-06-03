package com.example.jatin.wi_bird;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


public class SensorActivity extends ActionBarActivity {
    String conn_blue = "Connect via Bluetooth", conn_wifi = "Connect via Wi-fi", dis = "Disconnect";
    private Toolbar toolbar;
    BluetoothAdapter mBluetoothAdapter;
    WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mBluetoothAdapter.isEnabled());
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ((MyApplication) this.getApplication()).setEarlyWifiState(wifi.isWifiEnabled());
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
            popupMenu.getMenu().add(0, 2, 2, dis);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 0: {
                            //Toast.makeText(getApplication(), "Bluetooth connection needed",Toast.LENGTH_LONG).show();
                            if (!mBluetoothAdapter.isEnabled()) {
                                mBluetoothAdapter.enable();
                            }
                            break;
                        }
                        case 1: {
                            //Toast.makeText(getApplication(), "Wifi connection needed",Toast.LENGTH_LONG).show();
                            if (!wifi.isWifiEnabled()) {
                                wifi.setWifiEnabled(true);
                            }
                            break;
                        }
                        case 2: {
                            //Toast.makeText(getApplication(), "Disconnect",Toast.LENGTH_LONG).show();
                            break;
                        }
                        default:
                            //Toast.makeText(getApplication(), item.getItemId()+"",Toast.LENGTH_LONG).show();
                            break;
                    }
                    return true;
                }
            });
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
