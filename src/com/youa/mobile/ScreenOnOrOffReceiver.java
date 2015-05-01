package com.youa.mobile;

import com.youa.mobile.more.MoreMainPage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class ScreenOnOrOffReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = "";
		if (intent != null) {
			action = intent.getAction();
		}
		Log.d("ScreenOnOrOffReceiver", action);
		if (Intent.ACTION_SCREEN_ON.equals(action)) {
			LehoApp.isScreenOn=true;
		} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
			LehoApp.isScreenOn=false;
		}
	}
}
