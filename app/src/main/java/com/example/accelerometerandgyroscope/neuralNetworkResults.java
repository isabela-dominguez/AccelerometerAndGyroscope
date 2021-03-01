package com.example.accelerometerandgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.util.Log;
import android.widget.TextView;

import com.example.accelerometerandgyroscope.ml.MlpExercise;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.*;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


import android.widget.Toast;

public class neuralNetworkResults extends AppCompatActivity {

    Interpreter tflite;
    TextView results, probabilities;
    String probs ="";

    private static final String TAG = "NeuralNetwork";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neural_network_results);

        //setting up text
        results = (TextView) findViewById(R.id.results);
        probabilities = (TextView) findViewById(R.id.probabilities);

        //Turning numbers into bytebuffers
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


        //this is telling it what data type it needs
        ByteBuffer.allocate(4).putFloat(numbersTestInput[0]).array();
        byte[] byteArray= FloatArray2ByteArray(numbersTestInput);
        ByteBuffer byteBuffer= ByteBuffer.wrap(byteArray);
        getOutput(byteBuffer);

    }

//    public TensorBuffer doInference(String inputString){
//        //input
//        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 16}, DataType.FLOAT32);
//
//        //output
//
//        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//    }

    public static byte[] FloatArray2ByteArray(float[] values){
        ByteBuffer buffer= ByteBuffer.allocate(4 * values.length);
        for (float value : values){
            buffer.putFloat(value);
        }
        return buffer.array();
    }

//    private MappedByteBuffer loadModelFile() throws IOException{
//        //open model suing input stream and memory map to it to load
//        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("mlpExercise.tflite");
//        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = inputStream.getChannel();
//        long startOffset = fileDescriptor.getStartOffset();
//        long declaredLenght = fileDescriptor.getDeclaredLength();
//
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLenght);
//    }



    private void getOutput(ByteBuffer byteBuffer){
        try {
            MlpExercise model = MlpExercise.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 16}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            MlpExercise.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


            Toast.makeText(this,"output: "+outputFeature0.toString(),Toast.LENGTH_SHORT).show();
            Log.d(TAG, "output: " +outputFeature0.toString());
            results.setText("output: " +outputFeature0.toString() + "thing 0 " + outputFeature0.getFloatValue(0) +"thing 1 " + outputFeature0.getFloatValue(1) + "data type " + outputFeature0.getDataType() + " float array: " +outputFeature0.getFloatArray() + " \nbuffer " + outputFeature0.getBuffer() + " shape format " + outputFeature0.getShape() + "\n" );

            for(int i = 0; i <4; i++){
                probs += "at i: " + i + "  =>" + outputFeature0.getFloatValue(i) + "\n";
            }

            probabilities.setText(probs);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }







}