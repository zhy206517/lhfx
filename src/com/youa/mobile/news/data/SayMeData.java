package com.youa.mobile.news.data;

import android.content.Context;

import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class SayMeData {

	private static final String TAG = "SayMeData";
	public static final String PUBLISH_USER_ID = "uid";
	public static final String PUBLISH_USER_NAME = "uname";
	public static final String PUBLISH_USER_IMAGE = "head_img_id";
	public static final String PUBLISH_TIME = "time";
	public static final String PUBLISH_CONTENT = "content";
	public static final String REPLY_OR_COMMENT_FLAG = "flag";
	public static final String FEED_ID = "postid";
	public static final String FEED_CONTENT = "post_content";
	public static final String FEED_PRICE = "price";
	public static final String FEED_PLACE = "place";
	public static final String REPLY_USER_ID = "org_cmt_uid";
	public static final String REPLY_USER_NAME = "org_cmt_uname";
	public static final String REPLY_CONTENT = "org_cmt_content";
	public static final String ORG_ID = "org_cmtid";
	public static final String CMT_ID = "cmtid";

	private String publishUserId;
	private String publishUserName;
	private String publishUserImage;
	private String publishTime;
	private CharSequence publishContent;
	public String sourceImage;
	private String replyOrCommentFlag;
	private String replyUserId;
	private String replyUserName;
	private CharSequence replyContent;
	private String sourceFeedId;
	private String sourceUserId;
	private String sourceUserName;
	private CharSequence sourceContent;
	private String sourceConsumePrice;
	private String sourceConsumePlace;
	private String orgId;
	private String cmtId;
	private String sex;

	public SayMeData() {

	}

	public SayMeData(Context context, JsonObject json) {
		NewsUtil.LOGD(TAG, "SayMeData data json:" + json.toJsonString());
		publishUserId = json.getString(PUBLISH_USER_ID);
		publishUserName = json.getString(PUBLISH_USER_NAME);
		publishUserImage = json.getString(PUBLISH_USER_IMAGE);
		publishTime = json.getString(PUBLISH_TIME);
		sourceConsumePrice = json.getString(FEED_PRICE);
		sourceConsumePlace = json.getString(FEED_PLACE);
		replyUserId = json.getString(REPLY_USER_ID);
		replyUserName = json.getString(REPLY_USER_NAME);
		replyOrCommentFlag = json.getString(REPLY_OR_COMMENT_FLAG);
		orgId = json.getString(ORG_ID);
		cmtId = json.getString(CMT_ID);
		sex = json.getString("sex");

		String tempContent = json.getString(PUBLISH_CONTENT);
		if (!NewsUtil.isEmpty(tempContent)) {
			// tempContent = NewsUtil.parseContent(tempContent);
			publishContent = NewsUtil.parseStr2charSequence(tempContent, null,
					context);
		}
		sourceFeedId = json.getString(FEED_ID);
		tempContent = json.getString(FEED_CONTENT);
		if (!NewsUtil.isEmpty(tempContent)) {
			sourceContent = NewsUtil.parseContent(tempContent);
		}

		tempContent = json.getString(REPLY_CONTENT);
		if (!NewsUtil.isEmpty(tempContent)) {
			replyContent = NewsUtil.parseContent(tempContent);
		}
		JsonArray imageinfoArray = json.getJsonArray("va_img_info");
		if (imageinfoArray != null && imageinfoArray.size() > 0) {
			JsonObject obj = (JsonObject) imageinfoArray.get(0);
			sourceImage = obj.getString("imageid");
		}
	}

	public String getSex() {
		return sex;
	}

	public String getPublishUserId() {
		return publishUserId;
	}

	public String getPublishUserName() {
		return publishUserName;
	}

	public String getPublishUserImage() {
		return publishUserImage;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public CharSequence getPublishContent() {
		return publishContent;
	}

	public String getReplyOrCommentFlag() {
		return replyOrCommentFlag;
	}

	public String getOrgId() {
		return orgId;
	}

	public String getReplyUserId() {
		return replyUserId;
	}

	public String getReplyUserName() {
		return replyUserName;
	}

	public CharSequence getReplyContent() {
		return replyContent;
	}

	public String getSourceFeedId() {
		return sourceFeedId;
	}

	public String getSourceUserId() {
		return sourceUserId;
	}

	public String getSourceUserName() {
		return sourceUserName;
	}

	public CharSequence getSourceContent() {
		return sourceContent;
	}

	public String getSourceConsumePrice() {
		return sourceConsumePrice;
	}

	public String getSourceConsumePlace() {
		return sourceConsumePlace;
	}

	public String getCmtId() {
		return cmtId;
	}
}
