package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CollWeatherOpenHelper extends SQLiteOpenHelper {
public static final String CREATE_PROVINCE="CREATE TABLE PROVINCE ("
+"PROVINCE_ID INTEGER PRIMARY KEY AUTOINCREMENT ,"
+ "PROVINCE_NAME TEXT,"
+"PROVINCE_CODE TEXT )";
public static final String CREATE_CITY="CREATE TABLE CITY ("
+"CITY_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
+"CITY_NAME TEXT,"
+"CITY_CODE TEXT,"
+"PROVINCE_ID INTEGER)";
public static final String CREATE_COUNTY ="CREATE TABLE COUNTY ("
+"COUNTY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
+"COUNTY_NAME TEXT,"
+ "COUNTY_CODE TEXT," 
+"CITY_ID INTEGER )";
	public CollWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
   db.execSQL(CREATE_PROVINCE);
   db.execSQL(CREATE_CITY);
   db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
