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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.james.motorcycleassistant.R.id.map;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SensorEventListener{
    //Variable for the accelerometer sensor
    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    //Values for my counter
    int xCounter ;
    int yzCounter;

    private GoogleMap mMap;
    Marker LocationMarker;
    GoogleApiClient mGoogleApiClient;

    Location getLastLocation;
    LocationRequest userLocationRequest;

    //Builds the map using the map fragment
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        //Checks to see if the sensor is available on the device
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            //if accelerometer is found the sensor listener starts
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            //if we don't have a sensor user is brought back to main menu
            Toast.makeText(this, "No Accelerometer Detect", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TrackingActivity.this, Screen1.class);
            startActivity(intent);
            finish();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Gets map fragment and allows map to display location
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }


    //Specifies the type of map, Normal, Hybrid or Terrain
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
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
        userLocationRequest.setInterval(1000);
        userLocationRequest.setFastestInterval(1000);
        userLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
    @Override
    public void onLocationChanged(Location location) {

        //This gets the Current/Last Location an stores it in a variable called location
        getLastLocation = location;
        if (LocationMarker != null) {
            LocationMarker.remove();
        }

        //Uses LatLng to get the location and display a marker at the location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        /*
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        markerOptions.position(latLng);
        markerOptions.title("Starting Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        */

        //Adds a custom marker onto the location point
        mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));//fromResource(R.drawable.bike_arrow))
                //.position(latLng)
                //.flat(true)
                //.rotation(245)
                //.title("Starting Location"));

        //move camera to current location
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,10);
        mMap.animateCamera(cameraUpdate);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

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

        if(deltaX >=5 || deltaX <= -5){
            xCounter++;
        }
        if(deltaY >=10 || deltaY<=-10 || deltaZ >= 10 || deltaZ <=-10){
            yzCounter++;

        }

        //Button to stop journey
        Button stopBtn;
        stopBtn = (Button) findViewById(R.id.stopBtn);

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackingActivity.this, StatActivity.class);
                //When button is pressed it will start a new activity but also send the value of the x/ y counter with it
                intent.putExtra("yzValue" , getyzVal());
                intent.putExtra("xValue", getxVal());
                startActivity(intent);

            }
        });
    }

    //getxVal gets the value from xCounter and places it into a new variable xVal this can be used for the intent
    private int getxVal(){
        int xVal = Integer.parseInt(String.valueOf(xCounter));
        return xVal;
    }
    //getyzVal gets the value from yzCounter and places it into a new variable val this can be used for the intent
    private int getyzVal(){
        int val = Integer.parseInt(String.valueOf(yzCounter));
        return val;
        //return yzCounter;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
