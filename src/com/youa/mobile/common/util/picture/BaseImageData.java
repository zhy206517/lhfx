package com.youa.mobile.common.util.picture;

import android.os.Parcelable;

public abstract class BaseImageData implements Parcelable {
	public String id;
	public String desc;
	public String path;
	public int angle = 0;
}
