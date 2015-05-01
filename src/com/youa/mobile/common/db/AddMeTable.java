package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public class AddMeTable extends DBTable {

	public static final String TABLE_NAME = "Add_Me";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_USER_ID = "u_id";
	public static final String COLUMN_USER_NAME = "u_name";
	public static final String COLUMN_USER_IMAGE = "u_image"; 
	public static final String COLUMN_PUBLISH_TIME = "u_publish_time";
	public static final String COLUMN_ARTICLE_ID = "article_id"; 
	public static final String COLUMN_ARTICLE_TITLE = "article_title";
	public static final String COLUMN_ARTICLE_CONTENT = "article_content";
	public static final String COLUMN_ARTICLE_CONTENT_IMAGE = "article_content_image";
	public static final String COLUMN_ARTICLE_REPLY_NAME = "article_reply_name";
	public static final String COLUMN_ARTICLE_REPLY = "article_reply"; 
	public static final String COLUMN_ARTICLE_REPLY_IMAGE = "article_reply_image";
	public static final String COLUMN_ARTICLE_LIKE_COUNT = "article_like_count";
	public static final String COLUMN_ARTICLE_COMMENT_COUNT = "article_comment_count"; 
	public static final String COLUMN_ARTICLE_FORWARD_COUNT = "article_forward_count";

	public AddMeTable() {
		super(TABLE_NAME);
	}

	@Override
	void createIndex(SQLiteDatabase db) {
		
		
	}

	@Override
	void createTableIfNoExists(SQLiteDatabase db) {
		String sqlStr = "CREATE TABLE "
		      + TABLE_NAME
		      + " ("
		      + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		      + COLUMN_ARTICLE_ID + " TEXT, "
		      + COLUMN_USER_ID + " TEXT, "
		      + COLUMN_USER_NAME + " TEXT, "
		      + COLUMN_USER_IMAGE + " TEXT, " 
		      + COLUMN_PUBLISH_TIME + " TEXT, " 
		      + COLUMN_ARTICLE_TITLE + " TEXT, "
		      + COLUMN_ARTICLE_CONTENT + " TEXT, "
		      + COLUMN_ARTICLE_CONTENT_IMAGE + " TEXT, "
		      + COLUMN_ARTICLE_REPLY_NAME + " TEXT, "
		      + COLUMN_ARTICLE_REPLY + " TEXT, "
		      + COLUMN_ARTICLE_REPLY_IMAGE + " TEXT, "
		      + COLUMN_ARTICLE_LIKE_COUNT + " TEXT, "
		      + COLUMN_ARTICLE_COMMENT_COUNT + " TEXT, "
		      + COLUMN_ARTICLE_FORWARD_COUNT + " TEXT "
		      + ");";
		db.execSQL(sqlStr);
		
	}

}
