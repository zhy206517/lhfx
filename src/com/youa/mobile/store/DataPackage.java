package com.youa.mobile.store;

import android.net.Uri;
import android.provider.BaseColumns;

public class DataPackage {
	public static final String AUTHORITY = "com.lehuo.mobile.provider";
	public static final String DB_NAME = "lehuo.db";
	public static final int DB_VERSION = 1;

	// -------------------------数据表-------------------------
	public static final String TB_ACCOUNT_NAME = "account";
	public static final String TB_PICTURE_NAME = "picture";
	public static final String TB_FEED_NAME = "feed";
	public static final String TB_INFORMATION_NAME = "information";
	public static final String TB_PROFILE_NAME = "profile";

	// -------------------------code-------------------------
	public static final int CODE_ACCOUNT = 1;
	public static final int CODE_PICTURE = 2;
	public static final int CODE_PICTURE_ITEM = 3;
	public static final int CODE_FEED = 4;
	public static final int CODE_FEED_ITEM = 5;
	public static final int CODE_INFROMATION = 6;
	public static final int CODE_INFROMATION_ITEM = 7;
	public static final int CODE_PROFILE = 8;
	// -------------------------uri-------------------------
	public static final String URL_ACCOUNT = "account";
	public static final String URL_PICTURE = "pictures";
	public static final String URL_FEED = "feed";
	public static final String URL_INFORMATION = "informations";
	public static final String URL_PROFILE = "profile";

	// -------------------------数据结构-------------------------
	public static final class Account implements BaseColumns {
		// -------------------------数据库字段-------------------------
		public static final String USER_ID = "id";
		public static final String USER_NAME = "name";
		public static final String USER_PASSWORD="password";
		public static final String USER_SESSION = "session";
		public static final String USER_DENTITY = "dentity";
		// -------------------------uri-------------------------
		public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/"
				+ URL_ACCOUNT);
		// -------------------------mime-------------------------
		public static final String ACCOUNT_TYPE = "vnd.android.cursor.dir/com.youa.mobile.account";

	}

	public static final class Picture implements BaseColumns {
		// asc 升序,desc降序
		// -------------------------数据库字段-------------------------
		public static final String URL = "url";
		public static final String FILE_NAME = "_data";
		public static final String FILE_LENGTH = "length";

		// -------------------------uri-------------------------
		public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/"
				+ URL_PICTURE);
		// -------------------------mime-------------------------
		public static final String PICTURE_TYPE = "vnd.android.cursor.dir/com.youa.mobile.picture";
		public static final String PICTURE_ITEM_TYPE = "vnd.android.cursor.item/com.youa.mobile.picture.item";
	}

	public static final class Feed implements BaseColumns {
		// 存的应该是解析后的字段
		// feed的数据结构直接存在数据库里
		// 缓存数大于最大值时，删除掉前面的
		// 头像是否原图
		// -------------------------数据库字段-------------------------
		public static final String FEED_ID = "id";
		public static final String FEED_NAME = "name";
		public static final String FEED_HEAD_URL = "head_url";// 头像数据存在数据库里
		public static final String FEED_TIME = "time";
		public static final String FEED_LIKE_NUM = "like_num";
		public static final String FEED_COMMENT_NUM = "comment_num";
		public static final String FEED_TRANSPOND_NUM = "transpond_num";

		public static final String FEED_CONTENT = "content";// 包括：文字加单图，链接，图集，表情
		// -------------------------uri-------------------------
		public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/"
				+ URL_FEED);
		// -------------------------mime-------------------------
		public static final String FEED_TYPE = "vnd.android.cursor.dir/com.youa.mobile.feed";
		public static final String FEED_ITEM_TYPE = "vnd.android.cursor.item/com.youa.mobile.feed.item";
	}

	public static final class Information implements BaseColumns {
		// -------------------------数据库字段-------------------------
		// -------------------------uri-------------------------
		public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/"
				+ URL_INFORMATION);
		// -------------------------mime-------------------------
		public static final String INFORMATION__TYPE = "vnd.android.cursor.dir/com.youa.mobile.information";
		public static final String INFORMATION_ITEM_TYPE = "vnd.android.cursor.item/com.youa.mobile.information.item";
	}

	public static final class Profile implements BaseColumns {
		// -------------------------数据库字段-------------------
		// -------------------------uri-------------------------
		public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/"
				+ URL_PROFILE);
		// -------------------------mime-------------------------
		public static final String PROFILE_TYPE = "vnd.android.cursor.dir/com.youa.mobile.profile";
	}

}
