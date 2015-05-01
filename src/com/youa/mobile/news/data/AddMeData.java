package com.youa.mobile.news.data;

import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class AddMeData {

	private static final String TAG = "AddMeData";
	public static final String FEED_ID = "postid";
	public static final String PUBLISH_USER_ID = "post_uid";
	public static final String PUBLISH_USER_NAME = "post_uname";
	public static final String PUBLISH_USER_IMAGE = "head_imgid";
	public static final String PUBLISH_TIME = "modify_time";
	public static final String PUBLISH_CONTENT_ARR = "va_post_content";
	public static final String PUBLISH_CONTENT = "content";
	public static final String PUBLISH_PLACE = "place";
	public static final String PUBLISH_PRICE = "price";
	public static final String PUBLISH_CONTENT_IMAGE = "va_img_info";
	public static final String PUBLISH_LIKE_COUNT = "like_num";
	public static final String PUBLISH_COMMENT_COUNT = "cmt_num";
	public static final String PUBLISH_FORWARD_COUNT = "trans_num";
	public static final String PUBLISH_TYPE = "type";
	public static final String PUBLISH_FROM = "from_where";
	public static final String SOURCE_FEED_ID = "org_post_postid";
	public static final String SOURCE_USER_ID = "org_post_uid";
	public static final String SOURCE_USER_NAME = "org_post_uname";
	public static final String SOURCE_CONTENT_ARR = "org_content";
	public static final String SOURCE_CONTENT = "content";
	public static final String SOURCE_CONTENT_IMAGE = "arr_org_img_id";
	public static final String SOURCE_PLACE = "org_place";
	public static final String SOURCE_PRICE = "price";

	public static final String TYPE_FORWARD = "1";
	public static final String TYPE_ORIGINAL = "0";
	public static final String FROM_ADNROID = "1";
	public static final String FROM_WEB = "0";
	public static final String FROM_APPLE = "2";

	private String pubishFeedId;
	private String publishUserId;
	private String publishUserName;
	private String publishUserImage;
	private String publishTime;
	private String publishContent;
	private String publishLikeCount;
	private String publishCommentCount;
	private String publishForwardCount;
	private String publishConsumePrice;
	private String publishConsumePlace;
	private String publishFrom;// web:0 android:1 apple:2
	private String[] publishContentImageIds;
	private String publisType;// 原创0，转发1
	private String sourceFeedId;
	private String sourceUserId;
	private String sourceUserName;
	private String sourceContent;
	private String[] sourceContentImageIds;
	private String sourceConsumePrice;
	private String sourceConsumePlace;
	private boolean isOriginalImages;
	private boolean isSourceImages;

	public AddMeData() {

	}
	public AddMeData(JsonObject json) {
		
	}

	public AddMeData(JsonObject json,int a) {

		NewsUtil.LOGD(TAG, "AddMe data json:" + json.toJsonString());
		pubishFeedId = json.getString(FEED_ID);
		publishUserId = json.getString(PUBLISH_USER_ID);
		publishUserName = json.getString(PUBLISH_USER_NAME);
		publishUserImage = json.getString(PUBLISH_USER_IMAGE);
		publishTime = json.getString(PUBLISH_TIME);
		publishCommentCount = json.getString(PUBLISH_COMMENT_COUNT);
		publishLikeCount = json.getString(PUBLISH_LIKE_COUNT);
		publishForwardCount = json.getString(PUBLISH_FORWARD_COUNT);
		publishFrom = json.getString(PUBLISH_FROM);
		JsonObject contentObj = json.getJsonObject(PUBLISH_CONTENT_ARR);
		if (contentObj != null) {
			publishContent = contentObj.getString(PUBLISH_CONTENT);
			if (!NewsUtil.isEmpty(publishContent)) {
				publishContent = NewsUtil.parseContent(publishContent);
			}
			publishConsumePrice = contentObj.getString(PUBLISH_PRICE);
		}
		sourceFeedId = json.getString(SOURCE_FEED_ID);
		publisType = json.getString(PUBLISH_TYPE);
		publishConsumePlace = json.getString(PUBLISH_PLACE);
		JsonArray jsonArray = json.getJsonArray(PUBLISH_CONTENT_IMAGE);
		if (jsonArray != null && jsonArray.size() > 0) {
			int size = jsonArray.size();
			isOriginalImages = size > 1;
			publishContentImageIds = new String[size];
			for (int i = 0; i < size; i++) {
				publishContentImageIds[i] = ((JsonObject) jsonArray.get(i))
						.getString("imageid");
			}
		}
		sourceUserId = json.getString(SOURCE_USER_ID);
		sourceUserName = json.getString(SOURCE_USER_NAME);
		sourceFeedId = json.getString(SOURCE_FEED_ID);
		contentObj = json.getJsonObject(SOURCE_CONTENT_ARR);
		if (contentObj != null) {
			sourceContent = contentObj.getString(SOURCE_CONTENT);
			if (!NewsUtil.isEmpty(sourceContent)) {
				sourceContent = NewsUtil.parseContent(sourceContent);
			}
			sourceConsumePrice = contentObj.getString(SOURCE_PRICE);
		}
		sourceConsumePlace = json.getString(SOURCE_PLACE);
		jsonArray = json.getJsonArray(SOURCE_CONTENT_IMAGE);
		if (jsonArray != null && jsonArray.size() > 0) {
			int size = jsonArray.size();
			isSourceImages = size > 1;
			sourceContentImageIds = new String[size];
			for (int i = 0; i < size; i++) {
				sourceContentImageIds[i] = ((JsonObject) jsonArray.get(i))
						.getString("imageid");
			}
		}
		NewsUtil.LOGD("AddMeData", "pubishFeedId : " + pubishFeedId);
		NewsUtil.LOGD("AddMeData", "publishUserId : " + publishUserId);
		NewsUtil.LOGD("AddMeData", "publishUserName : " + publishUserName);
		NewsUtil.LOGD("AddMeData", "publishUserImage : " + publishUserImage);
		NewsUtil.LOGD("AddMeData", "publishTime : " + publishTime);
		NewsUtil.LOGD("AddMeData", "publishContent : " + publishContent);
		NewsUtil.LOGD("AddMeData", "publishLikeCount : " + publishLikeCount);
		NewsUtil.LOGD("AddMeData", "publishCommentCount : "	+ publishCommentCount);
		NewsUtil.LOGD("AddMeData", "publishForwardCount : "	+ publishForwardCount);
		NewsUtil.LOGD("AddMeData", "publishConsumePrice : "	+ publishConsumePrice);
		NewsUtil.LOGD("AddMeData", "publishConsumePlace : "	+ publishConsumePlace);
		NewsUtil.LOGD("AddMeData", "publishFrom : " + publishFrom);
		NewsUtil.LOGD("AddMeData", "publisType : " + publisType);
		NewsUtil.LOGD("AddMeData", "sourceFeedId : " + sourceFeedId);
		NewsUtil.LOGD("AddMeData", "sourceUserId : " + sourceUserId);
		NewsUtil.LOGD("AddMeData", "sourceUserName : " + sourceUserName);
		NewsUtil.LOGD("AddMeData", "sourceContent : " + sourceContent);
		NewsUtil.LOGD("AddMeData", "sourceConsumePrice : " + sourceConsumePrice);
		NewsUtil.LOGD("AddMeData", "sourceConsumePlace : " + sourceConsumePlace);
		if (publishContentImageIds != null) {
			for (int i = 0; i < publishContentImageIds.length; i++) {
				NewsUtil.LOGD("AddMeData", "publishContentImageIds : " + i + " :" + publishContentImageIds[i]);
			}

		}
		if (sourceContentImageIds != null) {
			for (int i = 0; i < sourceContentImageIds.length; i++) {
				NewsUtil.LOGD("AddMeData", "sourceContentImageIds : " + i + " :" + sourceContentImageIds[i]);
			}
		}
	}

	public String getPubishFeedId() {
		return pubishFeedId;
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

	public String getPublishContent() {
		return publishContent;
	}

	public String getPublishLikeCount() {
		return publishLikeCount;
	}

	public String getPublishCommentCount() {
		return publishCommentCount;
	}

	public String getPublishForwardCount() {
		return publishForwardCount;
	}

	public String getPublishConsumePrice() {
		return publishConsumePrice;
	}

	public String getPublishConsumePlace() {
		return publishConsumePlace;
	}

	public String getPublishFrom() {
		return publishFrom;
	}

	public String[] getPublishContentImageIds() {
		return publishContentImageIds;
	}

	public String getPublishType() {
		return publisType;
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

	public String getSourceContent() {
		return sourceContent;
	}

	public String[] getSourceContentImageIds() {
		return sourceContentImageIds;
	}

	public String getSourceConsumePrice() {
		return sourceConsumePrice;
	}

	public String getSourceConsumePlace() {
		return sourceConsumePlace;
	}

	public boolean isOriginalImages() {
		return isOriginalImages;
	}

	public boolean isSourceImages() {
		return isSourceImages;
	}

}
