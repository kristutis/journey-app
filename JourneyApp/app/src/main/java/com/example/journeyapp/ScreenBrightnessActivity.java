package com.example.journeyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class ScreenBrightnessActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    int maxBrightness =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_brightness);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(ScreenBrightnessActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView brightnessTextView = (TextView) findViewById(R.id.brightnessTextView);
        if (event.values[1]>9.6) {
//            Log.e("position: ", "vertical");
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 1F;
            getWindow().setAttributes(layout);
            brightnessTextView.setText("Position: vertical\ny = "+String.valueOf(event.values[1])+"\nz = "+String.valueOf(event.values[2])+"\nScreen brightness (float): "+String.valueOf(layout.screenBrightness));
        }

        if (event.values[2]>9.6) {
//            Log.e("position: ","Phone is lying on the back");
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 0;
            getWindow().setAttributes(layout);
            brightnessTextView.setText("Position: flat on the back\ny = "+String.valueOf(event.values[1])+"\nz = "+String.valueOf(event.values[2])+"\nScreen brightness (float): "+String.valueOf(layout.screenBrightness));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (senAccelerometer !=null) {
            senSensorManager.unregisterListener(ScreenBrightnessActivity.this, senAccelerometer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (senAccelerometer != null) {
            senSensorManager.registerListener(ScreenBrightnessActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}