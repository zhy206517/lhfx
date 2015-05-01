package com.youa.mobile.common.base;

import android.database.sqlite.SQLiteDatabase;

import com.youa.mobile.common.db.ConnectManager;

public class BaseManager {

	protected BaseManager() {
		
	}
	
	protected SQLiteDatabase getWritableDatabase() {
		return ConnectManager.getWriteDB();
	}
	
	protected SQLiteDatabase getReadableDatabase() {	
		return ConnectManager.getReadDB();
	}
	
	public void closeDB() {
		ConnectManager.closeDB();
	}
	
}
