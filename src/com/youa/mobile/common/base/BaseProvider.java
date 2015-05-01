package com.youa.mobile.common.base;

import android.database.sqlite.SQLiteDatabase;

import com.youa.mobile.common.db.ConnectManager;

public class BaseProvider {
	protected static final String TAG = BaseProvider.class.getSimpleName();
	
	public BaseProvider() {
		
	}
	
	public SQLiteDatabase getWritableDatabase() {
		return ConnectManager.getWriteDB();
	}
	
	public SQLiteDatabase getReadableDatabase() {
		return ConnectManager.getReadDB();
	}
	
}
