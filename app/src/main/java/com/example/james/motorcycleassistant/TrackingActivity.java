/**
 * Created by James Donohoe C13452398
 * This is the main activity. All of the main functionality will be in this class.
 * The users location will be tracked and all of the sensor data will be read from here.
 *
 * https://developers.google.com/maps/documentation/android-api/map
 * https://developer.android.com/reference/android/hardware/SensorManager.html
 *
 * **/


package com.example.james.motorcycleassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static com.example.james.motorcycleassistant.R.id.map;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SensorEventListener {

    //Gives access to the database
    public static DBHelper myDb;

    //Variables for the accelerometer sensor
    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    //Values for my counter
    int xCounter;
    int yzCounter;

    //Values to display star rating. Variables are equal to stars that are in the drawable folder
    int fiveStar = R.drawable.star5;
    int fourStar = R.drawable.star4;
    int threeStar = R.drawable.star3;
    int twoStar = R.drawable.star2;
    int oneStar = R.drawable.star1;

    //Variables for google maps, markers and requests
    private GoogleMap mMap;
    Marker LocationMarker;
    MarkerOptions mOptions;
    GoogleApiClient mGoogleApiClient;
    LocationRequest userLocationRequest;

    //Array used to store coordinates
    ArrayList<LatLng> points = new ArrayList<>();

    //Making myCoordinates Global
    LatLng myCoordinates;

    //Variables for distance and average speed
    double disKilom;
    double meters;
    double avgdisSpeed;
    
    //Variables for timers
    TextView timerTextView;
    long startTime = 0;

    private TextView speedText;
    private TextView disText;

    //Handler for timer
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    //Builds the map using the map fragment
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        //Allows screen to stay on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Starts timer on top of map
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        //Sets the text of speed_text and changes when location updated
        speedText = (TextView)findViewById(R.id.speed_text);
        speedText.setText("...");

        disText = (TextView)findViewById(R.id.disText);

        //Checks to see if the sensor is available on the device
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            //if accelerometer is found the sensor listener starts
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            //if we don't have a sensor user is brought back to main menu
            Toast.makeText(this, "No Accelerometer Detect", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TrackingActivity.this, Screen1.class);
            startActivity(intent);
            finish();
        }

        //Displays Starting Marker on LatLng
        mOptions = new MarkerOptions().position(new LatLng(0, 0)).title("End Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        //Checks SDK version
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Gets map fragment and allows map to display location
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        //Button used to get and display ending location
        Button stopLocBtn;
        stopLocBtn = (Button) findViewById(R.id.stopLocBtn);
        stopLocBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                //When button is pressed timer will stop
                timerHandler.removeCallbacks(timerRunnable);

                //Adds points to myCoordinates
                points.add(myCoordinates);

                //Adds a marker onto the last LatLng titled End Location
                LocationMarker.setPosition(myCoordinates);
                mMap.addMarker(new MarkerOptions()
                        .position(myCoordinates)
                        .title("End location"));

                //Stops location updates when the button is pressed
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, TrackingActivity.this);

                //Stops the accelerometer from detecting movements when users is finished
                sensorManager.unregisterListener(TrackingActivity.this, accelerometer);

                Toast.makeText(getApplicationContext(), "Journey Tracking has Stopped", Toast.LENGTH_LONG).show();
                speedText.setText("0 - Km/h");
            }
        });

    }


    //Specifies the type of map, Normal, Hybrid or Terrain
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Adds marker on map from onCreate
        LocationMarker = mMap.addMarker(mOptions);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    //Builds the map
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //Uses GPS sensor to read location of device
    @Override
    public void onConnected(Bundle bundle) {
        //Used to get an accurate location of the device
        userLocationRequest = new LocationRequest();
        //Requests the location every second
        userLocationRequest.setInterval(1000);
        userLocationRequest.setFastestInterval(1000);
        userLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, userLocationRequest, this);
        }

    }

    //Default method, not used for anything
    @Override
    public void onConnectionSuspended(int i) {

    }

    //Handles location, sets marker onto location
    @SuppressLint({"DefaultLocale", "ResourceAsColor"})
    @Override
    public void onLocationChanged(final Location location) {
        //Adds a marker on the current position found in LatLng
        myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());

        //Sets the marker to the position of LatLng and zooms the camera in on the location
        LocationMarker.setPosition(myCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
        float zoomLevel = 12.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, zoomLevel));

        //Adds the new coordinated on each update to the points array
        points.add(myCoordinates);

        //Adds a marker at the first coordinate in the array which is always a starting location
        mMap.addMarker(new MarkerOptions().position(points.get(0))
                .title("Starting Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        //Draws a polyline between the location updates stored in points.
        mMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(8)
                .color(Color.RED));

        //gets speed from Location and converts it to Km/h
        speedText.setText(String.format("%.0f - Km/h", location.getSpeed() * 3.6));

        //Displays journey distance to the user as they are driving
        //Get distance function called to get returned distance
        getDistance();
        disText.setText(String.format("%.2f - Km", disKilom));
    }


    //Gets start and end location and calculates distance between them
    @SuppressLint("DefaultLocale")
    private double getDistance(){
        //Uses computeLength to calculate the distance in meters from my Array of points
        meters = SphericalUtil.computeLength(points);

        //Divides by a 1000 to get meters
        disKilom = meters / 1000;

        avgdisSpeed = Double.parseDouble(String.format("%.2f", disKilom));
        //Rounds it to 2 decimal places
        return avgdisSpeed;

    }

    //Uses basic average speed formula distance over time to calculate average speed
    private int avgSpeed(){
        return (int) (avgdisSpeed % startTime);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Handles users permission request. Reads into the manifest to ask for location
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Asking user for permission to location
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    //Asks for permission from the user, checks manifest for correct requests
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0)
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // If permission is declined location will not be displayed.
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_LONG).show();
                    //User will be brought back to the main menu
                    Intent intent = new Intent(TrackingActivity.this, Screen1.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    //Handles the sensor events and the change of the sensors state
    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the change of the x,y,z values of the accelerometer
        float deltaX = Math.abs(lastX - event.values[0]);
        float deltaY = Math.abs(lastY - event.values[1]);
        float deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        //Left and right / Cornering
        if(deltaX >=8 || deltaX <= -8){
            xCounter++;

            //Adds a marker if the user corners too hard
            LocationMarker.setPosition(myCoordinates);
            mMap.addMarker(new MarkerOptions().position(myCoordinates)
                    .title("Cornering Fault")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        }
        //Up and down / Breaking and acceleration
        if(deltaY >=10 || deltaY <-5 && deltaZ >=5 || deltaZ <-5){
            yzCounter++;

            //Adds a marker if the user accelerates or brakes too hard
            LocationMarker.setPosition(myCoordinates);
            mMap.addMarker(new MarkerOptions().position(myCoordinates)
                    .title("Braking / Acceleration Fault")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        }


        //Button to stop journey
        final Button stopBtn;
        stopBtn = (Button) findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Press on the stars to get more details on your journey", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(TrackingActivity.this, StatActivity.class);

                //Gets average speed from avgSpeed and sends it to Stat Activity
                intent.putExtra("AvgSpeed", avgSpeed());

                //Gets distance and sends it to Stat Activity
                intent.putExtra("getDistance", getDistance());

                //Gets text and sends it to Stat Activity
                intent.putExtra("timerTxt", timerTextView.getText());

                //When button is pressed it will start a new activity but also send the value of the x/ y counter with it
                intent.putExtra("yzValue" , getyzVal());
                intent.putExtra("xValue", getxVal());

                //Gets xCounter val from sendxCounterStars and returns star based on xCounter value
                intent.putExtra("starxVal", sendxCounterStars());

                //Gets yzCounter from sendyzCounterStars and returns star based on XCounter value
                intent.putExtra("staryzVal", sendyzCounterStars());


                //If both cornering and acceleration/ braking is 5 star a toast message will be displayed
                if (sendxCounterStars() == fiveStar && sendyzCounterStars() == fiveStar)
                {
                    Toast.makeText(getApplicationContext(), "You had a perfect drive, well done!!", Toast.LENGTH_SHORT).show();
                }
                //Starts the new activity
                startActivity(intent);
            }
        });

    }

    //getxVal gets the value from xCounter and places it into a new variable xVal this can be used for the intent
    private int getxVal(){
        return Integer.parseInt(String.valueOf(xCounter));
    }
    //getyzVal gets the value from yzCounter and places it into a new variable val this can be used for the intent
    private int getyzVal(){
        return Integer.parseInt(String.valueOf(yzCounter));
    }

    //Sending stars for rating, braking and acceleration
    private int sendxCounterStars(){
        if(xCounter <=30)
            return fiveStar;
        if(xCounter >=31 && xCounter <=50)
            return fourStar;
        if(xCounter >=51 && xCounter <=60)
            return threeStar;
        if(xCounter >=61 && xCounter <=70)
            return twoStar;
        else
            return oneStar;
    }

    //Sending stars for rating, cornering
    private int sendyzCounterStars(){
        if(yzCounter <=30)
            return fiveStar;
        if(yzCounter >=31 && yzCounter <=50)
            return fourStar;
        if(yzCounter >=51 && yzCounter <=60)
            return threeStar;
        if(yzCounter >=61 && yzCounter <=70)
            return twoStar;
        else
            return oneStar;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
