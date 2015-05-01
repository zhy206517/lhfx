package com.youa.mobile.common.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

public class AppUtil {
	
	public static float getDensity(Activity activity) {
		DisplayMetrics displayMetrics = new DisplayMetrics(); 
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); 
		return displayMetrics.density;
	}
	
	public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getWidth();
	}
	
	public static int getScreenHeight(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getHeight();
	}
	
}
