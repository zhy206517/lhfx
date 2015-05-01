package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public class FriendTable extends DBTable {
	FriendTable() {
		super(TABLE_NAME);
	}
	
	public static final String TABLE_NAME = "Friend";
	public static final String COLUMN_USERID = "userid"; 
	public static final String COLUMN_USERNAME = "username"; 
	public static final String COLUMN_IMAGEID = "imageid"; 
	public static final String COLUMN_SEX = "sex"; 
	public static final String COLUMN_CITY = "city"; 
	public static final String COLUMN_BIRTHDAYYEAR = "year";
	public static final String COLUMN_BIRTHDAYMONTH = "month"; 
	public static final String COLUMN_BIRTHDAYDAY = "day"; 
	public static final String COLUMN_INTRODUCE = "introduce"; 

	
	@Override
	void createTableIfNoExists(SQLiteDatabase db) {
		String sqlStr = " CREATE TABLE IF NOT EXISTS "
					  + TABLE_NAME 
					  + " ("
					  + " " + COLUMN_USERID + " TEXT NOT NULL PRIMARY KEY ,"
					  + " " + COLUMN_USERNAME + " TEXT," 
					  + " " + COLUMN_IMAGEID + " TEXT," 
					  + " " + COLUMN_SEX + " TEXT," 
					  + " " + COLUMN_CITY + " TEXT," 
					  + " " + COLUMN_BIRTHDAYYEAR + " int," 
					  + " " + COLUMN_BIRTHDAYMONTH + " int," 
					  + " " + COLUMN_BIRTHDAYDAY + " int," 
					  + " " + COLUMN_INTRODUCE + " TEXT" +
					  " )";
		db.execSQL(sqlStr);
	}

	@Override
	void createIndex(SQLiteDatabase db) {}
}
