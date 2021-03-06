package com.example.wifi_positining;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText serverAddressInput;
    private EditText positionInput;
    private String positionText;
    private TextView resultText;
    private Button addDatasetBtn;
    private Button findPositionBtn;

    private WifiManager wifiManager;

    private String serverAddress;
    private String URL;

    JSONObject one_wifi_json = new JSONObject();
    JSONObject result_json = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverAddressInput = findViewById(R.id.serverAddressInput);
        addDatasetBtn = findViewById(R.id.addDatasetBtn);
        findPositionBtn = findViewById(R.id.findPositionBtn);
        positionInput = findViewById(R.id.positionInput);
        resultText = findViewById(R.id.resultText);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission","checkSelfPermission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d("permission","shouldShowRequestPermissionRationale");
                // ??????????????? ????????? ???????????????.
                // ?????? ????????? ?????? ???????????????.
            } else {
                // ????????????
                Log.d("permission","?????? ??????");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE},
                        1000);
            }
        }


        addDatasetBtn.setOnClickListener(view -> {
            serverAddress = serverAddressInput.getText().toString();
            positionText = positionInput.getText().toString();

            if (serverAddress.equals("") || positionText.equals("")) {
                resultText.setText("Please input server address and position");
            } else {
                URL = serverAddress + "/add";
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                scanWifi();
                addDatasetBtn.setEnabled(false);
                findPositionBtn.setEnabled(false);
            }
        });

        findPositionBtn.setOnClickListener(v -> {
            serverAddress = serverAddressInput.getText().toString();
            positionText = "";
            if (serverAddress.equals("")) {
                resultText.setText("Please input server address");
            } else {
                URL = serverAddress + "/findPosition";
                Log.d("test12", URL);
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                scanWifi();
                addDatasetBtn.setEnabled(false);
                findPositionBtn.setEnabled(false);
            }
        });

    }

    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            unregisterReceiver(this);
            // scan result ??????
            scanResultList.sort((s1, s2) -> s2.level - s1.level);


            TextView logTextView = findViewById(R.id.app_log);
            String scanLog = "";
            for (ScanResult scanResult : scanResultList) {
                scanLog += "BSSID: " + scanResult.BSSID + "  level: " + scanResult.level + "\n";
            }
            logTextView.setText(scanLog);

            // ????????? ?????? JSON ?????? ??????
            try {
                result_json.put("position", positionText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray json_array = new JSONArray();
            for (ScanResult scanResult : scanResultList) {
                one_wifi_json = new JSONObject();
                String bssid = scanResult.BSSID;
                int rssi = scanResult.level;

                try {
                    one_wifi_json.put("bssid", bssid);
                    one_wifi_json.put("rssi", rssi);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json_array.put(one_wifi_json);
            }
            try {
                result_json.put("wifi_data", json_array);

                EditText passwordText = findViewById(R.id.passwordInput);
                result_json.put("password", passwordText.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // ????????? ???????????? ??????
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String mRequestBody = result_json.toString(); // json ??? ???????????? ??????????????? ???????????? ???????????? ??????

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                    Log.i("test12", response);
                    resultText.setText(response); // ?????? ??????????????? ??????
                    addDatasetBtn.setEnabled(true);
                    findPositionBtn.setEnabled(true);
                }, error -> {
                    Log.e("test12", error.toString());
                    resultText.setText("Connection error! Check server address!\nExample : https://example.com");
                    addDatasetBtn.setEnabled(true);
                    findPositionBtn.setEnabled(true);
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() { // ?????? ?????? ???????????? ???????????? ??????
                        return mRequestBody.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) { // onResponse ??? ????????? ????????? ???????????? ??????
                        String responseString = "";
                        if (response != null) {
                            responseString = new String(response.data, StandardCharsets.UTF_8); // ?????? ???????????? ??????????????? ??????
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                requestQueue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}