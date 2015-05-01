package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public class ContentTable extends DBTable {

	public static final String TABLE_NAME = "content_table";

	public ContentTable() {
		super(TABLE_NAME);
	}

	@Override
	void createIndex(SQLiteDatabase db) {

	}

	@Override
	void createTableIfNoExists(SQLiteDatabase db) {
		String sqlStr = "CREATE TABLE " + TABLE_NAME + " (" 
				+ TableColumns.LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 	//
				+ ContentColumns.POST_ID + " TEXT, "
				+ ContentColumns.HREF + " TEXT, " 
				+ ContentColumns.STR + " TEXT, " 
				+ ContentColumns.TYPE + " INTEGER, "
				+ ContentColumns.EXT_DATA1 + " TEXT, "
				+ ContentColumns.EXT_DATA2 + " TEXT, "
				+ ContentColumns.EXT_DATA3 + " TEXT, "
				+ ContentColumns.EXT_DATA4 + " TEXT, "
				+ ContentColumns.EXT_DATA5 + " TEXT);";
		db.execSQL(sqlStr);

	}
	
	public interface ContentColumns extends TableColumns{
		public static final String POST_ID = "postId";
		public static final String HREF = "href";
		public static final String STR = "str";
		public static final String TYPE = "type";
	}
}
