package com.example.journeyapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GenerateItemsRqOperator extends Thread {
    public interface RequestOperatorListener {
        void success (Item[] items);
        void failed (int responseCode);
    }

    private GenerateItemsRqOperator.RequestOperatorListener listener;
    private int responseCode;

    public void setListener (GenerateItemsRqOperator.RequestOperatorListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        try {
            Item[] items = request();

            if (items !=null) {
                success(items);
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

    public void success(Item[] items) {
        if (listener!=null)
            listener.success(items);
    }

    private Item[]  request() throws IOException, JSONException {
        URL object = new URL("http://192.168.1.67:8000/api/items/generate");
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

    public Item[] parsingJsonObject(String response) throws JSONException {
//        JSONObject object = new JSONObject(response);
        JSONArray arr = new JSONArray(response);

        Item[] items = new Item[arr.length()];

        for (int i=0; i<arr.length(); i++){
            Item item = new Item();
            item.name=arr.getJSONObject(i).getString("item_name");
            item.weight=arr.getJSONObject(i).getDouble("weight");;
            item.count=arr.getJSONObject(i).getInt("count");;;
            items[i]=item;
        }
        return items;
    }
}
