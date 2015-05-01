package com.youa.mobile.news.data;

import android.content.Context;

import com.youa.mobile.friend.data.ContentImg;
import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class FavoriteData {

	private final static String TAG = "FavoriteData";
	public String postId;
	// user
	public String likeUserid;
	public String headImgId;
	public String sex;
	public String userName;
	public String likeTime;
	public String userType;

	// post
	public String sourceFeedId;
	public CharSequence sourceContent;
	public String sourceImage;

	public FavoriteData() {

	}

	public FavoriteData(Context context, JsonObject json) {
		NewsUtil.LOGD(TAG, "FavoriteData data json:" + json.toJsonString());
		likeTime = json.getString("create_time");
		postId= json.getString("postid");
		JsonObject profileJson = json.getJsonObject("profile");
		likeUserid = profileJson.getString("userid");
		sex = profileJson.getString("sex");
		headImgId = profileJson.getString("head_imid");
		userName = profileJson.getString("nickname");
		userType = profileJson.getString("type");

		JsonObject postinfoJson = json.getJsonObject("postinfo");
		if(postinfoJson!=null){
			sourceFeedId = postinfoJson.getString("postid");
			JsonObject contentobj = postinfoJson.getJsonObject("va_post_content");
			if (contentobj != null) {
				String temp = contentobj.getString("content");
				sourceContent = NewsUtil.parseContent(temp);
			}

			JsonArray imageinfoArray = postinfoJson.getJsonArray("va_img_info");
			if (imageinfoArray != null && imageinfoArray.size() > 0) {
				JsonObject obj = (JsonObject) imageinfoArray.get(0);
				sourceImage = obj.getString("imageid");
			}
		}
	}
}
