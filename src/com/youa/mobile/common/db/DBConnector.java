package com.youa.mobile.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnector extends SQLiteOpenHelper {

	private Context mContext;
	
	private static int DB_VERSION = 1;
	
	DBConnector(Context context, String name) {
		super(context, name, null, DB_VERSION);
		
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		TableFactory.createIfNoExists(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		TableFactory.onUpgrade(db, oldVersion, newVersion);
	}

}
