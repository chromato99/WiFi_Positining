package com.example.wifi_positining;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.List;

public class Post {

    public Post(){}

    public String POST(String urlAddress, JSONObject data) {
        Log.d("test12", "POST start");
        String result = null;
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            Log.d("test12", "POST start");
            InputStream is = null;
            result = "";

            conn.connect();
            Log.d("test12", data.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(data.toString());
            os.flush();
            os.close();

//            conn.connect();
            Log.d("test12", String.valueOf(conn.getResponseCode()));
            Log.d("test12", conn.getResponseMessage());

            try {
                is = conn.getInputStream();
                if (is != null) {
                    Log.d("test12", "IS not null");
                    result = convertInputStreamToString(is);
                }else
                    result = "No Data";
                    Log.d("test12", "No Data");
                if(result == null){
                    result = "IT's NULL";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream is) {
        String receiveMSG = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = rd.readLine()) != null) {
                buffer.append(str);
            }
            receiveMSG = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMSG;
    }
}