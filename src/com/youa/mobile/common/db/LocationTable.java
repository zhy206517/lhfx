package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public class LocationTable extends DBTable {

	public static final String TABLE_NAME = "suggest_place";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_USER_ID = "u_id";
	public static final String COLUMN_SH_ID = "sh_id";
	public static final String COLUMN_PLACE_NAME = "place_name";
	public static final String COLUMN_PLACE_ID = "plid";
	public static final String COLUMN_PLACE_ADDR = "place_addr";
	public static final String COLUMN_PLACE_TYPE = "type";
	public static final String COLUMN_PLACE_X = "place_x";
	public static final String COLUMN_PLACE_Y = "place_y";
	public static final String COLUMN_TIME_STAMP = "timestamp";

	public LocationTable() {
		super(TABLE_NAME);
	}

	@Override
	void createIndex(SQLiteDatabase db) {

	}

	@Override
	void createTableIfNoExists(SQLiteDatabase db) {
		String sqlStr = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_ID
				+ " TEXT, " + COLUMN_SH_ID + " TEXT, " + COLUMN_PLACE_NAME
				+ " TEXT, " + COLUMN_PLACE_ID + " TEXT, " + COLUMN_PLACE_ADDR
				+ " TEXT, " + COLUMN_PLACE_TYPE + " TEXT, " + COLUMN_PLACE_X
				+ " TEXT, " + COLUMN_PLACE_Y + " TEXT, " + COLUMN_TIME_STAMP
				+ " TEXT " + ");";
		db.execSQL(sqlStr);

	}

	/**
	 * 变更列名
	 * 
	 * @param db
	 * @param oldColumn
	 * @param newColumn
	 * @param typeColumn
	 */
	public void updateColumn(SQLiteDatabase db, String oldColumn,
			String newColumn, String typeColumn) {
		try {
			db.execSQL("ALTER TABLE " + TABLE_NAME + " CHANGE " + oldColumn
					+ " " + newColumn + " " + typeColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 变更列名
	 * 
	 * @param db
	 * @param oldColumn
	 * @param newColumn
	 * @param typeColumn
	 */
	public void addColumn(SQLiteDatabase db, String newColumn, String typeColumn) {
		try {
			db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD " + newColumn + " "
					+ typeColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
