package com.mobile.wifi_indoorpositioning;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;


public class Post {

    public Post(){}

    public static String POST(String urlAddress) {

        String result = null;
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            InputStream is = null;
            result = "";

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonParam = new JSONObject();

            jsonParam.put("mac", "11:11:11:11");
            jsonParam.put("rss", -49);
            jsonArray.put(jsonParam);

            Log.i("JSON", jsonArray.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());

            os.flush();
            os.close();

            try {
                is = conn.getInputStream();
                if (is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "No Data";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
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