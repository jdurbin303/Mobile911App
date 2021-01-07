package com.example.buoyv3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//Used this video tutorial to help
//https://www.youtube.com/watch?v=FIDeomTlYHI&list=RDCMUCQ5xY26cw5Noh6poIE-VBog&index=2
//https://www.youtube.com/watch?v=Ag8c8fnjEKw&list=RDCMUCQ5xY26cw5Noh6poIE-VBog

//THe user map activity shows the continuously updated location of the user
//and the location of the volunteer / responder
//The volunteer is passed as a static location here since

//IMPORTANT NOTE!!!!!!!!!!
//If you dont change the location of the emulator it will end up in California and you cant see both points on the map
public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private CountDownTimer mCountDownTimer;
    private boolean timerRunning;
    private long timeLeftinMils = 10000; //10 second timer
    //private Volunteer volunteer;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Button imSafeButton;
    private TextView secsLeft;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        imSafeButton = findViewById(R.id.imSafeButton);
        secsLeft=findViewById(R.id.secsLeftTextView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Asks for location permissions
            ActivityCompat.requestPermissions(UserMapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else{
            mapFragment.getMapAsync(this);
        }

        //im safe button cancels and returns to main activity after a 10s hold
        //warning here?? but i think it's fine
        imSafeButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: //If we hold down a click, run timer
                        Log.i("bloop", "action down");
                        startTimer();
                        displayTimeLeft();
                        return true;
                    case MotionEvent.ACTION_UP: //If we release a click, restart
                        Log.i("bloop", "action up");
                        //resets the time limit
                        resetTimer();
                        //cancels the current timer
                        pauseTimer();
                        //If the timer isn't running, this will erase the current time from the screen
                        displayTimeLeft();
                        //if it doesn't click here, it causes an error and basically interferes with on click listener
                        view.performClick();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        //Helps avoid error with onTouchListener but idk why we have a warning here...
        imSafeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //do nothing
            }
        });

        /*//Creates a fake volunteer
        //I tried to do this in onCreate but it didn't work out too well
        LatLng fakeDriverLatLng = new LatLng(39.746087, -105.003230);
        Location fakeDriverLocation = new Location("");
        fakeDriverLocation.setLatitude(fakeDriverLatLng.latitude);
        fakeDriverLocation.setLongitude(fakeDriverLatLng.longitude);
        Volunteer volunteer = new Volunteer("STACY", "8675309", "she", fakeDriverLocation);
        //mMap.addMarker(new MarkerOptions().position(fakeDriverLatLng).title("Volunteer"));*/
    }//on create

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //GoogleApi Functions
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //Default with google maps api, we're gonna change this
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient(); //MUST be above setMyLocationEnabled
        mMap.setMyLocationEnabled(true); //needed permissions check above
    }

    protected synchronized void buildGoogleApiClient(){
        //sets value of mGoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        //allows us to connect and use API
        mGoogleApiClient.connect();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //this function will be called each second
        //our most important function here, everything else just kind of gets it ready
        mLastLocation = location;

        //gets location of user
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //animates camera with location change
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12)); //different indexes give out different types of zoom
        //the smaller they are, the closer they are?
        //For a better implementation, I would like to zoom based on the bounds of the user and volunteer
        //I just didn't have time to do so
        //https://developers.google.com/maps/documentation/android-sdk/views

        //Does fake driver location and the distance between them
        onTheWay();
    }

    //@Override
    public void onTheWay(){
        //Just testing this out and trying to build around the function SOOOO
        //I am just going to build in a fake driver location and code around it
        //Later on this will probably be a parameter,
        //or the driver will be a parameter and i'll use get method to get this info
        //These are the coordinates of UC Denver
        LatLng fakeDriverLatLng = new LatLng(39.746087, -105.003230);
        Location fakeDriverLocation = new Location("");
        fakeDriverLocation.setLatitude(fakeDriverLatLng.latitude);
        fakeDriverLocation.setLongitude(fakeDriverLatLng.longitude);
        mMap.addMarker(new MarkerOptions().position(fakeDriverLatLng).title("Volunteer"));

        float distanceInMeters = mLastLocation.distanceTo(fakeDriverLocation);
        float distanceInMiles = (float) (distanceInMeters*0.000621371);
        String stringDistance = String.format("%.2f", distanceInMiles);

        TextView textView = findViewById(R.id.distanceAway);

        textView.setText("Help is " + stringDistance + " miles away");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //1000ms = 1 second, how often the location will update
        mLocationRequest.setFastestInterval(1000); //dont know what the difference is here
        //this is important so high accuracy is needed for both parties
        //DOWNSIDE--drains more battery life than lower priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Asks for location permissions
            ActivityCompat.requestPermissions(UserMapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    //this function fixes a bug in which google will not get the location due to lack of permissions

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map); //idk if i should put this here but idk how else to get rid of error
                    //bc i didn't have a map fragment variable
                    mapFragment.getMapAsync(this);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enable location services", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Timer functions
    //...probably should have put all of these in their own class

    //used this to help me with a countdown timer because a regular timer kept giving me an illegal state exception
    //Source: https://codinginflow.com/tutorials/android/countdowntimer/part-1-countdown-timer
    //java.lang.IllegalStateException: Timer already cancelled.
    //Also used this for help: https://www.youtube.com/watch?v=MDuGwI6P-X8
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(timeLeftinMils, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftinMils = millisUntilFinished;
                displayTimeLeft();
                int seconds = (int)timeLeftinMils/1000 + 1;
                String timeLeft = Integer.toString(seconds);
                Log.i("Time", timeLeft);
            }
            @Override
            public void onFinish() {
                timerRunning = false;
                returnHome();
            }
        }.start();
        timerRunning = true;
        Log.i("bloop", "timer start");
    }
    private void pauseTimer() {
        Log.i("bloop", "timer paused");
        mCountDownTimer.cancel();
        timerRunning = false;
    }
    private void resetTimer() {
        Log.i("bloop", "timer restart");
        timeLeftinMils = 10000;
    }

    public void displayTimeLeft(){
        int seconds = (int)timeLeftinMils/1000 + 1;
        String timeLeft = Integer.toString(seconds);
        if (timerRunning){
            secsLeft.setText(timeLeft);
        }
        else{
            Log.i("Bloop", "timer not running, erase text");
            secsLeft.setText("");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //go back to the main activity
    public void returnHome(){
        // invoke intent
        startActivity(new Intent(UserMapActivity.this, MainActivity.class));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}