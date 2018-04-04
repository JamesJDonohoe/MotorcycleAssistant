package com.example.james.motorcycleassistant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class BrakingInfoActivity extends AppCompatActivity {

    ImageView brakingStat;
    TextView brakingWorse;

    int brakeVal = 0;
    int cornVal = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braking_info);

        brakingWorse = (TextView)findViewById(R.id.brakingStat);

        //gets intents from StatActivity
        Intent intent = getIntent();

        //Gets xValueStat from StatActivity
        String brakingVal = intent.getExtras().get("xValueStat").toString();
        String cornerVal = intent.getExtras().get("yzValueStat").toString();

        brakingStat = (ImageView)findViewById(R.id.starViewStat);
        String brakeStar = intent.getExtras().get("brakingStar").toString();
        brakingStat.setImageResource(Integer.parseInt((brakeStar)));

        //Converts String to Int so it can be used in if loop
        brakeVal = Integer.parseInt(brakingVal);
        cornVal = Integer.parseInt(cornerVal);

        if (brakeVal <= 20 ){
            brakingWorse.setText("Your braking was excellent this journey, keep it up");
        }else if (brakeVal >= 20 && brakeVal <=29 ){
            brakingWorse.setText("Your braking was great, but almost not 5 stars");
        }else if (brakeVal == R.drawable.star4){
            brakingWorse.setText("Your braking was good but needs slight improvement");
        }else if (brakeVal == R.drawable.star3){
            brakingWorse.setText("Your braking needs improvement");
        }else if (brakeVal == R.drawable.star2 || brakeVal == R.drawable.star1){
            brakingWorse.setText("Your braking needs serious improvement, please see " +
                    "the RSA guide on how to improve your braking");
        }
    }
}
