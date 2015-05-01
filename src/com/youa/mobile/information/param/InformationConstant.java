package com.youa.mobile.information.param;

public class InformationConstant {
	//个人资料
	public static final String KEY_METHOD_GETPROFILE="getProfile";
	public static final String KEY_UID="testparam";
	
	public static final String result_key_uid = "userid";
	public static final String result_key_loginpass = "loginpass";
	public static final String result_key_email = "email";
	public static final String result_key_mobilephone = "mobilephone";
	public static final String result_key_name_sign = "name_sign";//TODO ?
	public static final String result_key_username = "nickname";//username
	public static final String result_key_thirdid = "thirdid";//TODO ?
	public static final String result_key_third_type = "third_type";//TODO ?
	public static final String result_key_login_type = "login_type";//TODO ?
	public static final String result_key_userstate = "userstate";//TODO ?
	public static final String result_key_regtime = "regtime";
	public static final String result_key_lasttime = "lasttime";
	public static final String result_key_regip = "regip";
	public static final String result_key_lastip = "lastip";
	public static final String result_key_figure = "figure";
	public static final String result_key_realname = "realname";
	public static final String result_key_sex = "sex";
	public static final String result_key_birth_year = "birth_year";
	public static final String result_key_birth_month = "birth_month";
	public static final String result_key_birth_day = "birth_day";
	public static final String result_key_province = "province";
	public static final String result_key_city = "city";
	public static final String result_key_head_imid = "head_imid";//head_imid
	public static final String result_key_district = "district";
	public static final String result_key_head_img_source = "head_img_source";
	public static final String result_key_level = "level";//TODO ?
	public static final String result_key_type = "type";//TODO ?
	public static final String result_key_signature = "signature";
	public static final String result_key_va_notify_types = "va_notify_types";//TODO ?
	public static final String result_key_va_photo_imid = "photo_imid";
	public static final String result_key_work_plid = "work_plid";
	public static final String result_key_life_plid = "life_plid";
	public static final String result_key_lastest_postid = "lastest_postid";//TODO ?
	public static final String result_key_lastest_post_time = "lastest_post_time";//TODO ?
	public static final String result_key_auth_remark = "auth_remark";//TODO ?
	public static final String result_key_cs_remark = "cs_remark";//TODO ?
	public static final String result_key_profile_imid = "profile_imid";//TODO ?
	
	//关注粉丝
	public static final String KEY_METHOD_ATTENTLIST="getFollowListWl";
	public static final String KEY_ATTENT_UID = "uid";
	public static final String KEY_ATTENT_OBJ_UID = "obj_uid";
	public static final String KEY_ATTENT_TYPE = "type";//1表示org_uid的关注列表，2表示org_uid的粉丝列表
	public static final String KEY_ATTENT_OFFSET = "offset";//分页起始值
	public static final String KEY_ATTENT_LIMIT = "limit";//当页显示数
	
	public static final String VALUE_ATTENT_TYPE_ATTENT = "1";//关注
	public static final String VALUE_ATTENT_TYPE_FANS = "2";//粉丝
	
}
