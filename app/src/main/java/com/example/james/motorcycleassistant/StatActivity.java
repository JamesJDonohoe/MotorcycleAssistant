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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class StatActivity extends Activity {

    ImageView starView, starView2;
    TextView time, avgSpeedText;
    Button shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        //gets data being sent from TackingActivity
        Intent intent = getIntent();

        avgSpeedText = (TextView) findViewById(R.id.avgSpeedTxt);
        String avgVal = intent.getStringExtra("avgSpeed");
        avgSpeedText.setText(avgVal);

        //Gets the time from TrackingActivity and sets the TextView to it
        time = (TextView) findViewById(R.id.time);
        String timeVal = intent.getStringExtra("timerTxt");
        time.setText(timeVal);

        //Displays braking and acceleration star rating
        starView = (ImageView) findViewById(R.id.starView);
        String sVal = intent.getExtras().get("staryzVal").toString();
        starView.setImageResource(Integer.parseInt((sVal)));

        //Displays cornering star rating
        starView2 = (ImageView) findViewById(R.id.starView2);
        String sVal2 = intent.getExtras().get("starxVal").toString();
        starView2.setImageResource(Integer.parseInt(sVal2));

        //If user clicks on the value for braking it will pop up with an info page
        starView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatActivity.this, BrakingInfoActivity.class);
                startActivity(intent);
            }
        });//end of on click listener

        shareBtn = (Button) findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot_and_send_email();
            }
        });
    }
    private void takeScreenshot_and_send_email() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), mPath);
            Uri path = Uri.fromFile(filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent .setType("vnd.android.cursor.dir/email");
            String to[] = {"james.j.donohoe@outlook.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));


        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
}
