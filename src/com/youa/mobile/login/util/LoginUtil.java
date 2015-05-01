package com.youa.mobile.login.util;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.NetworkStatus;
import com.youa.mobile.login.action.GetHomeDataAction;
import com.youa.mobile.login.auth.BaiduToken;
import com.youa.mobile.login.auth.BaseAuthPage;
import com.youa.mobile.login.auth.QQToken;
import com.youa.mobile.login.auth.RenrenToken;
import com.youa.mobile.login.auth.SinaToken;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.login.data.UserInfo;

public class LoginUtil {

	public static final boolean DEBUG = true;

	public static void LOGD(String tag, String msg) {
		if (DEBUG)
			Log.d("#login#" + tag, "----------------->" + msg);
	}

	public static boolean isEmpty(String str) {
		if (null == str || (str != null && str.length() > 0)) {
			return false;
		}
		return true;
	}

	public static boolean isUsernameAvailable(String str) {
		if (isEmpty(str)) {
			return false;
		}
		return true;
	}

	public static boolean isChineseNumberChar(String str) {
		String patternStr = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{3,15}$";
		Pattern pattern = Pattern.compile(patternStr);
		return pattern.matcher(str).matches();
	}

	public static boolean isPasswordAvailable(String str) {
		if (isEmpty(str)) {
			return false;
		}
		return true;
	}

	public static boolean isConPasswordAvailable(String pass, String conString) {
		if (isEmpty(conString) || !conString.equals(pass)) {
			return false;
		}
		return true;
	}

	public static boolean isEmailAvailable(String email) {
		if (isEmpty(email)) {
			return false;
		}
		return true;
	}

	// 判断字符串是否为数字
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static void writeThirdUserToPrefForAutoLogin(Context context, String uid, SupportSite site) {
		SharedPreferences preference = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(SystemConfig.KEY_THIRD_USERID, uid);
		editor.putString(SystemConfig.KEY_THIRD_SITE, site.getSiteTag());
		editor.commit();
	}

	public static void writeUserToPrefForAutoLogin(Context context, String username, String password) {
		SharedPreferences preference = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(SystemConfig.KEY_LOGIN_NAME, username);
		// editor.putString(SystemConfig.KEY_LOGIN_PASS, password);
		editor.commit();
	}

	public static UserInfo readUserFromPrefForAutoLogin(Context context) {
		// SupportSite site = readLoginFromType(context);
		// SharedPreferences preference = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_READABLE);
		// if(site == null){
		// String username = preference.getString(SystemConfig.KEY_LOGIN_NAME, "");
		// String password = preference.getString(SystemConfig.KEY_LOGIN_PASS, "");
		// if (LoginUtil.isUsernameAvailable(username) && LoginUtil.isPasswordAvailable(password)) {
		// return new UserInfo(username, password);
		// }
		// }else{
		// String thirdUid = preference.getString(SystemConfig.KEY_THIRD_USERID, "");
		// String siteValue = preference.getString(SystemConfig.KEY_THIRD_SITE, "");
		// for(SupportSite supportSite : SupportSite.values()){
		// if(supportSite.getSiteTag().equals(siteValue)){
		// site = supportSite;
		// }
		// }
		// if(LoginUtil.isUsernameAvailable(thirdUid) && null != site){
		// return new UserInfo(thirdUid, site);
		// }
		// }
		//
		// return null;
		SharedPreferences preference = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_READABLE);
		String youaSession = preference.getString(SystemConfig.KEY_SESSION_PREFIX + "", "");
		if (LoginUtil.isUsernameAvailable(youaSession)) {
			UserInfo userInfo = new UserInfo();
			userInfo.setYouaSession(youaSession);
			userInfo.setThirdUid(preference.getString(SystemConfig.KEY_THIRD_USERID, ""));
			SupportSite site = SupportSite.parseSite(preference.getString(SystemConfig.KEY_THIRD_SITE, ""));//userInfo.setSite(site);
			userInfo.setSite(site);
			return userInfo;
		}

		return null;
	}

	/**
	 * 写入登录类型；
	 * 
	 * @param context
	 * @param site
	 *            为null则是乐活登录类型
	 * @return
	 */
	public static void writeLoginFromType(Context context, SupportSite site) {
		SharedPreferences preference = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = preference.edit();
		if (site != null) {
			editor.putString(SystemConfig.KEY_LOGIN_FROM_TYPE, site.getSiteTag());
			editor.commit();
		}
	}

	/**
	 * 读取登录类型；
	 */
	public static SupportSite readLoginFromType(Context context) {
		SharedPreferences preference = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, Context.MODE_WORLD_WRITEABLE);
		String type = preference.getString(SystemConfig.KEY_LOGIN_FROM_TYPE, null);
		for (SupportSite site : SupportSite.values()) {
			if (site.getSiteTag().equals(type)) {
				return site;
			}
		}
		return null;
	}

	public static void getSaveHomeData(Context context) {
		ActionController.post(context, GetHomeDataAction.class, null, new GetHomeDataAction.DataResultListner() {
			@Override
			public void onFinish() {
				LOGD("getSaveHomeData", "GetHomeDataAction finish <login> : ");
			}

			@Override
			public void onFail() {
				LOGD("getSaveHomeData", "GetHomeDataAction fail <login> : ");
			}
		}, true);
	}

	public enum LoginFromType {
		FROM_THIRD("third"), FROM_LEHO("local");
		private String value;

		LoginFromType(String type) {
			this.value = type;
		}

		public String getType() {
			return value;
		}
	}

	public static String parseSiteType(String site) {
		// 第三方类型
		// 'THIRD_TYPE_NO' => 0, //无
		// 'THIRD_TYPE_BAIDU' => 1, //百度第三方
		// 'THIRD_TYPE_SINA' => 2, //sina
		// 'THIRD_TYPE_RENREN' => 4, //renren
		// 'THIRD_TYPE_QQ' => 8, //qq
		// 'THIRD_TYPE_KAIXIN' => 16, //kaixin
		if (null == site || "".equals(site)) {
			return "0";
		} else if ("baidu".equals(site)) {
			return "1";
		} else if ("sina".equals(site)) {
			return "2";
		} else if ("renren".equals(site)) {
			return "4";
		} else if ("qq".equals(site)) {
			return "8";
		} else if ("kaixin".equals(site)) {
			return "16";
		} else {
			return "0";
		}
	}

	public static void openThirdAuthPage(BaseAuthPage context, SupportSite site) {
		if (!NetworkStatus.isNetworkAvailable(context)) {
			Toast.makeText(context, R.string.common_network_not_available, Toast.LENGTH_SHORT).show();
			return;
		}

		switch (site) {
		case BAIDU:
			BaiduToken baiduToken = new BaiduToken(context);
			baiduToken.initToken(context);
			baiduToken.getAuthToken(context);
			break;
		case SINA:
			SinaToken sinaToken = new SinaToken(context);
			sinaToken.initToken(context);
			sinaToken.getAuthToken(context);
			break;
		case QQ:
			QQToken qqToken = QQToken.getInstance(context);
			qqToken.initToken(context);
			qqToken.getAuthToken(context);
			break;
		case RENREN:
			RenrenToken renrenToken = RenrenToken.getInstance(context);
			renrenToken.getAuthToken(context);
			break;
		default:
			break;
		}
	}
}
