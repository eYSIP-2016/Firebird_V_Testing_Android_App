package com.example.jatin.wi_bird;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    String conn_blue = "Connect via Bluetooth", conn_wifi = "Connect via Wi-fi", dis="Disconnect";
    private Toolbar toolbar;
    NavigationDrawerFragment df;
    BluetoothAdapter mBluetoothAdapter;
    WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        df = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ((MyApplication) this.getApplication()).setEarlyBluetoothState(mBluetoothAdapter.isEnabled());
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ((MyApplication) this.getApplication()).setEarlyWifiState(wifi.isWifiEnabled());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if (id == R.id.action_net) {
            View menuItemView = findViewById(R.id.action_net);
            PopupMenu popupMenu = new PopupMenu(this, menuItemView);
            popupMenu.getMenu().add(0, 0, 0, conn_blue);
            popupMenu.getMenu().add(0, 1, 1, conn_wifi);
            popupMenu.getMenu().add(0, 2, 2, dis);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case 0: {
                            //Toast.makeText(getApplication(), "Bluetooth connection needed",Toast.LENGTH_LONG).show();
                            if(!mBluetoothAdapter.isEnabled())
                            {
                                mBluetoothAdapter.enable();
                            }
                            break;
                        }
                        case 1: {
                            //Toast.makeText(getApplication(), "Wifi connection needed",Toast.LENGTH_LONG).show();
                            if(!wifi.isWifiEnabled())
                            {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        final boolean a = ((MyApplication)this.getApplication()).getEarlyBluetoothState();
        final boolean b = ((MyApplication)this.getApplication()).getEarlyWifiState();

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        if(!a)
                        {
                           mBluetoothAdapter.disable();
                        }
                        if(!b)
                        {
                            wifi.setWifiEnabled(false);
                        }
                        finish();
                        //  close();
                   }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }

}
