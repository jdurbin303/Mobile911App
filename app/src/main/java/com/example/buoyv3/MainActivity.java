package com.example.buoyv3;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

//Main activity class
//Our starting screen
//Implements menu button and drawer
//Navigates to dialog and finally map by pressing buoy button
public class MainActivity extends AppCompatActivity {

    //Private class variables

    //To time the button being held down
    private CountDownTimer mCountDownTimer;
    private boolean timerRunning;
    private long timeLeftinMils = 3000; //3 second timer
    private TextView timeLeftShown;

    //I ended up only really dealing with the data manager and user data in the PersonalizeDevice acitivty
    private DataManager mDataManager; //declare datamanager
    //private User user();

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //On create
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main); //we set the content as the menu, not the current activity

        //declare the buoy button
        //Users hold down the button for 3 sec to call for help
        Button buttonBuoy = (Button) findViewById(R.id.buoyButton);
        //time left shown is the countdown # shown on screen to the user as they hold down the button
        timeLeftShown = findViewById(R.id.countdown_textview);

        //instantiate dataManager
        mDataManager = new DataManager(this);

        //Try to make the menu button open the menu drawer
        //Got a lot of this code from this tutorial and learned how to apply it across several activities:
        //https://www.youtube.com/watch?v=3SHLg2isKi4&t=351s
        //As a better implementation in the future, I think i should pass an instance of User
        //across the various activities with an intent
        Button menuButton = (Button) findViewById(R.id.menu_button);
        final DrawerLayout menuDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_home){
                    Log.i("selected", "nav_home");
                    //we're already here so just close the drawer
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }
                if(id==R.id.nav_aboutUs){
                    Log.i("selected", "nav_aboutUs");
                    // invoke intent
                    startActivity(new Intent(MainActivity.this, AboutUs.class));
                    //close menu drawer
                    menuDrawer.closeDrawer(Gravity.LEFT);

                }
                if (id == R.id.nav_personalize) {
                    Log.i("selected", "nav_personalize");
                    // invoke intent
                    startActivity(new Intent(MainActivity.this, PersonalizeDevice.class));
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }
                return true;
            }
        });

        //trying to make it do the thing only when button is held down for x time before it does something
        //used this to help
        //https://stackoverflow.com/questions/22606977/how-can-i-get-button-pressed-time-when-i-holding-button-on
        buttonBuoy.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: //If we hold down a click, run timer
                        Log.i("bloop", "action down"); //error checking
                        startTimer();
                        displayTimeLeft();
                        return true;
                    case MotionEvent.ACTION_UP: //If we release a click, restart
                        Log.i("bloop", "action up"); //error checking
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

        //on click listener interfering with on touch listener--how to fix?
        //got implement an onclick that does nothing and call performClick in onTouchListener
        buttonBuoy.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
              //do nothing
            }
        });

        //make menu button open up the slide drawer and close it when it's open
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                boolean slideState = false;
                if(slideState){
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }else{
                    menuDrawer.openDrawer(Gravity.LEFT);
                }
            }
        });
    }//on create
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //used this to help me with a countdown timer because a regular timer kept giving me an illegal state exception
    //Source: https://codinginflow.com/tutorials/android/countdowntimer/part-1-countdown-timer
    //java.lang.IllegalStateException: Timer already cancelled.
    //Also used this for help: https://www.youtube.com/watch?v=MDuGwI6P-X8
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(timeLeftinMils, 1000) { //counts down by 1 sec interval from timeLeftinMils
            @Override
            public void onTick(long millisUntilFinished) {
                //Each time it ticks, we update, the time, and display it
                //The other part of the code was for error checking
                timeLeftinMils = millisUntilFinished;
                displayTimeLeft();
                int seconds = (int)timeLeftinMils/1000 + 1;
                String timeLeft = Integer.toString(seconds);
                Log.i("Time", timeLeft);
            }
            @Override
            public void onFinish() {
                //Once we've held down the button for long enough, the timer stops running
                //And we transition to the dialog that asks the user what kind of help they need
                timerRunning = false;
                //openMap();
                openHelpDialog2();
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
        timeLeftinMils = 3000; //resets time back to 3 seconds
    }
    //end of timer functions
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void openMap(){
        // invoke intent to open google map that displays help is xyz away
        startActivity(new Intent(MainActivity.this, UserMapActivity.class));
    }
    //open map
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void openHelpDialog2(){
        //Different types of help users can choose from
        final String[]typesOfHelp = new String[]{"Personal Safety", "Domestic", "Mental Health",
                "Retail & Service", "Conflict De-Escalation and Resolution"};

        //Build new alert dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Select the Type of Help You Need");
        dialog.setSingleChoiceItems(typesOfHelp, -1, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //user.setTypeOfHelp(typesOfHelp[i]);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.setCancelable(true); //return to main activity
            }
        });
        dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openMap();
            }
        });
        final AlertDialog shower = dialog.show();

    }
    //open dialog
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void displayTimeLeft(){
        //Shows time left in secs on the screen by setting the text of a textView
        int seconds = (int)timeLeftinMils/1000 + 1;
        String timeLeft = Integer.toString(seconds);
        if (timerRunning){
            timeLeftShown.setText(timeLeft);
        }
        else{
            Log.i("Bloop", "timer not running, erase text");
            timeLeftShown.setText("");
        }
    }
    //displayTimeLeft
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}//end of class