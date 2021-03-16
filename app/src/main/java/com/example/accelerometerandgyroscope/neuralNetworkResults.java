package com.example.accelerometerandgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.util.Log;
import android.widget.TextView;

//import com.example.accelerometerandgyroscope.ml.MlpExercise;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.*;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;


import android.widget.Toast;

public class neuralNetworkResults extends AppCompatActivity{

    //textviews
    TextView results, probabilities;
    String probs ="";

    //From: https://www.youtube.com/watch?v=RhjBDxpAOIc&ab_channel=TensorFlow
    Interpreter tflite;

    //tag
    private static final String TAG = "NeuralNetwork: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neural_network_results);

        //setting up text
        results = (TextView) findViewById(R.id.results);
        probabilities = (TextView) findViewById(R.id.probabilities);


        //pre-process tests
        //values
        float[][] preprocessTest = {{(float) -19.6091,	(float)-19.6085,	(float)1.4533,	(float)-2.3289, (float)-3.1108, (float)1.1325}, {(float)19.6079,	(float)-19.6085,	(float)19.6085,	(float)-7.357,	(float)1.4899,	(float)9.975},
                {(float)19.6079,	(float)-19.6085,	(float)19.6085,	(float)-7.357,	(float)1.4899,	(float)9.9751}, {(float)19.6079,	(float)18.4072,	(float)19.6085,	(float)-5.6551,(float)	0.8399,	(float)9.0988}, {(float)19.6079,	(float)-7.8961,	(float)19.6085,	(float)3.682,	(float)2.1634,	(float)-1.3134}};

        float[] numbersTestInput = new float[16];
        numbersTestInput[0] = (float) 0.417475728;
        numbersTestInput[1] = (float) 0.4875;
        numbersTestInput[2] = (float) 0.463414634;
        numbersTestInput[3] = (float) 0.857142857;
        numbersTestInput[4] = (float) 0.363636364;
        numbersTestInput[5] = (float) 0.727272727;
        numbersTestInput[6] = (float) 0.515625;
        numbersTestInput[7] = (float) 0.416666667;
        numbersTestInput[8] = (float) 0.411764706;
        numbersTestInput[9] = (float) 0.212121212;
        numbersTestInput[10] = (float) 0.666666667;
        numbersTestInput[11] = (float) 0.25;
        numbersTestInput[12] = (float) 0.444444444;
        numbersTestInput[13] = (float) 0.178815956;
        numbersTestInput[14] = (float) 0.241270137;
        numbersTestInput[15] = (float) 0.218049168;

//        Log.d(TAG, "Pre process tests: " + Arrays.deepToString(preprocessTest) );
//        preprocess test1 = new preprocess(preprocessTest);
//        float[] featuresResultTest = test1.getFeatures();
//        float[] featuresResultTest1 = test1.features;
//        for(int i = 0; i < featuresResultTest1.length; i++){
//            Log.d(TAG, "array at   " + i + "   " + featuresResultTest1[i]);
//        }

        //testing neural network functiosns from class
        neuralNetwork net = new neuralNetwork(this);
        net.predict(numbersTestInput);
        Log.d(TAG, "hash map with the new class:  " + net.getMap());

        //setting to label
        probabilities.setText("Push ups: " + net.getMap().get("Push ups") + " Jumping jacks: " +net.getMap().get("Jumping jacks") + " Sit ups: " +net.getMap().get("Sit ups") + " Squats: " +net.getMap().get("Squats"));

        //checking which item has highest probability
        float max = Collections.max(net.mappedProbabilities.values());

        for (Map.Entry<String, Float> entry : net.mappedProbabilities.entrySet()){
            if(entry.getValue()==max){
                results.setText("Result: " + entry.getKey());
            }

        }



    }







}