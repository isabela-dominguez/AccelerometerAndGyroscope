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
import java.util.Hashtable;


import android.widget.Toast;

public class neuralNetworkResults extends AppCompatActivity {

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



        // from https://www.youtube.com/watch?v=RhjBDxpAOIc&ab_channel=TensorFlow
        openModel();


        //prediction values
        float[] test = new float[16];
        float[][] prediction = doInference(test);


        //tag of predictions
        Log.d(TAG, "predictions:  " + prediction);


        //printing the values
        for(int i = 0; i <4; i++){
            probs += "at i: " + i + "  =>" + prediction[0][i] + "\n";
        }

        //setting to label
        probabilities.setText(probs);

        //hashed maps with labels
        Hashtable<String, Float> mappedProbabilities = mapOutputToLabel(prediction);
        Log.d(TAG, "hash map:  " + mappedProbabilities);




    }



    public MappedByteBuffer loadModelFile() throws IOException{
        //open model suing input stream and memory map to it to load
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("mlpExercise.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLenght = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLenght);
    }


    public float[][] doInference(float[] inputNumbers){
        //input vals
        //change numbersTestInput to inputnumbers.
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



        //output shape
        float[][] output = new float[1][4];

        //run inference
        tflite.run(numbersTestInput, output);

        return output;


    }

    public Hashtable<String, Float> mapOutputToLabel(float[][] outputProbs){
        Hashtable<String, Float> mappedProbabilities = new Hashtable<String, Float>();
        for(int i = 0; i <4; i++){
            //probs += "at i: " + i + "  =>" + outputProbs[0][i] + "\n";
            if (i == 0){
                mappedProbabilities.put("Jumping jacks", outputProbs[0][i]);
            }
            else if (i == 1){
                mappedProbabilities.put("Push ups", outputProbs[0][i]);
            }
            else if (i == 2){
                mappedProbabilities.put("Squats", outputProbs[0][i]);
            }
            else {
                mappedProbabilities.put("Sit ups", outputProbs[0][i]);
            }

        }

        return mappedProbabilities;

    }

    public void openModel(){
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception ex){
            ex.printStackTrace();
            Log.d(TAG, "error while opening model  " );
        }
    }



}