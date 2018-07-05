package com.asisdroid.drawfun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class GalleryDBAdapter extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "DrawAndFun.db";
    static final int DATABASE_VERSION = 1; //ADDED IMAGE NAME COLUMN IN THIS VERSION
    public static final int NAME_COLUMN = 1;
    private static final String default_image_name = "null";
    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table " + "MY_FAV_DRAWING_DETAILS" + "(FILENAME text)";
    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    // Database open/upgrade helper

    public GalleryDBAdapter(Context _context) {
        super(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // If you need to add a new column
        /*if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE MY_FAV_DRAWING_DETAILS ADD COLUMN IMAGENAME text DEFAULT null");
        }*/
        /*db.execSQL("DROP TABLE IF EXISTS EXPENSE_ACCOUNT_DETAILS");
        onCreate(db);*/
    }

    public GalleryDBAdapter open() throws SQLException {
        db = this.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertIntoFav(String favFileName) {

        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("FILENAME", favFileName);
        // Insert the row into your table
        db.insert("MY_FAV_DRAWING_DETAILS", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public boolean removeFromFav(String favFileName) {
        String[]whereArgs =  {favFileName};
        int numberOFEntriesDeleted = db.delete("MY_FAV_DRAWING_DETAILS", "FILENAME = ?",whereArgs);
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        if(numberOFEntriesDeleted==1){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isMyFavourite(String favFileName) {
        String[]whereArgs =  {favFileName};

        Cursor cursor = null;
        try{
            cursor = db.query("MY_FAV_DRAWING_DETAILS", new String[]{"FILENAME"}, "FILENAME = ?", whereArgs, null, null, null);
            if( cursor != null && cursor.moveToFirst() ) {
                return  true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null) cursor.close();
        }
        Log.d("karthi", "not available");
        return false;
    }
}

