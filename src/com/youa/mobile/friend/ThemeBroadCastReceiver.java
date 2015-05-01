package com.youa.mobile.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.friend.FriendFeedActivity;

public class ThemeBroadCastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(LehuoIntent.ACTION_USER_DISTRICT_CHANGE.equals(action)){
//			FriendFeedActivity.isGetCircumOnce = true;
		}if(LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE.equals(action)){
			FriendFeedActivity.isGetFriendOnce = true;
		}

	}

}
