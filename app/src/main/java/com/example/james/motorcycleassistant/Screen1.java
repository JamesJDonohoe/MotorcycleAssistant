package com.example.james.motorcycleassistant;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Screen1 extends AppCompatActivity {

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //This will check and see if wifi and location are enabled
        //If they are turned off it'll prompt the user to go into settings and turn them on
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.alertDialog);
            dialog.setMessage("Network and Location are not enabled");
            dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    //Intent getGPS = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Intent getNetwork = new Intent ( Settings.ACTION_SETTINGS);
                    context.startActivity(getNetwork);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                    //Dialog for the option of the tutorial
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.alertDialog);
                    // set title
                    alertDialogBuilder.setTitle("Learn what the app does and how to use it");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Open the tutorial?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Screen1.this, TutorialActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //skips the tutorial
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
            dialog.show();
        }



        //When user presses the button a dialog will be displayed, if the user presses yes it will
        //start tracking them after one minute. This allows the user to get ready without false readings
        //Start button to start tracking
        final Button startbtn;
        startbtn = (Button) findViewById(R.id.startbtn);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Screen1.this, R.style.alertDialog);
                builder.setMessage("We will start tracking your journey in 60 seconds");

                builder.setPositiveButton("Track Journey", new DialogInterface.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //When button is already pressed the listener will be set to null and the user will be told
                        //button has been pressed
                        startbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Button already pressed, " +
                                        "journey will be tracked shortly", Toast.LENGTH_LONG).show();
                                startbtn.setOnClickListener(null);
                            }
                        });
                        Toast.makeText(getApplicationContext(), "We will track your journey in 60 seconds", Toast.LENGTH_LONG).show();

                        //Brings user to a new activity
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Intent intent = new Intent(Screen1.this, TrackingActivity.class);
                                Toast.makeText(getApplicationContext(), "Your Journey will now be tracked", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }

                            //6000 milliseconds = 1 minute
                        }, 2000L );//60000L);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                //creating alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }

}
