package com.youa.mobile.friend.data;

import java.io.Serializable;
import java.util.Arrays;

import com.youa.mobile.parser.ContentData;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6156724509296263753L;
	final public static int MEN = 1, WOMEN = 2;
	public String postId; // friend ,theme
	public String uId;
	public int sex;
	// 0:普通 1:专家2：达人 3商户
	public int type;
	public String feedType;
	public String img_head_id;// 头像
	public String name;// 姓名
	public transient CharSequence nameCharSequence;
	public ContentData[] contents;
	// public String content;
	public transient CharSequence charSequence;
	public String time;
	public String timeLine;
	public ContentImg[] contentImg;
	public String place;
	public String price;
	public String fromWhere;
	public String like_num;
	public String comment_num;
	public String transpond_num;
	public int contentImgWidth;
	public int contentImgHeight;
	public boolean isLiked;
	// ----
	public boolean isHeadNeedGet;
	public boolean isContentImgNeedGet;

	//gps经纬度(地点或商家位置信息)
	public String lon;
	public String lat;
	public String address;
	public int placeType;
	public String refId;
	
	public void setCharSequence(CharSequence charSequence) {
		this.charSequence = charSequence;
	}

	public CharSequence getCharSequence() {
		return charSequence;
	}

	public void setNameCharSequence(CharSequence nameCharSequence) {
		this.nameCharSequence = nameCharSequence;
	}

	public CharSequence getNameCharSequence() {
		return this.nameCharSequence;
	}

	@Override
	public String toString() {
		return "User [postId=" + postId + ", \nuId=" + uId + ", \nsex=" + sex
				+ ", \ntype=" + type + ", \nfeedType=" + feedType
				+ ", \nimg_head_id=" + img_head_id + ", \nname=" + name
				+ ", \ncontents=" + Arrays.toString(contents) + ",\n time="
				+ time + ", \ntimeLine=" + timeLine + ", \ncontentImg="
				+ Arrays.toString(contentImg) + ", \nplace=" + place + "(lat:"
				+ lat + " ,lon: " + lon + ")" + ", \nprice=" + price
				+ ", \nfromWhere=" + fromWhere + ", \nlike_num=" + like_num
				+ ", \ncomment_num=" + comment_num + ", \ntranspond_num="
				+ transpond_num + ", \nisLiked=" + isLiked
				+ ", \nisHeadNeedGet=" + isHeadNeedGet
				+ ", \nisContentImgNeedGet=" + isContentImgNeedGet + "]";

	}
}
