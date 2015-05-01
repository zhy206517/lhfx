package com.youa.mobile.common.base;

import com.youa.mobile.common.manager.ApplicationManager;

import android.app.ActivityGroup;
import android.os.Bundle;

public class BasePageGroup extends ActivityGroup {
	protected final static String TAG = BasePageGroup.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationManager.getInstance().init(this);
	}
}
