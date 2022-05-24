package com.mobile.wifi_indoorpositioning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.HttpResponse;
import com.mobile.wifi_indoorpositioning.Post;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private Button sendButton;
    private EditText locationText;
    private Button request;

    private WifiManager wifiManager;
    private List<ScanResult> scanResultList;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    private String URL = "http://server.chromato99.com/add";
    private String location;
    private String Wmac;
    private int Wrss;
    private String StrWrss;
    private String WLocation;

    JSONArray WjsonArray = new JSONArray();
    JSONObject WjsonParam = new JSONObject();
    JSONObject resultObj = new JSONObject();

    List<String> list = new ArrayList<String>();
    JSONObject obj = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = findViewById(R.id.scanBtn);
        request = findViewById(R.id.requestPosition);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL = "http://server.chromato99.com/add";
                locationText = findViewById(R.id.EditLocation);
                WLocation = locationText.getText().toString();
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                scanWifi();
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL = "http://server.chromato99.com/findPosition";
                locationText = findViewById(R.id.EditLocation);
                WLocation = locationText.getText().toString();
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                scanWifi();
            }
        });

    }

    private void scanWifi() {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanResultList = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : scanResultList) {

                Wmac = scanResult.BSSID;
                Wrss = scanResult.level;
                StrWrss = String.valueOf(Wrss);
                JSONArray array = new JSONArray();

                try {
                    resultObj.put("position", WLocation);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i<scanResultList.size(); i++){
                    try {
                        WjsonParam.put("mac",Wmac);
                        WjsonParam.put("rss",StrWrss);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(WjsonParam);
                }
                try {
                    resultObj.put("wifi_data",array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            new Thread() {
                public void run() {
                    String result="Fail";
                    Post post = new Post();
                    result = post.POST(URL,resultObj);
                    Log.d("test12", "Result : " + result);
                }
            }.start();

        }
    };
}