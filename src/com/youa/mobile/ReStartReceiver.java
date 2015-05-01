package com.youa.mobile;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReStartReceiver extends BroadcastReceiver {
	public static final String TAG = "ReStartReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "############com.youa.mobile.ReStartReceiver");
		ActivityManager mActivityManager = 
			(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = mActivityManager.getRunningTasks(10);

		for(RunningTaskInfo runningTaskInfo:runningTaskInfos) {
			Log.d(TAG,"appProcess.processName:" + runningTaskInfo.baseActivity);
			if("com.youa.mobile".equals(runningTaskInfo.baseActivity.getPackageName())) {
				return ;
			}
		}
		Intent mainintent = new Intent(context, LehoTabActivity.class);
		mainintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mainintent);
	}
}
