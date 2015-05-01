package com.youa.mobile.content.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.circum.data.PopCircumData;
import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.ParserListView;
import com.youa.mobile.content.action.LikeAction;
import com.youa.mobile.content.action.LikeCancelAction;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.HomePageListConfig;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.ParserContent;

public class HomeHttpManager extends BaseRequestManager {

	public boolean requestIsLiked(Context context, String postId)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(postId);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.isLikedOnePost",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		boolean isLiked = parerIsLike(resultObject);
		return isLiked;
	}

	public int requestLike(Context context, String postId)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(postId);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.addLike", paramMap,
				context);
		JsonObject resultObject = httpRes.getJSONObject();
		return parserLike(resultObject);
	}
	public int requestDelete(Context context, String postId)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		//json.add(uid);
		json.add(postId);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.delOnePost", paramMap,
				context);
		JsonObject resultObject = httpRes.getJSONObject();
		return parserDelete(resultObject);
	}
	public int requestCancleLike(Context context, String postId)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		json.add(uid);
		json.add(postId);
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.delLike", paramMap,
				context);
		JsonObject resultObject = httpRes.getJSONObject();
		return parserDeLike(resultObject);
	}

	public List<FeedContentCommentData> requestFriendComment(Context context,
			String postId, String offset, int limit) throws MessageException {
		JsonArray json = new JsonArray();
		json.add("" + postId);// 消息id
		json.add("" + offset);// 分页最小commid
		json.add("" + limit);// 当页显示数
		String str = json.toJsonString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.getCmtByPostidWl",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		List<FeedContentCommentData> commentList = parserFriendComment(resultObject);
		return commentList;
	}

	// 好友正文页
	public HomeData requestFriendContent(Context context, String postId)
			throws MessageException {
		String uid = ApplicationManager.getInstance().getUserId();
		JsonArray json = new JsonArray();
		// json.add(uid);// uid
		json.add("" + postId);// 消息id
		json.add("true");
		String str = json.toJsonString();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", str);
		HttpRes httpRes = getHttpManager().post("jt:msg.getOnePostForWireless",
				paramMap, context);
		JsonObject resultObject = httpRes.getJSONObject();
		HomeData homeData = parserFriendContent(context, resultObject);
		// parserItem(json)
		return homeData;
	}
	
	private JsonObject isResponseOk(JsonObject object) {
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			// homeList.removeFooter(footer);
			// 错误信息处理
			return null;
		}
		return object;
	}
	// ---------------------------parser--------------------------------

	private HomeData parserFriendContent(Context context, JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		dataObj = dataObj.getJsonObject("rpcret");
		if (dataObj == null) {
			return null;
		}
		return ParserListView.getInstance().parserItem(context, dataObj, true);
	}

	private boolean parerIsLike(JsonObject object) {
		if (isResponseOk(object) == null) {
			return false;
		}
		JsonObject obj = object.getJsonObject("data");
		if (obj == null) {
			return false;
		}
		String returnValue = obj.getString("rpcret");
		if (null == returnValue || "".equals(returnValue.trim())) {
			return false;
		}
		if ("1".equals(returnValue) || "true".equals(returnValue)) {
			return true;
		}
		return false;
	}

	private List<FeedContentCommentData> parserFriendComment(JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject obj = object.getJsonObject("data");
		obj = obj.getJsonObject("rpcret");
		FeedContentCommentData.commentNum = obj.getString("tn");
		JsonArray jsonArray = obj.getJsonArray("arr_info");
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<FeedContentCommentData> list = new ArrayList<FeedContentCommentData>();
		FeedContentCommentData[] data = new FeedContentCommentData[jsonArray
				.size()];
		JsonObject jsonO = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonO = (JsonObject) jsonArray.get(i);
			if (jsonO == null) {
				continue;
			}
			data[i] = new FeedContentCommentData();
			data[i].commentId = jsonO.getString("cmtid");// 评论id
			data[i].publicId = jsonO.getString("uid");// 发布人id
			data[i].replyId = jsonO.getString("org_uid");// 被回复人id
			data[i].replyName = jsonO.getString("org_uname");// 被回复人姓名
			data[i].public_img_head_id = jsonO.getString("head_img_id");// 评论者头像图片id
			data[i].public_name = jsonO.getString("uname");// 评论者姓名
			int sex = 0;
			try {
				sex = Integer.parseInt(jsonO.getString("sex"));
			} catch (Exception e) {
				sex = 0;
			}
			if (sex < 2) {
				data[i].sex = User.MEN;
			} else if (sex >= 2) {
				data[i].sex = User.WOMEN;
			}
			String content = jsonO.getString("content");
			if (content != null) {
				data[i].contents = ParserContent.getParser().parser(
						content.toCharArray());
			}
			// data[i].public_content = jsonO.getString("content");// 评论内容
			data[i].public_time = jsonO.getString("time");// 评论时间
			list.add(data[i]);
		}
		return list;
	}

	private int parserLike(JsonObject object) {
		String responseCode = object.getString("err");
		if ("jt.u_liked".equals(responseCode)) {
			return LikeAction.LIKE_ED;
		} else if ("jt.u_arg.post_del".equals(responseCode)) {
			return LikeAction.LIKE_ORIGIN_DELETED;
		}
		if (!"mcphp.ok".equals(responseCode)) {
			return LikeAction.LIKE_NO;
		}
		JsonObject obj = object.getJsonObject("data");
		String likeId = obj.getString("rpcret");
		if (likeId != null && !"".equals(likeId.trim())) {
			return LikeAction.LIKE_OK;
		}
		return LikeAction.LIKE_NO;
	}
	private int parserDelete(JsonObject object) {
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			return 0;
		}
		JsonObject obj = object.getJsonObject("data");
		String id = obj.getString("rpcret");
		int flag = 0;
		if ("1".equals(id)) {
			flag = 1;
		}
		return flag;
	}
	private int parserDeLike(JsonObject object) {
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			return LikeAction.LIKE_NO;
		}
		JsonObject obj = object.getJsonObject("data");
		String likeId = obj.getString("rpcret");
		if (likeId != null && !"".equals(likeId.trim())) {
			return LikeCancelAction.DELIKE_OK;
		}
		return LikeCancelAction.DELIKE_NO;
	}

}
