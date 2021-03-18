package com.example.accelerometerandgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class excerciseDescriptions extends AppCompatActivity {
    TextView   btnexercise;
    TextView titlepage;
    Animation animpage, bttone, bttwo, btthree, ltr;
    //ImageView imgpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excercise_descriptions);

        btnexercise = (TextView) findViewById(R.id.btnexercise);

        //load animation
        animpage = AnimationUtils.loadAnimation(this, R.anim.animpage);
        bttone = AnimationUtils.loadAnimation(this, R.anim.bttone);
        bttwo = AnimationUtils.loadAnimation(this, R.anim.bttwo);
        btthree = AnimationUtils.loadAnimation(this, R.anim.btthree);
        ltr = AnimationUtils.loadAnimation(this, R.anim.ltr);

        //set text views and img views
        titlepage = (TextView) findViewById(R.id.instructions);
        //subtitlepage = (TextView) findViewById(R.id.subtitlepage);
        btnexercise = (TextView) findViewById(R.id.btnexercise);
        //imgpage = (ImageView) findViewById(R.id.bucket);


        //export animate
        //imgpage.startAnimation(animpage);
        titlepage.startAnimation(bttone);
        btnexercise.startAnimation(btthree);


        //start to other page
        btnexercise.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent a = new Intent(excerciseDescriptions.this, MainActivity.class);
                a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(a);
            }
        });
    }
}