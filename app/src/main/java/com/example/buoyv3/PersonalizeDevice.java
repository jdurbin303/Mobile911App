package com.example.buoyv3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


//Activity to add user data to sql database and persist as user opens and closes the app
//intention is to make it easier to locate / know how to address the user in the event of an emergency
public class PersonalizeDevice extends AppCompatActivity {

    private Button menuButton;
    private Button saveButton;
    private Button clearButton;

    private TextView firstNameEditText;
    private TextView lastNameEditText;
    private TextView dobEditText;
    private TextView phoneNumberEditText;
    private TextView streetAddressEditText;
    private TextView cityEditText;
    private TextView stateEditText;
    private TextView zipEditText;
    private TextView pronounEditText;

    private DataManager dataManager;
    //private DatabaseHelper databaseHelper;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_personalize_device); //we set the content as the menu, not the current activity

        //Create menu button
        menuButton = findViewById(R.id.menu_button3);
        //Create clear and save buttons
        clearButton = findViewById(R.id.clearButton);
        saveButton = findViewById(R.id.saveButton);
        //Create edit text fields
        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        dobEditText = findViewById(R.id.editTextDOB);
        phoneNumberEditText = findViewById((R.id.editTextPhone));
        streetAddressEditText = findViewById(R.id.editTextTextStreetAddress);
        cityEditText = findViewById(R.id.editTextCity);
        stateEditText = findViewById(R.id.editTextState);
        zipEditText = findViewById(R.id.editTextZip);
        pronounEditText = findViewById(R.id.editTextPronouns);

        dataManager = new DataManager(this);

        //Creates menu drawer
        final DrawerLayout menuDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_home){
                    Log.i("selected", "nav_home");
                    // invoke intent
                    startActivity(new Intent(PersonalizeDevice.this, MainActivity.class));
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }
                if(id==R.id.nav_aboutUs){
                    Log.i("selected", "nav_aboutUs");
                    // invoke intent
                    startActivity(new Intent(PersonalizeDevice.this, AboutUs.class));
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }
                if (id == R.id.nav_personalize) {
                    Log.i("selected", "nav_personalize");
                    //We're already here so just close the drawer
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }
                return true;
            }
        });


        //Open and close menu drawer with button
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

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                firstNameEditText.setText("");
                lastNameEditText.setText("");
                phoneNumberEditText.setText("");
                streetAddressEditText.setText("");

                cityEditText.setText("");
                stateEditText.setText("");
                zipEditText.setText("");
                dobEditText.setText("");
                pronounEditText.setText("");

                //Returns cursor to the first field
                firstNameEditText.requestFocus();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String name = firstNameEditText.getText().toString() + " " + lastNameEditText.getText().toString();
                String dob = dobEditText.getText().toString();
                String phone = phoneNumberEditText.getText().toString();
                String street = streetAddressEditText.getText().toString();
                String city = cityEditText.getText().toString();
                String state = stateEditText.getText().toString();
                String zip = zipEditText.getText().toString();
                String pronouns = pronounEditText.getText().toString();

                //We don't know this yet so we'll leave it "blank"
                String typeOfHelp = "";
                //We don't know/need the gps location yet so we're just going to leave that part blank for now
                User user = new User(name, phone, dob, pronouns, street, city, state, zip);

                //if everything is blank, just delete the user
                if(name=="" && dob=="" &&phone=="" &&street=="" &&city=="" &&state=="" &zip=="" &&pronouns=="") {
                    dataManager.delete(user);
                }
                else{
                    if(isData())
                    {
                        Log.i("info", "there is a user so edit it");
                        dataManager.edit(user);
                    }
                    else{
                        addNewUser(user);
                    }

                    //MainActivity callingActivity = (MainActivity) getActivity();
                    //callingActivity.addNewContact(contact);
                    //dismiss();

                    //Returns cursor to the first field
                    firstNameEditText.requestFocus();
                }
            }
        });
    }//oncreate
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.activity_personalize_device, container, false);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Functions for data persistence and database
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }//onResume

    public void addNewUser (User user) {
        String name = user.getName();
        String phone = user.getPhoneNumber();
        String dob = user.getDob();
        String pronouns = user.getPronouns();
        String streetAddress = user.getStreetAddress();
        String city = user.getCity();
        String state = user.getState();
        String zip = user.getZip();
        String typeOfHelp = user.getTypeOfHelp();

        dataManager.insert(name, phone, dob, pronouns, streetAddress, city, state, zip, typeOfHelp, null);

        loadData();
    }//addNewUser

    //need to avoid crash if they try to save all blank data
    public void loadData () {
        Cursor cursor = dataManager.selectAll();

        if (cursor.getCount()>0 && cursor.moveToFirst())
        {

            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            String dob = cursor.getString(3);
            String pronouns = cursor.getString(4);
            String streetAddress = cursor.getString(5);
            String city = cursor.getString(6);
            String state = cursor.getString(7);
            String zip = cursor.getString(8);


            //I kind of was lazy with the first name last name thing so now we gotta split it
            String[] splitted = name.split("\\s+");

            //if no info for name, make them blank
            if(splitted.length==0) {
                //if it's blank, make it blank
                firstNameEditText.setText("");
                lastNameEditText.setText("");
            }
            else{
                //Make the first element the fist elements
                firstNameEditText.setText(splitted[0]);
                if(splitted.length < 2){
                    //if there's not more elements, last name is blank
                    //avoids an index error
                    lastNameEditText.setText("");
                }
                else{
                    //Otherwise lastName is the next element
                    lastNameEditText.setText(splitted[1]);
                }
            }

            //set text to the textViews
            phoneNumberEditText.setText(phone);
            dobEditText.setText(dob);
            pronounEditText.setText(pronouns);
            streetAddressEditText.setText(streetAddress);
            cityEditText.setText(city);
            stateEditText.setText(state);
            zipEditText.setText(zip);
        }
    }//loadData

    //Will return true if there's already data, false if not
    public boolean isData(){
        Cursor cursor = dataManager.selectAll();
        return cursor.moveToFirst();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}//end PersonalizeDevice class

