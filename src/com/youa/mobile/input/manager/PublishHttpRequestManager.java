package com.youa.mobile.input.manager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseRequestManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.http.netsynchronized.FileUploader;
import com.youa.mobile.input.data.ImageData;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonBool;
import com.youa.mobile.parser.JsonNum;
import com.youa.mobile.parser.JsonObject;

public class PublishHttpRequestManager extends BaseRequestManager {

	private final static String TAG = "PublishHttpRequestManager";

	public String[] requestPublishImage(Context context, String imagePath)
			throws MessageException {

		try {
			FileUploader fileUploader = new FileUploader(imagePath, false);
			String imageId = fileUploader.startUpLoad(context);
			String path = fileUploader.getTargetPath();
			InputUtil.LOGD(TAG,
					"enter requestPublishImage() return data <imageId> :"
							+ imageId);
			return new String[] { imageId, path };
		} catch (FileNotFoundException e) {
			throw new MessageException("", R.string.common_error_filenotfound);
		}
	}

	public void requestPublishForward(Context context, String userId,
			String content, String sourceId, boolean isComment)
			throws MessageException {
		InputUtil.LOGD(TAG,
				"enter requestPublishForward() data <userId    > : " + userId);
		InputUtil
				.LOGD(TAG, "enter requestPublishForward() data <sourceId  > : "
						+ sourceId);
		InputUtil.LOGD(TAG,
				"enter requestPublishForward() data <content   > : " + content);
		InputUtil.LOGD(TAG,
				"enter requestPublishForward() data <isComment > : "
						+ isComment);
		JsonArray finalArr = new JsonArray();
		finalArr.add(userId);
		finalArr.add(sourceId);
		finalArr.add(content);
		JsonObject obj = new JsonObject();
		obj.put("from_where", new JsonNum(1));
		obj.put("is_visiable_in_profile", new JsonNum(1));
		
		finalArr.add(obj);// from where
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", finalArr.toJsonString());
		HttpRes res = getHttpManager().post("jt:msg.transmitOnePost", paramMap,
				context);
		JsonObject json = res.getJSONObject();
		InputUtil.LOGD(TAG,
				"enter requestPublishForward() return data <json> :" + json);
		if (isComment) {
			requestPublishComment(context, userId, content, sourceId, "", true,
					false);
		}

	}

	public void requestPublishComment(Context context, String userId,
			String content, String sourceId, String commentId,
			boolean isCommentOrReply, boolean isForward)
			throws MessageException {
		InputUtil.LOGD(TAG,
				"enter requestPublishComment() data <userId    > : " + userId);
		InputUtil
				.LOGD(TAG, "enter requestPublishComment() data <sourceId  > : "
						+ sourceId);
		InputUtil.LOGD(TAG,
				"enter requestPublishComment() data <content   > : " + content);
		InputUtil.LOGD(TAG,
				"enter requestPublishComment() data <isComment > : "
						+ isCommentOrReply);
		InputUtil.LOGD(TAG,
				"enter requestPublishComment() data <isForward > : "
						+ isForward);
		JsonArray finalArr = new JsonArray();
		finalArr.add(userId);
		finalArr.add(content);
		finalArr.add(isCommentOrReply ? new JsonNum(1) : new JsonNum(2));
		finalArr.add(sourceId);
		if (isCommentOrReply) {
			finalArr.add(new JsonNum(0));
		} else {
			finalArr.add(commentId);
		}
		finalArr.add(new JsonBool(isForward ? true : false));
		finalArr.add(new JsonNum(1));
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", finalArr.toJsonString());
		HttpRes res = getHttpManager().post("jt:msg.commentWl", paramMap,
				context);
		JsonObject json = res.getJSONObject();
		System.out.println("json:" + json);
		// * 发表评论或回复
		// * @param $uid uint用户id
		// * @param $content 评论内容,
		// * @param $type 评论的类型：评论：1 回复：2
		// * @param $postid 消息所属人
		// * @param $org_cmtid 被回复的评论id 如果type为2，此值一定要传，不然报错
		// * @param $transmit 是否转发 转发：true ,不转发：false
		// * @return true
		// * @param $from_where 来自哪里
	}

	public JsonObject requestPublishOriginal(Context context, ArrayList<ImageData> imageList, String uid, String content, int columnNo,

			String placeName, String price, int latitude, int longitude,
			String plid) throws MessageException {//, List<SyncThirdData> datas
		InputUtil.LOGD(TAG, "enter requestPublishOriginal() data <uid  > : "
				+ uid);
		InputUtil
				.LOGD(TAG,
						"enter requestPublishOriginal() data <content   > : "
								+ content);
		InputUtil
				.LOGD(TAG, "enter requestPublishOriginal() data <columnNo > : "
						+ columnNo);
		InputUtil.LOGD(TAG,
				"enter requestPublishOriginal() data <placeName > : "
						+ placeName);
		InputUtil.LOGD(TAG, "enter requestPublishOriginal() data <price > : "
				+ price);
		InputUtil.LOGD(TAG, "enter requestPublishOriginal() data <plid > : "
				+ plid);
		JsonArray finalArr = new JsonArray();
		// uid
		finalArr.add(uid);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		JsonArray imgArr = new JsonArray();
		JsonObject obj = null;
		if (imageList!=null) {
			ImageData data=null;
			for(int i=0;i<imageList.size();i++){
				data=imageList.get(i);
				BitmapFactory.decodeFile(data.imagePath, options);
				int width = options.outWidth;
				int heigh = options.outHeight;
				obj = new JsonObject();
				obj.put("imageid", data.imageId);
				obj.put("width", width);
				obj.put("height", heigh);
				obj.put("desc", data.imageDes==null?"":data.imageDes);
				imgArr.add(obj);
			}
		}
		finalArr.add(imgArr);
		// time
		finalArr.add("");
		// plid消费地点
		if (TextUtils.isEmpty(plid)) {
			finalArr.add(new JsonNum());
		} else {
			finalArr.add(plid);
		}
		// content
		finalArr.add(content);
		// type 0=原创
		finalArr.add(new JsonNum(0));
		// 随便说说 or 分享
		finalArr.add(new JsonNum(0));
		obj = new JsonObject();
		// from where 1=android
		obj.put("from_where", 1);
		obj.put("is_visiable_in_profile", 1);
		if (!InputUtil.isEmpty(placeName)) {
			obj.put("place_name", placeName);
		}
		if (!InputUtil.isEmpty(price)) {
			obj.put("price", price);
		}

//		JsonArray thirdType = new JsonArray();
//		if (datas != null) {
//			for (SyncThirdData data : datas) {
//				if ("1".equals(data.getFlag())) {
//					thirdType.add(LoginUtil.parseSiteType(data.getSite()
//							.getSiteTag()));
//				}
//			}
//		}
//		obj.put("third_type", thirdType);
		finalArr.add(obj);

		// 参数列表
		// Array($uid,$imgArr,$timeLine,$plid,$content,$type,$column_no,$option=array())
		//
		// 其中$uid表示用户的id
		// $imgArr=array("imageid","desc") //这个也是一个array，其中imageid表示图片的id。
		// desc表示图片的描述
		// $timeline datetime类型，表示时间的发生时间
		// $plid int 表示地点的id值。该值可以通过suggest得到结果
		// $content string 表示分享的内容
		// $type int 表示feed的类型。 其中0表示原创，1表示转发 ，2表示喜欢
		// $column_no int 表示原创的小分类。 其中0表示分享，1表示优惠信息，2表示随便说说。
		// $option=array(
		// "price" 原创分享的价格
		// "org_url" 优惠信息时，和优惠信息相关的url
		// "place_name" 如果suggest没有得到plid，直接将地点名称传递到该字段
		// )
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", finalArr.toJsonString());
		HttpRes res = getHttpManager().post("jt:mobile.addOnePost", paramMap,
				context);
		JsonObject json = res.getJSONObject();
		InputUtil.LOGD(TAG, "enter requestPublishOriginal() data <json > : "
				+ json);
		return json;
	}

	public void requestPublishFeedback(Context context, String userId,
			String content, String type) throws MessageException {
		InputUtil.LOGD(TAG,
				"enter requestPublishComment() data <userId    > : " + userId);
		InputUtil.LOGD(TAG,
				"enter requestPublishComment() data <sourceId  > : " + content);
		InputUtil.LOGD(TAG,
				"enter requestPublishComment() data <content   > : " + type);
		JsonArray finalArr = new JsonArray();
		finalArr.add(userId);
		finalArr.add(type);
		finalArr.add(content);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rpcinput", finalArr.toJsonString());
		HttpRes res = getHttpManager().post("appshare.addCrmService", paramMap,
				context);
		JsonObject json = res.getJSONObject();
		System.out.println("json:" + json);

	}
}
