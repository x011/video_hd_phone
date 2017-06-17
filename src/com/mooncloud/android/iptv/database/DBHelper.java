package com.mooncloud.android.iptv.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	public static final String DATABASE_NAME = "vodDatabase";
	public static final String TABLE_VOD_PASSWORD = "password";
	public static final int VERSION = 1;
	public static final String TAG = "Database";

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		  String vodHistorySql = "create table if not exists " + TABLE_VOD_PASSWORD +
				" (id integer primary key autoincrement," +
		  		"password varchar(100))";
		  db.execSQL(vodHistorySql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	
}
