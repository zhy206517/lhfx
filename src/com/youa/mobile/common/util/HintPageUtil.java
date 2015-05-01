package com.youa.mobile.common.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.widget.HintPage;

public class HintPageUtil {
	public static final String HINT_HOME = "hint_home";
	public static final String HINT_USEREDIT = "hint_useredit";
	public static final String HINT_USEREDITHEADER = "hint_usereditheader";
	public static final String HINT_HOMEBOTTOM = "hint_homebottom";
	public static final String HINT_USERLIFE = "hint_userlife";
//	public static final String HINT_CONSUMERINFORMATION = "consumerInformation";
	public static Map<String,Boolean> hintMap = new HashMap<String, Boolean>();
	public static void checkHint(final String key, final Context context, final Handler mHandler) {
		if(!hintMap.containsKey(key)) {
			SharedPreferences sharedPreferences = 
				context.getSharedPreferences(SystemConfig.XML_FILE_LOGIN_GUIDE, 0);
			boolean isHinted = sharedPreferences.getBoolean(key, false);
			if(isHinted) {
				hintMap.put(key, isHinted);
			} else {
				mHandler.postDelayed(new Runnable(){
					public void run() {
						Intent intent = new Intent(context, HintPage.class);
						intent.putExtra(HintPage.KEY_BACKGROUNDID, getBackGroundRes(key));
						context.startActivity(intent);
					}
				}, 280);
				Editor mEditor = sharedPreferences.edit();
				mEditor.putBoolean(key, true);
				mEditor.commit();
				hintMap.put(key, isHinted);
			}
		}
	}
	
	private static int getBackGroundRes(String key) {
		if(key.equals(HINT_HOME)) {
			return R.drawable.hint_first_main;
		} else if(key.equals(HINT_USEREDIT)) {
			return R.drawable.hint_user_edit;
		} else if(key.equals(HINT_USEREDITHEADER)) {
			return R.drawable.tab_wo_selector;
//		} else if(key.equals(HINT_HOMEBOTTOM)) {
//			return R.drawable.feed_tab_piazza;
////		} else if(key.equals(HINT_CONSUMERINFORMATION)) {
//			return R.drawable.hint_publish_consume;
		}else if(key.equals(HINT_USERLIFE)){
			return R.drawable.hint_life_consume;
		} else {
			throw new IllegalArgumentException("error key :" + key);
		}
	}
}
