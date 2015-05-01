package com.youa.mobile;

public class SystemConfig {

	public static final String KEY_DATATABASE_NAME = "datatabase_";
	public static final String XML_FILE_SYSTEM_CONFIG = "system_config";
	public static final String XML_NEW_NEWS_COUNT = "news_new_count";
	public static final String KEY_LOGIN_NAME = "login_name";
	public static final String KEY_LOGIN_PASS = "login_pass";
	public static final String KEY_LOGIN_SHOWNAME = "login_showname";
	public static final String KEY_USER_NICKNAME = "user_nickname";
	public static final String KEY_SESSION_PREFIX = "sessionid_";
	public static final String KEY_YOUAINDENTITY_PREFIX = "youaindentiry_";
	public static final String KEY_USERID_PREFIX = "userid_";

	public static final String KEY_LOGIN_FROM_TYPE = "login_from_type";

	public static final String KEY_THIRD_USERID = "third_userid";
	public static final String KEY_THIRD_SITE = "third_site";

	public final static String XML_FILE_LOGIN_GUIDE = "login_guide";

	public final static String XML_FILE_LOCATION_GUIDE = "location_config";
	public final static String KEY_PLACE_X = "place_x";
	public final static String KEY_TIME_TMP = "timetmp";
	public final static String KEY_PLACE_Y = "place_y";
	public final static String KEY_LOCATION_NAME = "address";
	public final static String KEY_CITY_NAME = "city";

	public final static String XML_BROWSER_MODLE_CONFIG_BOOLEAN = "is_gaoqing";
	public final static String XML_SYNC_SINA_CONFIG_BOOLEAN = "is_sync_sina";
	public final static String XML_SYNC_QQ_CONFIG_BOOLEAN = "is_sync_qq";
	public final static String XML_SYNC_RENREN_CONFIG_BOOLEAN = "is_sync_renren";

	public static final String PACKAGE_NAME = SystemConfig.class.getPackage()
			.getName();
	public static final String ACTION_REFRESH_HOME = "leho.intent.action.REFRESH_HOME";
	public static final String ACTION_REFRESH_NEWS = "leho.intent.action.REFRESH_NEWS";
	public static final String ACTION_REFRESH_MY = "leho.intent.action.REFRESH_MY";
	public static final String ACTION_REFRESH_USER_INFO_UPDATE = "leho.intent.action.USER_INFO_UPDATE";

	// for jing pin tui jian
	public static final String JPTJ_PREF_NAME = "jptj_pref";
	public static final String KEY_JPTJ = "key_jptj";

	// for waterfall list view
	/**
	 * -1 - default<br>
	 * 0 - need not to change <br>
	 * other - screen width
	 */
	public static int SCREEN_WIDTH = -1;

	public final static String XML_FILE_TAG_CACHE = "cache_tag";
	public final static String KEY_TAG_INFO = "tag_info";
	public final static String KEY_ABLUM_INFO = "ablum_info";
	public final static String KEY_TAG_TMP = "timetmp";
}
