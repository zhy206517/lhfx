package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public abstract class DBTable {

	String mTableName;

	DBTable(String name) {
		mTableName = name;
	}

	public void createIfNoExists(SQLiteDatabase db) {
		createTableIfNoExists(db);
		createIndex(db);
	}

	abstract void createTableIfNoExists(SQLiteDatabase db);

	abstract void createIndex(SQLiteDatabase db);
}
