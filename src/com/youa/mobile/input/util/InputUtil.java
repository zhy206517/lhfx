package com.youa.mobile.input.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.youa.mobile.DebugMode;

public class InputUtil {

	public static final String WHITE_SPACES = " \r\n\t\u3000\u00A0\u2007\u202F";
	
	public static void LOGD(String tag, String msg) {
		if (DebugMode.debug)
			Log.d("#input#" + tag, "----------------->" + msg);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() <= 0;
	}

	public static String getPathByImageURI(Activity context, Uri uri) {
		if (uri == null) {
			return null;
		}
		String[] proj = { MediaStore.Images.Media.DATA };  
		Cursor actualimagecursor = context.managedQuery(uri,proj,null,null,null);  
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
		actualimagecursor.moveToFirst();  
		String img_path = actualimagecursor.getString(actual_image_column_index);
		return img_path;
	}

	/** lstrip - strips spaces from left
	   * @param str what to strip
	   * @return String the striped string
	   */
	  public static String lstrip(String str) {
	    return megastrip(str, true, false, WHITE_SPACES);
	  }

	  /** rstrip - strips spaces from right
	   * @param str what to strip
	   * @return String the striped string
	   */
	  public static String rstrip(String str) {
	    return megastrip(str, false, true, WHITE_SPACES);
	  }
	  
	  /** strip - strips both ways
	   * @param str what to strip
	   * @return String the striped string
	   */
	  public static String strip(String str) {
	    return megastrip(str, true, true, WHITE_SPACES);
	  }
	  
	  /**
	   * This is a both way strip
	   *
	   * @param str the string to strip
	   * @param left strip from left
	   * @param right strip from right
	   * @param what character(s) to strip
	   * @return the stripped string
	   */
	  public static String megastrip(String str,
	                                 boolean left, boolean right,
	                                 String what) {
	    if (str == null) {
	      return null;
	    }

	    int limitLeft = 0;
	    int limitRight = str.length() - 1;

	    while (left && limitLeft <= limitRight &&
	           what.indexOf(str.charAt(limitLeft)) >= 0) {
	      limitLeft ++;
	    }
	    while (right && limitRight>=limitLeft &&
	           what.indexOf(str.charAt(limitRight)) >= 0) {
	      limitRight --;
	    }

	    return str.substring(limitLeft, limitRight + 1);
	  }
}
