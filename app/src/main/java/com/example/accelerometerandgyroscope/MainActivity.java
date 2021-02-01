package com.example.accelerometerandgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;

    //sensors
    private Sensor accelerometer, mGyro;

    //Defining text variables
    TextView xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue;

    //buttons
    Button buttonStart;
    Button buttonStop;

    //writer
    FileWriter writer;

    boolean isRunning;

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


        //button
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);

        isRunning = false;

        //getting log on console
        Log.d(TAG, "onCreate: Init sensor services");

        //sensor mangements and setting sensor type
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //******************************************************

        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(new File(getStorageDir(), "App_AccelerometerAndGyroscope_"  + Calendar.getInstance().getTime() + ".csv"));
                    writer.write(String.format("ACCELEROMETER X," + "ACCELEROMETER Y,"+ "ACCELEROMETER Z," + "GRYOSCOPE X," + "GRYOSCOPE Y," + "GRYOSCOPE Z," + "TIMESTAMP, \n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL); //sampling period is in microseconds. 5000 micro = 5 mili

                isRunning = true;
                return true;
            }
        });


        buttonStop.setOnTouchListener(new View.OnTouchListener() {

            @Override

            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "Button stop and closing file ");
                xValue.setText("SAVING TO FILE " );
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                sensorManager.flush(MainActivity.this);
                sensorManager.unregisterListener(MainActivity.this);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });






        //********************************************************

        //defining sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //checking if sensors are available in device
        if(accelerometer != null){
            //sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            //logging on terminal
           // Log.d(TAG, "onCreate: registerd accelerometer listener");
        }else {
            xValue.setText("Accelerometer not found");

        }

        if(mGyro != null){
           // sensorManager.registerListener((SensorEventListener) this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            //logging on terminal
            //Log.d(TAG, "onCreate: registered gyroscope listener");
        }else {
            xGyroValue.setText("Gyroscope not found");
        }


    }



    //**********************************************
    private String getStorageDir() {
        Log.d(TAG, "storage directory " + this.getExternalFilesDir(null).getAbsolutePath());
        return this.getExternalFilesDir(null).getAbsolutePath();

        // SAVING TO: storage/emulated/0/Android/data/com.example.accelerometerandgyroscope/files
        // open it on device file explorer.
    }




    //*************************************************


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String millisInString  = dateFormat.format(new Date());

        if(isRunning){

            if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //read accelerometer values
               // Log.d(TAG, "onSensorChanged: x: " + sensorEvent.values[0] + "   Y:" + sensorEvent.values[1] + "    z:" + sensorEvent.values[2]);

                xValue.setText("xValue: " + sensorEvent.values[0]);
                yValue.setText("yValue: " + sensorEvent.values[1]);
                zValue.setText("zValue: " + sensorEvent.values[2]);

                try {
                    writer.write(String.format("%d, ACC, %s,  %f, %f, %f,\n", sensorEvent.timestamp, millisInString,  sensorEvent.values[0],  sensorEvent.values[1],  sensorEvent.values[2]));
                   // writer.write(String.format("%d, ACC, %f, %f, %f,\n", sensorEvent.timestamp,  sensorEvent.values[0],  sensorEvent.values[1],  sensorEvent.values[2]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
                //read accelerometer values
                //Log.d(TAG, "onSensorChanged: x: " + sensorEvent.values[0] + "   Y:" + sensorEvent.values[1] + "    z:" + sensorEvent.values[2]);

                xGyroValue.setText("xGyroValue: " + sensorEvent.values[0]);
                yGyroValue.setText("yGyroValue: " + sensorEvent.values[1]);
                zGyroValue.setText("zGyroValue: " + sensorEvent.values[2]);

                try {
                    writer.write(String.format("%d, GYRO, %f, %f, %f,\n", sensorEvent.timestamp,  sensorEvent.values[0],  sensorEvent.values[1],  sensorEvent.values[2]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}