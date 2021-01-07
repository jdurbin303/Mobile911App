package com.example.buoyv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

//Helps save user information in an SQL database
public class DataManager{
    private SQLiteDatabase db;

    public DataManager (Context context) {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public Cursor selectAll() {

        Cursor cursor = null;
        String query = "select * from buoy_users order by name";//limit 1?

        try {
            cursor = db.rawQuery(query, null);
            if (cursor == null){
                Log.i("info", "your cursor is still null");
            }
            Log.i ("info", "In Datamanager selectAll try statement");
        } catch (Exception e) {
            Log.i("info", "In DataManager selectAll method");
            Log.i("info", e.getMessage());
        }

        Log.i("info", "Loaded data " + cursor.getCount());
        return cursor;
    }

    public void insert(String name, String phone, String dob, String pronouns,
                       String street, String city, String state, String zip, String typeOfHelp, Location location) {

        String query = "insert into buoy_users" +
                "(name, phone, dob, pronouns, street, city, state, zip, typeOfHelp, location) values " +
                "( '" + name + "', '" + phone + "', '" + dob + "', '" + pronouns + "','" + street + "','" + city +
                "', '" + state + "', '" + zip + "', '" + typeOfHelp + "','" + location + "' )";

        try {
            db.execSQL(query);
            Log.d("added", "add data: adding " + name + " to buoy_users" );
        } catch (SQLException e) {
            Log.i("info", "In DataManager insert method");
            Log.i("info", e.getMessage());
        }
        Log.i("info", "Updated user " + name);
    }

    public void edit(User newUser){
        //We're always just going to change the top row of the table since it's only possible
        //to enter one user per device
        String whereClause = " _id = ?";
        Cursor cursor = selectAll();
        cursor.moveToFirst();
        String id = cursor.getString(0);
        String whereArgs[] = new String[]{id};

        //Then we need to populate the new content values based on newContact variables
        ContentValues values = new ContentValues();
        values.put("name", newUser.getName());
        //Log.i("Name", newUser.getName()); //having an issue with the last name not saving
        values.put("phone", newUser.getPhoneNumber());
        values.put("dob", newUser.getDob());
        values.put("pronouns", newUser.getPronouns());
        values.put("street", newUser.getStreetAddress());
        values.put("city", newUser.getCity());
        values.put("state", newUser.getState());
        values.put("zip", newUser.getZip());
        values.put("typeOfHelp", newUser.getTypeOfHelp());
        values.put("location", "null");
        //Call the update method and we're gucci!
        db.update("buoy_users",values,whereClause,whereArgs);

    }

    public void delete(User user){ //Contact contact
        //Looks for the contact by name and deletes that row of the given table
        //A little help from here: https://abhiandroid.com/database/operation-sqlite.html
        String whereClause = "_id=?";
        //int id = contact.getId();
        String whereArgs[] = new String[]{Integer.toString(user.getId())};
        db.delete("buoy_users", whereClause,whereArgs);

    }

    private class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper (Context context) {
            super(context, "Users", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String newTable = "create table buoy_users ("
                    + "_id integer primary key autoincrement not null, "
                    + "name text not null, "
                    + "phone text, "
                    + "dob text, "
                    + "pronouns text, "
                    + "street text, "
                    + "city text, "
                    + "state text, "
                    + "zip text, "
                    + "typeOfHelp text, "
                    + "location text)";
            try {
                db.execSQL(newTable);
            }
            catch (SQLException e) {
                Log.i ("info", "In MySQLiteOpenHelper class onCreate method");
                Log.i ("info", e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //No code needed
        }
    }
}