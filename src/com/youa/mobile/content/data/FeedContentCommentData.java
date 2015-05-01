package com.youa.mobile.content.data;

import com.youa.mobile.parser.ContentData;

public class FeedContentCommentData {
	final public static int MEN = 1, WOMEN = 2;
	public static String commentNum;

	public String commentId;
	public String publicId;
	public String public_name;
	public String public_img_head_id;
	public String public_content;
	public ContentData[] contents;
	public String public_time;

	public String replyId;// 被回复人id
	public String replyName;// 被回复人姓名
	public int sex;

	public boolean isHeadImgNeedGet;
}
