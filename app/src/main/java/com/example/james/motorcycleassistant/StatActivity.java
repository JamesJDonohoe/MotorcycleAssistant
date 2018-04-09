/**
 * This class handles the results of the users journey.
 * The star rating system is displayed here, all results are sent from the TrackingActivity and displayed here
 * The number of times the users brake/ accelerate too hard and corner too hard is also displayed here
 * **/
package com.example.james.motorcycleassistant;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fitness.request.GetFileUriRequest;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;



public class StatActivity extends Activity {

    ImageView starView, starView2;
    TextView time, avgSpeedText, disText;
    Button shareBtn;
    String brakingVal, cornerVal, sVal, sVal2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        //gets data being sent from TackingActivity
        Intent intent = getIntent();

        //getting braking info from Tracking Activity
        brakingVal = intent.getExtras().get("xValue").toString();
        //getting cornering info from Tracking Activity
        cornerVal = intent.getExtras().get("yzValue").toString();

        disText = (TextView)findViewById(R.id.disText);
        String disVal = intent.getExtras().get("getDistance").toString();
        disText.setText(disVal);

        //Gets average speed from TrackingActivity and sets text to the value
        avgSpeedText = (TextView) findViewById(R.id.avgSpeedTxt);
        String avgVal = intent.getExtras().get("AvgSpeed").toString();
        avgSpeedText.setText(avgVal);

        //Gets the time from TrackingActivity and sets the TextView to it
        time = (TextView) findViewById(R.id.time);
        String timeVal = intent.getStringExtra("timerTxt");
        time.setText(timeVal);

        //Displays braking and acceleration star rating
        starView = (ImageView) findViewById(R.id.starView);
        sVal = intent.getExtras().get("staryzVal").toString();
        starView.setImageResource(Integer.parseInt((sVal)));

        //Displays cornering star rating
        starView2 = (ImageView) findViewById(R.id.starView2);
        sVal2 = intent.getExtras().get("starxVal").toString();
        starView2.setImageResource(Integer.parseInt(sVal2));

        //If user clicks on the star for braking it will pop up with an info page
        starView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatActivity.this, BrakingInfoActivity.class);

                //getting braking value from brakingVal and sending it to BrakingInfoActivity
                intent.putExtra("xValueStat", brakingVal);

                //sending star value to BrakingInfo
                intent.putExtra("brakingStar", sVal);

                startActivity(intent);
            }
        });//end of on click listener

        //When user presses on the cornering star they will be brought to a new activity with more details
        starView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatActivity.this, CornerInfoActivity.class);

                //getting cornering value from cornerVal and sending it to CornerInfoActivity
                intent.putExtra("yzValueStat", cornerVal);

                //sending star value from CornerInfo
                intent.putExtra("cornerStar", sVal2);

                startActivity(intent);
            }
        });

        //When the user presses the share button it calls the sendScreen function
        shareBtn = (Button) findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendScreen();
            }
        });
    }
    //Function that automatically takes a screenshot and attaches to an email so the user can send it
    private void sendScreen() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".png";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            File filelocation = new File(MediaStore.Images.Media.DATA  + mPath);
            Uri myUri = Uri.parse("file://" + filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            // set the type to 'email'
            emailIntent .setType("vnd.android.cursor.dir/email");
            String to[] = {"Enter your email address"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Journey : " + now );
            startActivity(Intent.createChooser(emailIntent , "Select your preferred email app.."));

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
}
