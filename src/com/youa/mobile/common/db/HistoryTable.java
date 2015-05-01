package com.youa.mobile.common.db;

import android.database.sqlite.SQLiteDatabase;

public class HistoryTable extends DBTable {

	public static final String TABLE_NAME = "history_table";

	public HistoryTable() {
		super(TABLE_NAME);
	}

	@Override
	void createIndex(SQLiteDatabase db) {
		
	}
	
	@Override
	void createTableIfNoExists(SQLiteDatabase db) {
		String sqlStr = "CREATE TABLE " + TABLE_NAME + " (" 
				+ TableColumns.LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 	//
				+ HistoryColumns.POST_ID + " TEXT, " 
				+ HistoryColumns.U_ID + " TEXT, " 
				+ HistoryColumns.SEX + " INTEGER, "
				+ HistoryColumns.TYPE + " INTEGER, "
				+ HistoryColumns.FEED_TYPE + " TEXT, "
				+ HistoryColumns.IMG_HEAD_ID + " TEXT, " 
				+ HistoryColumns.NAME + " TEXT, " 
				+ HistoryColumns.TIME + " TEXT, "
				+ HistoryColumns.TIMELINE + " TEXT, "
				+ HistoryColumns.PLACE + " TEXT, "
				+ HistoryColumns.PRICE + " TEXT, "
				+ HistoryColumns.FROMWHERE + " TEXT, "
				+ HistoryColumns.LIKE_NUM + " TEXT, "
				+ HistoryColumns.COMMENT_NUM + " TEXT, "
				+ HistoryColumns.TRANSPOND_NUM + " TEXT, "
				+ HistoryColumns.NAMECHARSEQUENCE + " TEXT, "
				+ HistoryColumns.CHARSEQUENCE + " TEXT, "
				+ HistoryColumns.ORIGIN_POST_ID + " TEXT, "
				+ HistoryColumns.USER_TYPE + " INTEGER, "
				+ HistoryColumns.CREATE_TIME + " TEXT, "
				+ HistoryColumns.EXT_DATA1 + " TEXT, "
				+ HistoryColumns.EXT_DATA2 + " TEXT, "
				+ HistoryColumns.EXT_DATA3 + " TEXT, "
				+ HistoryColumns.EXT_DATA4 + " TEXT, "
				+ HistoryColumns.EXT_DATA5 + " TEXT);";
		db.execSQL(sqlStr);

	}

	public interface HistoryColumns extends TableColumns{
		public static final String POST_ID = "postId";
		public static final String U_ID = "uId";
		public static final String SEX = "sex";
		//0:普通 1:专家2：达人 3商户
		public static final String TYPE = "type";
		public static final String FEED_TYPE = "feedType";
		public static final String IMG_HEAD_ID = "img_head_id";
		public static final String NAME = "name";
		public static final String TIME = "time";
		public static final String TIMELINE = "timeLine";
		public static final String PLACE = "place";
		public static final String PRICE = "price";
		public static final String FROMWHERE = "fromWhere";
		public static final String LIKE_NUM = "like_num";
		public static final String COMMENT_NUM = "comment_num";
		public static final String TRANSPOND_NUM = "transpond_num";
		public static final String NAMECHARSEQUENCE = "nameCharSequence";
		public static final String CHARSEQUENCE = "charSequence";
		public static final String ORIGIN_POST_ID = "origin_post_id";
		/**
		 * 0 - public user<br>1 - orign user<br>2 - transpond user
		 */
		public static final String USER_TYPE = "user_type";
		public static final String CREATE_TIME = "create_time";
	}
}
