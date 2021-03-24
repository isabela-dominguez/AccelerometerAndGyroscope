package com.example.accelerometerandgyroscope;

import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

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
import java.util.Hashtable;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;


import android.widget.Toast;

public class neuralNetwork {
    //From: https://www.youtube.com/watch?v=RhjBDxpAOIc&ab_channel=TensorFlow
    Interpreter tflite;

    //tag
    private static final String TAG = "NeuralNetwork: ";

    //output shape
    public float[][] output = new float[1][4];

    //array list with filtered probs to calculate average probability
    ArrayList<Float> probabilitiesForAveraging = new ArrayList<Float>();

    //output hahs
    //Hashtable<String, Float> mappedProbabilities = new Hashtable<String, Float>();
    public Hashtable<String, Float> mappedProbabilities = new Hashtable<>();

    //individual probability:
    public static float pushUpsProb;
    public static float jumpingJacksProb;
    public static float squatsProb;
    public static float sitUpsProb;
    public static String finalExercisePrediction;

    public static float pushUpsProbtot;
    public static float jumpingJacksProbtot;
    public static float squatsProbtot;
    public static float sitUpsProbtot;

    //prob averaged
    public static float probAveragedResult;



    //context
    private Context context;

    //counters
    int counterJump = 0;
    int counterPush =0;
    int counterSquat = 0;
    int counterSit = 0;





    public neuralNetwork(Context context, float[] input){
        this.context = context;
        initMap();

    }

    public neuralNetwork(Context context){
        this.context = context;
        initMap();
    }

    public void predict(float[] input){
        openModel();
        doInference(input);
        mapOutputToLabel();
    }

    public void averageProbabilityResults(){
        probAveragedResult = calculateAverage(getProbabilitiesForAveraging());
    }


    public MappedByteBuffer loadModelFile() throws IOException {
        neuralNetworkResults instance = new neuralNetworkResults();
        //open model suing input stream and memory map to it to load
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("mlpExercise.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLenght = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLenght);
    }


    public void doInference(float[] inputNumbers){
        openModel();
        //run inference
        tflite.run(inputNumbers, output);


    }




    public void mapOutputToLabel(){

        for(int i = 0; i <4; i++){
            //probs += "at i: " + i + "  =>" + outputProbs[0][i] + "\n";
            if (i == 0){
                mappedProbabilities.put("Jumping jacks",output[0][i] + mappedProbabilities.get("Jumping jacks"));
                if (jumpingJacksProbtot < output[0][i]) {
                    jumpingJacksProbtot = output[0][i];
                }

                if (output[0][1] > 0.1){
                    probabilitiesForAveraging.add(output[0][1]);
                }

                jumpingJacksProb = output[0][i];

            }
            else if (i == 1){
                mappedProbabilities.put("Push ups", output[0][i]  + mappedProbabilities.get("Push ups"));
                if (pushUpsProbtot < output[0][i]) {
                    pushUpsProbtot = output[0][i];
                }

                if (output[0][1] > 0.1){
                    probabilitiesForAveraging.add(output[0][1]);
                }

                pushUpsProb = output[0][i];


            }
            else if (i == 2){
                mappedProbabilities.put("Squats", output[0][i] + mappedProbabilities.get("Squats"));
                if (squatsProbtot < output[0][i]) {
                    squatsProbtot = output[0][i];
                }

                if (output[0][1] > 0.1){
                    probabilitiesForAveraging.add(output[0][1]);
                }

                squatsProb = output[0][i];

            }
            else {
                mappedProbabilities.put("Sit ups", output[0][i] + mappedProbabilities.get("Sit ups"));

                if (sitUpsProbtot < output[0][i]) {
                    sitUpsProbtot = output[0][i];
                }
                sitUpsProb = output[0][i];

                if (output[0][1] > 0.1){
                    probabilitiesForAveraging.add(output[0][1]);
                }

            }

        }

        //return mappedProbabilities;

    }

    public void openModel(){
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception ex){
            ex.printStackTrace();
            Log.d(TAG, "error while opening model  " );
        }
    }

    public void initMap(){
        mappedProbabilities.put("Jumping jacks", (float)0);
        mappedProbabilities.put("Push ups", (float)0);
        mappedProbabilities.put("Squats", (float)0);
        mappedProbabilities.put("Sit ups", (float)0);

    }

    public float[] testInput(){
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

        return  numbersTestInput;
    }

    public  Hashtable<String, Float> getMap(){
        return mappedProbabilities;
    }

    public String getExerciseOfMaxProbability() {
        //checking which item has highest probability
        float max = Collections.max(mappedProbabilities.values());

        for (Map.Entry<String, Float> entry : mappedProbabilities.entrySet()) {
            if (entry.getValue() == max) {
                finalExercisePrediction = entry.getKey();
                return entry.getKey() ; // + probability linked to it
            }

        }
        return "Not Found";
    }



    public float getPushUpsProb(){

        return pushUpsProb;
    }

    public float getJumpingJacksProb(){

        return jumpingJacksProb;
    }

    public float getSitUpsProb() {

        return sitUpsProb;
    }

    public float getSquatsProb() {

        return squatsProb;
    }

    public float getMaxProbability(){
        String exercise = getExerciseOfMaxProbability();
        if (exercise.equals("Jumping jacks")){
            return jumpingJacksProb;
        }
        else if (exercise.equals("Push ups")){
            return pushUpsProb;
        }
        else if (exercise.equals("Squats")){
            return squatsProb;
        }
        else if (exercise.equals("Sit ups")){
            return sitUpsProb;
        }
        return 0;
    }

    public String getFinalProbabilityExercise(){
        return finalExercisePrediction;
    }

    public ArrayList<Float> getProbabilitiesForAveraging(){
        return probabilitiesForAveraging;
    }

    public float calculateAverage(ArrayList<Float> probs) {
        float sum = 0;
        if(!probs.isEmpty()) {
            for (Float prob : probs) {
                sum += prob;
            }
            return sum / probs.size();
        }
        return sum;
    }
}
