package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public class TableFactory {

	private static final FriendTable mUserTable = new FriendTable();

	// 消息页---@我
	private static final AddMeTable mAddMeTable = new AddMeTable();
	// 消息页---评论
	private static final SayMeTable mSayMeTable = new SayMeTable();
	// 消息页---喜欢
	private static final FavoriteTable mFavoriteTable = new FavoriteTable();
	// 建议的地址
	private static final LocationTable mLocationTable = new LocationTable();
	// 历史记录
	private final static HistoryTable HISTORY_TABLE = new HistoryTable();
	// 历史记录中的记录内容
	private final static ContentTable CONTENT_TABLE = new ContentTable();
	// 历史记录中的记录图片
	private final static ImageTable IMAGE_TABLE = new ImageTable();

	public static void createIfNoExists(SQLiteDatabase db) {
		mUserTable.createIfNoExists(db);
		mAddMeTable.createTableIfNoExists(db);
		mSayMeTable.createTableIfNoExists(db);
		mFavoriteTable.createTableIfNoExists(db);
		mLocationTable.createTableIfNoExists(db);
		HISTORY_TABLE.createTableIfNoExists(db);
		CONTENT_TABLE.createTableIfNoExists(db);
		IMAGE_TABLE.createTableIfNoExists(db);

	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
