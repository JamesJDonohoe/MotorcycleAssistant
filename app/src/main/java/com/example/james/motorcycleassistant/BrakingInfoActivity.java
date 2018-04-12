package com.example.james.motorcycleassistant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class BrakingInfoActivity extends AppCompatActivity {

    ImageView brakingStat;
    TextView brakingWorse, brakingAmnt;
    TextView HyperLink;
    Spanned Text;
    int brakeVal = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braking_info);

        brakingWorse = (TextView)findViewById(R.id.brakingWorse);
        brakingAmnt = (TextView)findViewById(R.id.brakingAmnt);

        HyperLink = (TextView)findViewById(R.id.linkText);

        //gets intents from StatActivity
        Intent intent = getIntent();

        //Gets xValueStat from StatActivity
        String brakingVal = intent.getExtras().get("xValueStat").toString();

        //Displays the users star from the journey
        brakingStat = (ImageView)findViewById(R.id.starViewStat);
        String brakeStar = intent.getExtras().get("brakingStar").toString();
        brakingStat.setImageResource(Integer.parseInt((brakeStar)));

        //Converts String to Int so it can be used in if loop
        brakeVal = Integer.parseInt(brakingVal);

        //nested if to give user more detailed report on their driving
        if (brakeVal < 30 ){
            brakingWorse.setText("Your braking and acceleration was excellent this journey, keep it up");
            brakingAmnt.setText("You had 10 faults or less");

        }else if (brakeVal >= 31 && brakeVal <=50 ){
            brakingWorse.setText("Your braking and acceleration was great, but almost not 5 stars");
            brakingAmnt.setText("You had 10 to 15 faults");

        }else if (brakeVal >= 51 && brakeVal <=60){
            brakingWorse.setText("Your braking and acceleration was good but needs slight improvement");
            brakingAmnt.setText("You had 15 to 20 faults");

        }else if (brakeVal >= 61 && brakeVal <=70){
            brakingWorse.setText("Your braking and acceleration needs improvement");
            brakingAmnt.setText("You had 20 to 25 faults");

            Text = Html.fromHtml("<a href='http://www.rsa.ie/Documents/Road%20Safety/Motorcycles/This_is_your_bike.pdf'>RSA - THIS IS YOUR BIKE</a>");

            HyperLink.setMovementMethod(LinkMovementMethod.getInstance());
            HyperLink.setText(Text);

        }else if (brakeVal >= 71 ){
            brakingWorse.setText("Your braking and acceleration needs serious improvement, please see " +
                    "the RSA guide on how to improve your braking");
            brakingAmnt.setText("You had more than 25 faults");

            Text = Html.fromHtml("<a href='http://www.rsa.ie/Documents/Road%20Safety/Motorcycles/This_is_your_bike.pdf'>RSA - THIS IS YOUR BIKE</a>");

            HyperLink.setMovementMethod(LinkMovementMethod.getInstance());
            HyperLink.setText(Text);
        }
    }

}
