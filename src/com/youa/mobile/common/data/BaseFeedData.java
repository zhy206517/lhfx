package com.youa.mobile.common.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.youa.mobile.parser.ContentData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.JsonValue;
import com.youa.mobile.parser.ParserContent;

public class BaseFeedData implements FeedData{
	public static final String TAG = "BaseFeedData";
	private String img_head_id;
	private String time;
	private List<ImageData> contentImgList = new ArrayList<ImageData>();
	// ----
	private String like_num;
	private String comment_num;
	private String transpond_num;
	private ContentData[] contents;
	private String content;
	// ----
	private String public_name;// 消息发布者姓名
	private String postId;
	private String uid;// 消息发布者的uid
	private String feedType;
	private String originId;
	private String originUid;
	// ---
	private boolean isHeadNeedGet;
	private boolean isContentImgNeedGet;

	public BaseFeedData(JsonObject json) {
		postId = json.getString("postid");
		uid = json.getString("uid");// 消息发布者的uid
		feedType = json.getString("type");// 消息类型：0表示原创,1表示转发，2表示喜欢"
		JsonObject obj = json.getJsonObject("va_post_content");
		if (obj != null) {// 消息的内容信息
			String data = obj.getString("content");
			if (data == null || "".equals(data)) {
				content = "";
			} else {
				contents = ParserContent.getParser().parser(data.toCharArray());
				StringBuffer sb = new StringBuffer();
				for (ContentData d : contents) {
					sb.append(d.str);
				}
				content = sb.toString();
			}
			obj = null;
		}
		// TODO 这个地方需要数组，原来是对象处理，暂时改成数组，但是需要进一步处理
		JsonArray jsonArray = json.getJsonArray("va_img_info");
		if(jsonArray != null && jsonArray.size() > 0) {
			parserContentImgArray(jsonArray);
		}
		
		time = json.getString("create_time");
		originId = json.getString("org_post_postid");// 转发消息时,最原创的消息id
		originUid = json.getString("org_post_uid");// 转发消息时,最原创者的uid
		like_num = json.getString("like_num");
		comment_num = json.getString("comment_num");
		transpond_num = json.getString("transmit_num");
		// clsid "类目信息的id值，和class表中clsid一致",
		// time_line"时间线,表示事件的发生时间", //是发消息的时间吗？
		// plid 消息中提及的事件发生的地点对应的id值", //地点对应的id什么用
		// has_img "是否有图片信息"
		// va_img_info va_img_info
		// is_sync_third 是否将消息同步到第三方
		// share_gid 将消息的访问权限分配给自己所关注的那个组
		// column_no "0表示消费分享|1表示优惠信息|2表示随便说说|64表示其他"
		// from_where 表示发送消息的终端,0表示网站,1表示移动终端
		// org_transmit_postid 转发消息时,被转发的消息如果也是转发的，记录这条转发内容
		// org_transmit_uid 转发消息时,被转发的消息如果也是转发的，记录这条转发消息的发布者
		// org_url 如果消息源于网络,则记录消息来源的url
		// short_url 记录网络url的短url内容
		// create_time 消息的创建时间
		// modify_time 消息的修改时间
	}

	private void parserContentImgArray(JsonArray jsonArray) {
		contentImgList.clear();
		if (jsonArray == null || jsonArray.size() < 1) {
			return;
		}		
		JsonObject obj = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonValue jsonValue = jsonArray.get(i);
			if(jsonValue instanceof JsonObject) {
				obj = (JsonObject) jsonValue;
				ContentImg contentImg = new ContentImg();
				contentImg.img_content_id = obj.getString("imageid");
				Log.d(TAG, "parserContentImgArray-i：" + contentImg.img_content_id);
				contentImg.img_desc = obj.getString("desc");
				contentImgList.add(contentImg);
			} 
		}
	}

	public String getImg_head_id() {
		return img_head_id;
	}

	public String getTime() {
		return time;
	}

	public String getLike_num() {
		return like_num;
	}

	public String getComment_num() {
		return comment_num;
	}

	public String getTranspond_num() {
		return transpond_num;
	}

	public ContentData[] getContents() {
		return contents;
	}

	public String getContent() {
		return content;
	}

	public String getPostId() {
		return postId;
	}

	public String getPublicId() {
		return uid;
	}

	public String getFeedType() {
		return feedType;
	}

	public String getOriginId() {
		return originId;
	}

	public String getOriginUid() {
		return originUid;
	}

	public boolean isHeadNeedGet() {
		return isHeadNeedGet;
	}

	public boolean isContentImgNeedGet() {
		return isContentImgNeedGet;
	}


	@Override
	public String getHeaderImgid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ImageData> getImageData() {
		return contentImgList;
	}

	@Override
	public String getUserName() {
		return public_name;
	}
	

	private class ContentImg implements FeedData.ImageData{
		private String img_content_id;
		private String img_desc;
		@Override
		public String getImageDesc() {
			return img_desc;
		}
		@Override
		public String getImageId() {
			return img_content_id;
		}
	}
}
