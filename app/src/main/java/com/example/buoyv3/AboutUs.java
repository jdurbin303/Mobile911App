package com.example.buoyv3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

//About us class doesn't do much
//Implements menu button and navigation drawer
//really just shows stuff in the xml file, it's just info and text for the user
public class AboutUs extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_about_us); //we set the content as the menu, not the current activity

        //Create menu button
        Button menuButton = (Button) findViewById(R.id.menu_button2);

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
                    startActivity(new Intent(AboutUs.this, MainActivity.class));
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }
                if(id==R.id.nav_aboutUs){
                    //We're already here so just close the drawer
                    Log.i("selected", "nav_aboutUs");
                    menuDrawer.closeDrawer(Gravity.LEFT);
                }
                if (id == R.id.nav_personalize) {
                    Log.i("selected", "nav_personalize");
                    startActivity(new Intent(AboutUs.this, PersonalizeDevice.class));
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

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.activity_about_us, container, false);
    }


}
