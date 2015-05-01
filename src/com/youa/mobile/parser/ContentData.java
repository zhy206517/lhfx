package com.youa.mobile.parser;

import java.io.Serializable;

public class ContentData implements Serializable {
	final public static int TYPE_TEXT = 0, TYPE_AT = 1, TYPE_TOPIC = 2,
			TYPE_EMOTION = 3, TYPE_LINK = 4;
	public int type;
	public String str;
	public String href;

//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeInt(type);
//		dest.writeString(str);
//	}
//
//	public ContentData() {
//	}
//
//	private ContentData(Parcel in) {
//		type = in.readInt();
//		str = in.readString();
//	}
//
//	public static final Parcelable.Creator<ContentData> CREATOR = new Parcelable.Creator<ContentData>() {
//		public ContentData createFromParcel(Parcel in) {
//			return new ContentData(in);
//		}
//
//		public ContentData[] newArray(int size) {
//			return new ContentData[size];
//		}
//	};
}
