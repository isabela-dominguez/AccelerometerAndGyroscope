package com.example.accelerometerandgyroscope;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.content.Intent;

import org.tensorflow.lite.Interpreter;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private Chronometer chronometer;
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;

    //******
    private SensorManager mSensorManager;
    private boolean mRegisteredSensor;

    //sensors
    private Sensor accelerometer, mGyro;

    //Defining text variables
    TextView xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue;

    //buttons
    Button buttonStart;
    Button buttonStop;

    //writer
    FileWriter writer;

    // Accelerometer and gyrovalue
    private float [] accelerometerValues = new float [3];
    private float [] gyroValues = new float [3];





    boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //chronometer
        chronometer = findViewById(R.id.chronometer);

        //button
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);

        isRunning = false;

        //getting log on console
        Log.d(TAG, "onCreate: Init sensor services");

        //sensor mangements and setting sensor type
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);



        accelerometerValues[0] = 0;
        accelerometerValues[1] = 0;
        accelerometerValues[2] = 0;
        gyroValues[0] = 0;
        gyroValues[1] = 0;
        gyroValues[2] = 0;

        //Chronometer init

        //******************************************************
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ArrayList<List<Sensor >>sensors = new ArrayList<List<Sensor >>();
        mRegisteredSensor = false;



        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                Log.d(TAG, "Button started ");
                chronometer.start();
                Log.d(TAG, "Chronometer started ");

                //writing to file
                Log.d(TAG, "Writing to " + getStorageDir());

                try {
                    writer = new FileWriter(new File(getStorageDir(), "App_AccelerometerAndGyroscope_"  + Calendar.getInstance().getTime() + ".csv"));
                    writer.write(String.format("ACCELEROMETER X," + "ACCELEROMETER Y,"+ "ACCELEROMETER Z," + "GRYOSCOPE X," + "GRYOSCOPE Y," + "GRYOSCOPE Z," + "TIMESTAMP, \n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
//                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI); //sampling period is in microseconds. 5000 micro = 5 mili. 0.1 seconds is 100 000 microseconds


                sensors.add (mSensorManager.getSensorList (Sensor.TYPE_ACCELEROMETER));
                sensors.add (mSensorManager.getSensorList (Sensor.TYPE_GYROSCOPE));

                for (List<Sensor>sensor: sensors) {
                    if (sensor.size ()>0) {
                        mRegisteredSensor = mSensorManager.registerListener (MainActivity.this,  sensor.get (0), SensorManager.SENSOR_DELAY_GAME);
                    }
                }

                isRunning = true;
                return true;
            }
        });


        buttonStop.setOnTouchListener(new View.OnTouchListener() {

            @Override

            public boolean onTouch(View view, MotionEvent motionEvent) {
                chronometer.stop();
                Log.d(TAG, "Chronometer started ");
                Log.d(TAG, "Button stop and closing file ");
                //xValue.setText("SAVING TO FILE " );
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


                //change to prediction results
                Intent a = new Intent(MainActivity.this, neuralNetworkResults.class);
                a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(a);




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



                Log.d(TAG, " to acc stack" );
                accelerometerValues = sensorEvent.values.clone ();

                try {
                    writer.write(String.format("%f, %f, %f, %f, %f, %f, %s\n", accelerometerValues[0], accelerometerValues[1], accelerometerValues[2], gyroValues[0],  gyroValues[1],  gyroValues[2], millisInString));
                } catch (IOException e) {
                    e.printStackTrace();
                }





            }
            else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
                //read accelerometer values
                //Log.d(TAG, "onSensorChanged: x: " + sensorEvent.values[0] + "   Y:" + sensorEvent.values[1] + "    z:" + sensorEvent.values[2]);


                Log.d(TAG, " to gyro stack" );
                gyroValues = sensorEvent.values.clone ();




            }



        }




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        {
            ArrayList<List<Sensor >>sensors = new ArrayList<List<Sensor >>();
            sensors.add (mSensorManager.getSensorList (Sensor.TYPE_ACCELEROMETER));
            sensors.add (mSensorManager.getSensorList (Sensor.TYPE_GYROSCOPE));
            for (List<Sensor>sensor: sensors) {
                if (sensor.size ()>0) {
                    mRegisteredSensor = mSensorManager.registerListener (this,
                            sensor.get (0), 100000);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRegisteredSensor) {
            mSensorManager.unregisterListener (this);mRegisteredSensor = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener (this);
    }

    //chronometer
    public void startChronometer(View v){
        if(!isRunning){
            chronometer.start();

        }

    }

    // create a new file with columns

    //means and sma


    //standard diviation

    //fast fourier

    public void featureExtraction(){

    }
}