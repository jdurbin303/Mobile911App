package com.example.buoyv3;
import android.location.Location;

//User class with variables, getters and setters, etc
public class User {

    //Fields for user class
    private int id;
    private String name;
    private String phoneNumber;
    private String dob;
    private String pronouns;
    private String streetAddress;
    private String city;
    private String state;
    private String zip;
    private String typeOfHelp;
    private Location currentLocation; //Def not a string but i'll just leave this as a place holder for now

    //constructor
    public User(String name, String phoneNumber, String dob, String pronouns, String streetAddress,
                String city, String state, String zip) {
        this.id = 0; //place holder, we will assign the actual id later
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pronouns = pronouns;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.typeOfHelp = null;
        this.currentLocation = null;
        this.dob = dob;
    }//end constructor

    //second constructor
    public User(){
        this.id = 0;
        this.name = "";
        this.phoneNumber = "";
        this.pronouns = "";
        this.streetAddress = "";
        this.city = "";
        this.state = "";
        this.zip = "";
        this.typeOfHelp = "";
        this.currentLocation = null;
        this.dob = "";
    }

    //getter and setters
    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getPronouns(){return pronouns;}
    public void setPronouns(String pronouns){this.pronouns = pronouns;}

    public String getStreetAddress(){return streetAddress;}
    public void setStreetAddress(String streetAddress){this.streetAddress = streetAddress;}

    public String getCity(){return city;}
    public void setCity(String city){this.city = city;}

    public String getState(){return state;}
    public void setState(String state){this.state = state;}

    public String getZip(){return zip;}
    public void setZip(String zip){this.zip = zip;}

    public String getTypeOfHelp(){return typeOfHelp;}
    public void setTypeOfHelp(String s){this.typeOfHelp = typeOfHelp;}

    public Location getCurrentLocation(){return currentLocation;}
    public void setCurrentLocation(Location currentLocation){this.currentLocation = currentLocation;}

    public String getDob(){return dob;}
    public void setDob(String dob) { this.dob = dob; }
}
