/**
 * This class handles the results of the users journey.
 * The star rating system is displayed here, all results are sent from the TrackingActivity and displayed here
 * The number of times the users brake/ accelerate too hard and corner too hard is also displayed here
 * **/
package com.example.james.motorcycleassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



public class StatActivity extends Activity{

    TextView baValue ;
    TextView cornerVal;
    ImageView starView;
    ImageView starView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        //gets data being sent from TackingActivity
        Intent intent = getIntent();

        baValue = (TextView) findViewById(R.id.baValue);
        cornerVal = (TextView) findViewById(R.id.cornerVal);

        //Displays braking and acceleration star rating
        starView = (ImageView) findViewById(R.id.starView);
        String sVal = intent.getExtras().get("staryzVal").toString();
        starView.setImageResource(Integer.parseInt((sVal)));

        //Displays cornering star rating
        starView2 = (ImageView) findViewById(R.id.starView2);
        String sVal2 = intent.getExtras().get("starxVal").toString();
        starView2.setImageResource(Integer.parseInt(sVal2));

        //this gets the data and assigns it to a new variable to be used for the setText
        String baVal = intent.getExtras().get("yzValue").toString();
        String cValue = intent.getExtras().get("xValue").toString();

        //this sets the text of the text view from the values in the variables
        baValue.setText(String.valueOf(baVal));
        cornerVal.setText(String.valueOf(cValue));

        //If user clicks on the value for braking it will pop up with an info page
        starView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatActivity.this, BrakingInfoActivity.class);
                startActivity(intent);
            }
        });//end of on click listener

    }
}
