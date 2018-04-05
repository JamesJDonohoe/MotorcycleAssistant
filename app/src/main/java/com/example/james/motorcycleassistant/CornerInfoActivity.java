package com.example.james.motorcycleassistant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

public class CornerInfoActivity extends AppCompatActivity {

    ImageView starViewStat2;
    TextView cornerStat, cornerText;
    TextView HyperLink;
    Spanned Text;

    int cornVal = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corner_info);

        cornerStat = (TextView)findViewById(R.id.cornerStat);
        cornerText = (TextView)findViewById(R.id.cornerText);

        HyperLink = (TextView)findViewById(R.id.linkText);


        //Gets intent data from StatActivity
        Intent intent = getIntent();

        //Gets yzValueStat from StatActivity
        String cornerVal = intent.getExtras().get("yzValueStat").toString();

        //Displays star value from Stat Activity
        starViewStat2 = (ImageView)findViewById(R.id.starViewStat2);
        String cornerStar= intent.getExtras().get("cornerStar").toString();
        starViewStat2.setImageResource(Integer.parseInt((cornerStar)));

        //converts String cornerVal into int for loop
        cornVal = Integer.parseInt(cornerVal);

        if(cornVal < 30){
            cornerStat.setText("Your cornering was excellent this journey, keep it up");
            cornerText.setText("You had 10 faults or less");

        }else if (cornVal >= 31 && cornVal <= 50) {
            cornerStat.setText("Your cornering was great, but almost not 5 stars");
            cornerText.setText("You had 10 to 15 faults");

        }else if (cornVal >= 51 && cornVal <= 60){
            cornerStat.setText("Your cornering was good but needs slight improvement");
            cornerText.setText("You had 15 to 20 faults");

        }else if (cornVal >= 61 && cornVal <= 70){
            cornerStat.setText("Your cornering needs improvement");
            cornerText.setText("You had 20 to 25 faults");

            Text = Html.fromHtml("<a href='http://www.rsa.ie/Documents/Road%20Safety/Motorcycles/This_is_your_bike.pdf'>RSA - THIS IS YOUR BIKE</a>");

            HyperLink.setMovementMethod(LinkMovementMethod.getInstance());
            HyperLink.setText(Text);

        }else if (cornVal >= 71){
            cornerStat.setText("Your cornering needs serious improvement, please see " +
                            "the RSA guide on how to improve your cornering");
            cornerText.setText("You had more than 25 faults");

            Text = Html.fromHtml("<a href='http://www.rsa.ie/Documents/Road%20Safety/Motorcycles/This_is_your_bike.pdf'>RSA - THIS IS YOUR BIKE</a>");

            HyperLink.setMovementMethod(LinkMovementMethod.getInstance());
            HyperLink.setText(Text);
        }
    }
}
