package com.example.accelerometerandgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    Sensor accelerometer;

    TextView xValue, yValue, zValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting refrerences
        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);


        Log.d(TAG, "onCreate: Init sensor services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate: registerd accelerometer listener");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged: x: "+ sensorEvent.values[0] + "   Y:" +  sensorEvent.values[1] +"    z:" +  sensorEvent.values[2] ) ;

        xValue.setText("xValue: " + sensorEvent.values[0]);
        yValue.setText("yValue: " + sensorEvent.values[1]);
        zValue.setText("zValue: " + sensorEvent.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}