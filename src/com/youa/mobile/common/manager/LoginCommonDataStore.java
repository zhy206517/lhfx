package com.youa.mobile.common.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.impl.client.BasicCookieStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.youa.mobile.SystemConfig;

public class LoginCommonDataStore extends BasicCookieStore {
	public static final String KEY_SESSIONID = SystemConfig.KEY_SESSION_PREFIX;
	public static final String KEY_YOUAINDENTITY = SystemConfig.KEY_YOUAINDENTITY_PREFIX;
	public static final String KEY_USERID = SystemConfig.KEY_USERID_PREFIX;
	public static final String KEY_NICKNAME = SystemConfig.KEY_USER_NICKNAME;
	
	public static final String KEY_THIRD_USERID = SystemConfig.KEY_THIRD_USERID;
	public static final String KEY_THIRD_SITE_TYPE = SystemConfig.KEY_THIRD_SITE;
	
	private static final Map<String, String> sessionMap = new HashMap<String, String>();
	public LoginCommonDataStore() {
		super();
	}
	
//	public static synchronized void clearLoginData(
//			Context context, 
//			String userName) {
//		sessionMap.clear();
//		SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
//		Editor editor = sharedPreferences.edit();
//		editor.remove(KEY_SESSIONID + userName);
//		editor.remove(KEY_YOUAINDENTITY + userName);
//		editor.remove(KEY_USERID + userName);
//		editor.remove(SystemConfig.KEY_LOGIN_NAME);
//		editor.remove(SystemConfig.KEY_LOGIN_PASS);
//		editor.commit();
//
//	}
	
	public static synchronized void saveDataIfNeed(
			Context context, 
			String key,
			String userName, 
			String dataValue) {
		String savekey = key+userName;

		sessionMap.put(savekey, dataValue);
		SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
		Editor editor = sharedPreferences.edit();
		editor.putString(savekey, dataValue);
		editor.commit();
		
	}

	public static synchronized String getData(Context context, String key, String userName) {
		String savekey = key+userName;
		String sessionId = sessionMap.get(savekey);
		if(sessionId == null) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
			//---------------临时代码，用于处理版本升级userName为空，键值获取不到--------------------
			Map keyMap = sharedPreferences.getAll();
			if(keyMap != null) {
				Set keySet = keyMap.keySet();
				for (Iterator iter = keySet.iterator();iter.hasNext();) {
					String _key = (String) iter.next();
					if (_key.startsWith(key)) {
						sessionId = (String) keyMap.get(_key);
					}
				}
			}
			//------------------------------------------------------------------------
			// 后续版本应修改为直接获取
			//sessionId = sharedPreferences.getString(savekey, null);	
			//------------------------------------------------------------------------		
			if(sessionId != null
					&& !sessionId.equals("")
					&& !sessionId.equals("null")) {
				sessionMap.put(savekey, sessionId);
			}
		}
		if(sessionId != null && sessionId.equals("")) {
			sessionId = null;
		}
		return sessionId;
		
	}
	
	public static void logout(Context context, String key, String userName) {
		sessionMap.clear();
		SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
		Editor editor = sharedPreferences.edit();
		String savekey = key+userName;
		editor.remove(savekey);
		editor.remove(KEY_SESSIONID);
		editor.remove(KEY_YOUAINDENTITY);
		editor.remove(KEY_USERID);
		editor.remove(KEY_NICKNAME);
		editor.remove(SystemConfig.KEY_LOGIN_FROM_TYPE);
		editor.commit();
	}
}
