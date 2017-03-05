package com.example.ruslan.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiScanReceiver wifiReciever;
    WifiInfo wifiInfo;
    ArrayList<String> wifis;
    ArrayAdapter<String> adapter;

    final int WIFI_ACCESS_PERMISSION = 11231;
    ListView list;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.text);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        wifiInfo = wifiManager.getConnectionInfo();

        wifis = new ArrayList<>();
        wifis.add("loading...");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);

        requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE}, WIFI_ACCESS_PERMISSION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WIFI_ACCESS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    wifiManager.startScan(); //make sure this is the last call

                } else {
                    Toast.makeText(getApplicationContext(), "No Permissions", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();

            wifis.clear();

            for (int i = 0; i < wifiScanList.size(); i++) {
                String ssid = wifiScanList.get(i).SSID;
                String bssid =  wifiScanList.get(i).BSSID;
                wifis.add(ssid + " " + bssid + " " +((wifiScanList.get(i)).toString()) ); //append to the other data
            }

            adapter.notifyDataSetChanged();
            wifiManager.startScan();
        }
    }

    @Override
    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
}
