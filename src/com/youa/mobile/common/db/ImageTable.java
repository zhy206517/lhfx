package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public class ImageTable extends DBTable {

	public static final String TABLE_NAME = "image_table";

	public ImageTable() {
		super(TABLE_NAME);
	}

	@Override
	void createIndex(SQLiteDatabase db) {

	}

	@Override
	void createTableIfNoExists(SQLiteDatabase db) {
		String sqlStr = "CREATE TABLE " + TABLE_NAME + " (" 
				+ TableColumns.LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 	//
				+ ImageColumns.POST_ID + " TEXT, "
				+ ImageColumns.IMG_CONTENT_ID + " TEXT, " 
				+ ImageColumns.IMG_DESC + " TEXT, " 
				+ ImageColumns.HEIGHT + " INTEGER, "
				+ ImageColumns.WIDTH + " INTEGER, "
				+ ImageColumns.EXT_DATA1 + " TEXT, "
				+ ImageColumns.EXT_DATA2 + " TEXT, "
				+ ImageColumns.EXT_DATA3 + " TEXT, "
				+ ImageColumns.EXT_DATA4 + " TEXT, "
				+ ImageColumns.EXT_DATA5 + " TEXT);";
		db.execSQL(sqlStr);

	}
	
	public interface ImageColumns extends TableColumns{
		public static final String POST_ID = "postId";
		public static final String IMG_CONTENT_ID = "img_content_id";
		public static final String IMG_DESC = "img_desc";
		public static final String HEIGHT = "height";
		public static final String WIDTH = "width";
	}
}
