package com.example.accelerometerandgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

//import com.example.accelerometerandgyroscope.ml.MlpExercise;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.*;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;


import android.widget.Toast;

public class neuralNetworkResults extends AppCompatActivity{

    //textviews
    TextView probabilities, btnweekly;
    ImageView exerciseImg, greatworkout;
    String probs ="";

    //animation
    Animation animpage, bttone, bttwo, btthree, ltr;


    //writer
    FileWriter writer;
    FileReader reader;
    File file;

    //From: https://www.youtube.com/watch?v=RhjBDxpAOIc&ab_channel=TensorFlow
    Interpreter tflite;

    //tag
    private static final String TAG = "NeuralNetwork results: ";

    //decimal format
    DecimalFormat decimalFormat =  new DecimalFormat("###.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neural_network_results);

        //setting up text
        probabilities = (TextView) findViewById(R.id.probabilities);
        exerciseImg = (ImageView) findViewById(R.id.workoutResult);
        greatworkout = (ImageView) findViewById(R.id.greatWorkout);
        btnweekly = (TextView) findViewById(R.id.btnbuckets);

        //load animation
        animpage = AnimationUtils.loadAnimation(this, R.anim.animpage);
        bttone = AnimationUtils.loadAnimation(this, R.anim.bttone);
        bttwo = AnimationUtils.loadAnimation(this, R.anim.bttwo);
        btthree = AnimationUtils.loadAnimation(this, R.anim.btthree);
        ltr = AnimationUtils.loadAnimation(this, R.anim.ltr);


        //pre-process tests
        //values
        //float[][] preprocessTest = {{(float) -19.6091,	(float)-19.6085,	(float)1.4533,	(float)-2.3289, (float)-3.1108, (float)1.1325}, {(float)19.6079,	(float)-19.6085,	(float)19.6085,	(float)-7.357,	(float)1.4899,	(float)9.975},
               // {(float)19.6079,	(float)-19.6085,	(float)19.6085,	(float)-7.357,	(float)1.4899,	(float)9.9751}, {(float)19.6079,	(float)18.4072,	(float)19.6085,	(float)-5.6551,(float)	0.8399,	(float)9.0988}, {(float)19.6079,	(float)-7.8961,	(float)19.6085,	(float)3.682,	(float)2.1634,	(float)-1.3134}};


        greatworkout.startAnimation(bttone);
        btnweekly.startAnimation(btthree);
        probabilities.startAnimation(bttone);
        exerciseImg.startAnimation(bttone);

        String result = neuralNetwork.finalExercisePrediction;
        float prob;


        if (result.equals("Jumping jacks")){
            prob = neuralNetwork.jumpingJacksProbtot;
            exerciseImg.setImageResource(R.drawable.jumping);
        }
        else if (result.equals("Push ups")){
            prob = neuralNetwork.pushUpsProbtot;
            exerciseImg.setImageResource(R.drawable.pushup);
        }
        else if (result.equals("Squats")){
            prob = neuralNetwork.squatsProbtot;
            exerciseImg.setImageResource(R.drawable.squat);
        }
        else {
            prob = neuralNetwork.sitUpsProbtot;
            exerciseImg.setImageResource(R.drawable.situp);
        }


        decimalFormat.setRoundingMode(RoundingMode.DOWN);

        //using array list to averga probabilities
        float avgProbs = neuralNetwork.probAveragedResult;
        Log.d(TAG, "Average prob:  " + decimalFormat.format(avgProbs*100));


        //setting to label
        //probabilities.setText("We are "+ decimalFormat.format(prob*100) + "% sure\nyou were doing\n" + result +"!");
        probabilities.setText("We are "+ decimalFormat.format(avgProbs*100) + "% sure\nyou were doing\n" + result +"!\n" + "Top prediction was: \n"+ decimalFormat.format(neuralNetwork.maxProbFromAvgArray*100) + "%" );

        //writing to file
        Log.d(TAG, "Writing to " + getStorageDir());

        //creating date for file
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-EEEE HH:mm");
        String dateWithMins  = dateFormat.format(new Date());

        //check if a file already exsitis
        file = new File(getStorageDir(), "Exercises performed.csv");

        //try creating it and writing to it
        try {
            if(!file.exists()){
                //file does not exits
                file.createNewFile();
            }

            writer = new FileWriter(file, true);
            writer.write(String.format("%s, %s\n", result, dateWithMins));
            Log.d(TAG, "File does not exist it's new and writing to it ");

        } catch (IOException e) {
            e.printStackTrace();
        }



        //closing file
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }







    }


    private String getStorageDir() {
        Log.d(TAG, "storage directory " + this.getExternalFilesDir(null).getAbsolutePath());
        return this.getExternalFilesDir(null).getAbsolutePath();

        // SAVING TO: storage/emulated/0/Android/data/com.example.accelerometerandgyroscope/files
        // open it on device file explorer.
    }





}