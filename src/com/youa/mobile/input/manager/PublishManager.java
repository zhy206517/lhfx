package com.youa.mobile.input.manager;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.youa.mobile.common.base.BaseManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.friend.data.ContentImg;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.input.data.ImageData;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class PublishManager extends BaseManager {

	private PublishHttpRequestManager httpRequestManager;

	private PublishHttpRequestManager getHttpRequestManager() {
		if (httpRequestManager == null) {
			httpRequestManager = new PublishHttpRequestManager();
		}
		return httpRequestManager;
	}

	public HomeData requestPublishOriginal(Context context,
			ArrayList<ImageData> imageList, String uid, String content,
			int columnNo, String placeName, String price, int latitude,
			int longitude, String plid, boolean shared)
			throws MessageException {

		String strArr[] = null;
		if (imageList != null && imageList.size() > 0) {
			for (int i = 0; i < imageList.size(); i++) {
				ImageData data = imageList.get(i);
				strArr = getHttpRequestManager().requestPublishImage(context,
						data.imagePath);
				data.imageId = strArr[0];
				data.imagePath = strArr[1];
			}
		}
		if (InputUtil.isEmpty(content)) {
			content = "分享图片";
		}
		JsonObject json = getHttpRequestManager().requestPublishOriginal(context, imageList, uid,
				content, columnNo, placeName, price, latitude, longitude, plid);
		return shared ? parsHomeData(json, content) : null;// 如果需要第三方分享则返回数据，否则不返回任何数据。
	}

	////{data={userid=21147e1b4fb511dc04239ea0, rpcret={postid=7d38397da2c704f950a47721, imgArr=[{imageid=eab9ec35a15ea64834d8ab90, width=816, height=612, desc=}]}, youasession=619d262483436408c7a8a03c40fffbce09703f383775c1, username=guiwenbin888, youaidentity=68a38c54b09c777dd35c81385628ed16, head_imid=f0121941c46c22a1a70a27ad}, err=mcphp.ok}
	/**
	 * 解析或拼接为HomeData 对象，目的是用户发送feed后的第三方分享。此HomeData中的数据格式不是严格的（不是所有字段都有值），仅满足三方分享的需要。
	 * @param json
	 * @param content
	 * @return
	 */
	private HomeData parsHomeData(JsonObject json, String content){
		if(json == null){
			return null;
		}
		JsonObject data = json.getJsonObject("data");
		if(data == null)
			return null;
		
		JsonObject rpcret = data.getJsonObject("rpcret");
		if(rpcret == null)
			return null;
		String postId = rpcret.getString("postid");
		JsonArray imgs = rpcret.getJsonArray("imgArr");
		User publicUser = new User();
		parserContentImgArray(publicUser, imgs);
		ContentData contentData = new ContentData();
		contentData.str = content;
		ContentData[] contents = {contentData};
		publicUser.contents = contents;
		publicUser.postId = postId;
		String nickName = data.getString("username");
		publicUser.name = nickName;
		HomeData feed = new HomeData();
		feed.PublicUser = publicUser;
		return feed;
	}
	
	private void parserContentImgArray(User user, JsonArray jsonArray) {
		if (jsonArray == null || jsonArray.size() < 1) {
			return;
		}
		user.contentImg = new ContentImg[jsonArray.size()];
		JsonObject obj = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			if (!(jsonArray.get(i) instanceof JsonObject)) {
				continue;
			}
			obj = (JsonObject) jsonArray.get(i);
			user.contentImg[i] = new ContentImg();
			user.contentImg[i].img_content_id = obj.getString("imageid");
			user.contentImg[i].img_desc = obj.getString("desc");
			String height=obj.getString("height");
			if(!TextUtils.isEmpty(height)&&!"null".equals(height)){
				user.contentImg[i].height = Integer.parseInt(height);
			}else{
				user.contentImg[i].height =0;
			}
			String width=obj.getString("width");
			if(!TextUtils.isEmpty(width)){
				user.contentImg[i].width = Integer.parseInt(width);
			}else{
				user.contentImg[i].width =0;
			}
		}
	}
	public void requestPublishForward(Context context, String userId,
			String content, String sourceId, boolean isComment)
			throws MessageException {
		getHttpRequestManager().requestPublishForward(context, userId, content,
				sourceId, isComment);
	}

	public void requestPublishComment(Context context, String userId,
			String content, String sourceId, String commentId,
			boolean isComment, boolean isForward) throws MessageException {
		getHttpRequestManager().requestPublishComment(context, userId, content,
				sourceId, commentId, isComment, isForward);
	}

	public void requestPublishFeedback(Context context, String userId,
			String content, String type) throws MessageException {
		getHttpRequestManager().requestPublishFeedback(context, userId,
				content, type);
	}
}
