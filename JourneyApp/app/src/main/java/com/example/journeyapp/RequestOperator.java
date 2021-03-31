package com.example.journeyapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestOperator extends Thread {
    public interface RequestOperatorListener {
        void success (String[] journeys);
        void failed (int responseCode);
    }

    private RequestOperatorListener listener;
    private int responseCode;

    public void setListener (RequestOperatorListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        try {
            String[] journeys = request();

            if (journeys !=null) {
                success(journeys);
            } else {
                failed(responseCode);
            }
        } catch (IOException | JSONException e) {
            failed(-2);
        }
    }

    public void failed(int code) {
        if (listener!=null)
            listener.failed(code);
    }

    public void success(String[] journeys) {
        if (listener!=null)
            listener.success(journeys);
    }

    private String[] request() throws IOException, JSONException {
        URL object = new URL("http://192.168.1.67:8000/api/journeys");
        HttpURLConnection connection = (HttpURLConnection) object.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        responseCode = connection.getResponseCode();
        Log.i("Response Code", String.valueOf(responseCode));

        InputStreamReader inputStreamReader;
        if (responseCode==200) {
            inputStreamReader = new InputStreamReader(connection.getInputStream());
        } else {
            inputStreamReader = new InputStreamReader(connection.getErrorStream());
        }

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String inputLine;
        StringBuffer responseStringBuffer = new StringBuffer();

        while ((inputLine=bufferedReader.readLine())!=null) {
            responseStringBuffer.append(inputLine);
        }

        bufferedReader.close();

        Log.i("Response Result", responseStringBuffer.toString());

        if (responseCode==200) {
            return parsingJsonObject(responseStringBuffer.toString());
        } else {
            return null;
        }
    }

    public String[] parsingJsonObject(String response) throws JSONException {
//        JSONObject object = new JSONObject(response);
        JSONArray arr = new JSONArray(response);

        String[] journeys = new String[arr.length()];

        for (int i=0; i<arr.length(); i++){
            journeys[i]=arr.getJSONObject(i).getString("journey_name");
        }
//        String[] journey = object.getSt("journey_name");
        return journeys;
    }
}
