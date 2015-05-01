package com.youa.mobile.common.base;

import android.database.sqlite.SQLiteDatabase;

import com.youa.mobile.common.db.ConnectManager;

public class BaseDao {
	
	public BaseDao() {
		
	}
	
	public SQLiteDatabase getWritableDatabase() {
		return ConnectManager.getWriteDB();
	}
	
	public SQLiteDatabase getReadableDatabase() {
		return ConnectManager.getReadDB();
	}
	
}
