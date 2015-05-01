package com.youa.mobile.common.util.picture;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageData extends BaseImageData{

	public ImageData(){}
	
	public ImageData(String id, String desc, String path){
		this.id = id;
		this.desc = desc;
		this.path = path;
	}
	
	public ImageData(String id, String desc, String path, int angle){
		this.id = id;
		this.desc = desc;
		this.path = path;
		this.angle = angle;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(desc);
		dest.writeString(path);
		dest.writeInt(angle);
	}
	private ImageData(Parcel in) {
		id = in.readString();
		desc = in.readString();
		path = in.readString();
		angle = in.readInt();
	}
	
	public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
		public ImageData createFromParcel(Parcel in) {
			return new ImageData(in);
		}

		public ImageData[] newArray(int size) {
			return new ImageData[size];
		}
	};
	
	
}
