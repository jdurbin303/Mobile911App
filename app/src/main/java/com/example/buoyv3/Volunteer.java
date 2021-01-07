package com.example.buoyv3;
import android.location.Location;

//Volunteer class really isn't implemented in this project, but is set as a foundation for
//building upon it in the future
public class Volunteer {

    //variables
    private String name;
    private String phoneNumber;
    private String pronouns;
    private Location currentLocation; //not a string irl, this is just a placeholder for now

    //constructor
    public Volunteer(String name, String phoneNumber, String pronouns, Location currentLocation) {

        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pronouns = pronouns;
        this.currentLocation = currentLocation;

    }//end constructor

    //getters and setters
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getPronouns(){return pronouns;}
    public void setPronouns(String pronouns){this.pronouns = pronouns;}

    public Location getCurrentLocation(){return currentLocation;}
    public void setCurrentLocation(Location currentLocation){this.currentLocation = currentLocation;}

}
