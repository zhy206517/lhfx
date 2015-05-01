package com.youa.mobile.common.db;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;

import com.youa.mobile.SystemConfig;

public class ConnectManager {

	private static Context mContext;

	private static String mDatabaseName;
	
	private static SQLiteDatabase mWriteDB = null;
	private static SQLiteDatabase mReadDB = null;
	
	private static DBConnector mDBConnector = null;

	private static int times = 0;
	
	public static List<Long> mThreadIDList = new LinkedList<Long>();

	public static void initDBConnector(Context context) {
		mContext = context;
	}
	
	public static synchronized SQLiteDatabase getWriteDB() {
		addThreadID();
		
		if (mWriteDB == null) {
			mWriteDB = getDBConnector().getWritableDatabase();			
		}
		
		return mWriteDB;
	}

	public static synchronized SQLiteDatabase getReadDB() {
		addThreadID();
		
		if (mReadDB == null) {
			mReadDB = getDBConnector().getReadableDatabase();
		}
		
		return mReadDB;
	}
	
	private static void addThreadID() {
		Long threadID = Thread.currentThread().getId();
		
		if ( ! mThreadIDList.contains(threadID)) {
			mThreadIDList.add(Thread.currentThread().getId());
		}
	}
	
	private static DBConnector getDBConnector() {
		if (mDBConnector == null) {
			mDBConnector = new DBConnector(mContext, getDatabaseName());
		}
		
		return mDBConnector;
	}
	
	public static synchronized void closeDB() {
		mThreadIDList.remove(Thread.currentThread().getId());
		
		if (mThreadIDList.size() == 0 && mDBConnector != null) {
			mDBConnector.close();
			mWriteDB = null;
			mReadDB = null;
			mDBConnector = null;
		}
	}
	
	public static void reset() {
		times ++ ;
		
		if (mThreadIDList.size() > 0) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(times > 3) {
			} else {
				reset();
			}
		}
		
		times = 0;
		
		synchronized(ConnectManager.class) {
			if (mDBConnector != null) {
				mDBConnector.close();
			}
			
			mWriteDB = null;
			mReadDB = null;
			mThreadIDList.clear();		
			mDBConnector = null;
		}
	}

	public static synchronized void setDatabaseName(String databaseName) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
		
		Editor editor = sharedPreferences.edit();
		editor.putString(SystemConfig.KEY_DATATABASE_NAME, databaseName);
		editor.commit();
		
		mDatabaseName = databaseName;
		
		reset();
	}
	
	public static synchronized String getDatabaseName() {
		if (mDatabaseName == null) {
			SharedPreferences sharedPreferences = mContext.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
			mDatabaseName = sharedPreferences.getString(SystemConfig.KEY_DATATABASE_NAME, null);
		}
		
		return mDatabaseName;
	}
	
}
