package com.youa.mobile.friend.data;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentImg implements Serializable{//Parcelable
	public String img_content_id;
	public String img_desc;
	public int width;
	public int height;

//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(img_content_id);
//		dest.writeString(img_desc);
//	}
//
//	public ContentImg() {
//	}
//
//	private ContentImg(Parcel in) {
//		img_content_id = in.readString();
//		img_desc = in.readString();
//	}
//
//	public static final Parcelable.Creator<ContentImg> CREATOR = new Parcelable.Creator<ContentImg>() {
//		public ContentImg createFromParcel(Parcel in) {
//			return new ContentImg(in);
//		}
//
//		public ContentImg[] newArray(int size) {
//			return new ContentImg[size];
//		}
//	};
}
