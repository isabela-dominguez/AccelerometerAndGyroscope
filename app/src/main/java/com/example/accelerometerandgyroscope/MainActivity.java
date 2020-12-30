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

    //sensors
    private Sensor accelerometer, mGyro;

    //Defining text variables
    TextView xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting refrerences for texts
        //accelerometer
        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);

        //gyroscope
        xGyroValue = (TextView) findViewById(R.id.xGyroValue);
        yGyroValue = (TextView) findViewById(R.id.yGyroValue);
        zGyroValue = (TextView) findViewById(R.id.zGyroValue);


        //getting log on console
        Log.d(TAG, "onCreate: Init sensor services");

        //sensor mangements and setting sensor type
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //checking if sensors are available in device
        if(accelerometer != null){
            sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            //logging on terminal
            Log.d(TAG, "onCreate: registerd accelerometer listener");
        }else {
            xValue.setText("Accelerometer not found");

        }

        if(mGyro != null){
            sensorManager.registerListener((SensorEventListener) this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            //logging on terminal
            Log.d(TAG, "onCreate: registered gyroscope listener");
        }else {
            xGyroValue.setText("Gyroscope not found");
        }


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //read accelerometer values
            Log.d(TAG, "onSensorChanged: x: " + sensorEvent.values[0] + "   Y:" + sensorEvent.values[1] + "    z:" + sensorEvent.values[2]);

            xValue.setText("xValue: " + sensorEvent.values[0]);
            yValue.setText("yValue: " + sensorEvent.values[1]);
            zValue.setText("zValue: " + sensorEvent.values[2]);
        }
        else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
            //read accelerometer values
            Log.d(TAG, "onSensorChanged: x: " + sensorEvent.values[0] + "   Y:" + sensorEvent.values[1] + "    z:" + sensorEvent.values[2]);

            xGyroValue.setText("xGyroValue: " + sensorEvent.values[0]);
            yGyroValue.setText("yGyroValue: " + sensorEvent.values[1]);
            zGyroValue.setText("zGyroValue: " + sensorEvent.values[2]);
        }




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}