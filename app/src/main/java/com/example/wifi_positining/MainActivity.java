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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText locationText;
    private TextView locationResult;

    private WifiManager wifiManager;

    private String URL = "http://server.chromato99.com/add";
    private String WLocation;

    JSONObject one_wifi_json = new JSONObject();
    JSONObject resultObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_location);

        Button scanButton = findViewById(R.id.scanBtn);
        Button requestPosition = findViewById(R.id.requestPosition);
        locationResult = findViewById(R.id.LocationResult);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission","checkSelfPermission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d("permission","shouldShowRequestPermissionRationale");
                // 사용자에게 설명을 보여줍니다.
                // 권한 요청을 다시 시도합니다.
            } else {
                // 권한요청
                Log.d("permission","권한 요청");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE},
                        1000);
            }
        }


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL = "http://server.chromato99.com/add";
                locationText = findViewById(R.id.positionInput);
                WLocation = locationText.getText().toString();
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                scanWifi();
            }
        });

        requestPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL = "http://server.chromato99.com/findPosition";
                locationText = findViewById(R.id.positionInput);
                WLocation = locationText.getText().toString();
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                scanWifi();
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
            // scan result 정렬
            scanResultList.sort((s1, s2) -> s2.level - s1.level);


            TextView logTextView = findViewById(R.id.app_log);
            String scanLog = "";
            for (ScanResult scanResult : scanResultList) {
                scanLog += "BSSID: " + scanResult.BSSID + "  level: " + scanResult.level + "\n";
            }
            logTextView.setText(scanLog);

            // 서버에 보낼 JSON설정 부분
            try {
                resultObj.put("position", WLocation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray json_array = new JSONArray();
            for (ScanResult scanResult : scanResultList) {
                one_wifi_json = new JSONObject();
                String mac = scanResult.BSSID;
                int rss = scanResult.level;
                String str_rss = String.valueOf(rss);
                try {
                    one_wifi_json.put("mac", mac);
                    one_wifi_json.put("rss", str_rss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json_array.put(one_wifi_json);
            }
            try {
                resultObj.put("wifi_data", json_array);

                EditText passwordText = findViewById(R.id.passwordInput);
                resultObj.put("password", passwordText.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 서버와 통신하는 부분
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String mRequestBody = resultObj.toString(); // json을 통신으로 보내기위해 문자열로 변환하는 부분

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("test12", response);
                        locationResult.setText(response); // 결과 출력해주는 부분
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("test12", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError { // 요청 보낼 데이터를 처리하는 부분
                        return mRequestBody == null ? null : mRequestBody.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) { // onResponse에 넘겨줄 응답을 처리하는 부분
                        String responseString = "";
                        if (response != null) {
                            responseString = new String(response.data, StandardCharsets.UTF_8); // 응답 데이터를 변환해주는 부분
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