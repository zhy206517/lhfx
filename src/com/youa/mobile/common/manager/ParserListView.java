package com.youa.mobile.common.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.friend.data.ContentImg;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.ParserContent;

public class ParserListView {

	public static ParserListView getInstance() {
		return  new ParserListView();
	}
 
	public List<HomeData> parserFriend(Context context, JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		JsonArray jsonArray = dataObj.getJsonArray("rpcret");
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<HomeData> list = new ArrayList<HomeData>();
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(parserItem(context, json, false));
		}
		clear();
		return list;
	}

	private void clear() {
		foregroundColorSpan = null;
		contentView = null;
	}

	public HomeData parserItem(Context context, JsonObject json,
			boolean isCombine) {
		HomeData homeData = new HomeData();
		// ----------------------------发布者------------------------------------
		homeData.PublicUser = new User();
		ParserUser(context, homeData.PublicUser, json, isCombine);
		// ----------------------------源-----------------------------------------
		JsonObject jsonObj = json.getJsonObject("org_post");
		if (jsonObj != null) {
			homeData.originUser = new User();
			ParserUser(context, homeData.originUser, jsonObj, false);
			parserUserInfo(homeData.originUser,
					json.getJsonObject("org_post_user"));
		}
		jsonObj = null;
		// ------------------------------转发--------------------------------------
		// -
		jsonObj = json.getJsonObject("org_transmit");
		if (jsonObj != null) {
			homeData.transPondUser = new User();
			ParserUser(context, homeData.transPondUser, jsonObj, false);
			parserUserInfo(homeData.transPondUser,
					json.getJsonObject("org_transmit_user"));
		}
		jsonObj = null;
		// ------------------------------数据处理------------------------------------
		if (("1".equals(homeData.PublicUser.feedType) || "2"
				.equals(homeData.PublicUser.feedType))
				&& homeData.originUser != null) {// 0原创1转发2喜欢
//			getCharSqence(context, homeData.originUser.name,
//					homeData.originUser.contents);
			// homeData.originUser.content = homeData.originUser.name + "  "
			// + homeData.originUser.content;
//			int end = 0;
//			if (homeData.originUser.name != null) {
//				end = homeData.originUser.name.length();
//			}
//			homeData.originUser.charSequence = createStyle(context, end);
			homeData.originUser.nameCharSequence=createStyle(homeData.originUser.name);
		}
//		if (homeData.transPondUser == null
//				|| homeData.transPondUser.contents == null) {
//			return homeData;
//		}
		// --------------合并转发--------------
		// int length = homeData.transPondUser.contents.length + 1;
		// if (homeData.PublicUser.contents != null) {
		// length += homeData.PublicUser.contents.length;
		// }
		// ContentData[] contentData = new ContentData[length];
		// contentData[0] = new ContentData();
		// contentData[0].str = "@" + homeData.transPondUser.name;
		// contentData[0].type = ContentData.TYPE_AT;
		// homeData.transPondUser.contents[0].str = ":"
		// + homeData.transPondUser.contents[0].str;
		//
		// System.arraycopy(homeData.transPondUser.contents, 0, contentData, 1,
		// homeData.transPondUser.contents.length);
		// if (homeData.PublicUser.contents != null) {
		// System.arraycopy(homeData.PublicUser.contents, 0, contentData,
		// homeData.transPondUser.contents.length + 1,
		// homeData.PublicUser.contents.length);
		// }
		// homeData.PublicUser.contents = contentData;
		// // ---------------
		// StringBuffer sb = new StringBuffer();
		// sb.append("@");
		// sb.append(homeData.transPondUser.name);
		// sb.append(":");
		// sb.append(homeData.transPondUser.content);
		// sb.append(homeData.PublicUser.content);
		// homeData.PublicUser.content = sb.toString();
		return homeData;
	}

	public CharSequence getCharSqence(Context context, String firstStr,
			ContentData[] datas) {
		if (contentView == null) {
			contentView = new TextView(context);
		}
		if (datas == null) {
			return null;
		}
		contentView.setText(null);
		if (firstStr != null) {
			contentView.append(firstStr + " ");
		}
		if (datas != null) {
			for (ContentData d : datas) {
				if (d.type == ContentData.TYPE_EMOTION) {
					Spanned span = EmotionHelper.parseToImageText(context,
							d.str, 20);
					contentView.append(span);
				} else {
					contentView.append(d.str == null ? "" : d.str);
				}
			}
		}
		return contentView.getText();
	}

	private void ParserUser(Context context, User user, JsonObject json,
			boolean isCombine) {
		if (user == null || json == null) {
			return;
		}
		user.postId = json.getString("postid");
		user.uId = json.getString("uid");
		user.feedType = json.getString("type");// 消息类型：0表示原创,1表示转发，2表示喜欢"
		if (user.feedType == null) {
			user.feedType = "" + json.getNum("type", 0);
		}
		if (user.feedType != null) {
			user.feedType = user.feedType.trim();

		}
		JsonObject jsonO = json.getJsonObject("user");
		parserUserInfo(user, jsonO);
		// 取转发消息的名称和内容。在sb时，拼进去。
		JsonObject obj = json.getJsonObject("va_post_content");
		if (obj != null) {// 内容gei
			String data = obj.getString("content");
			StringBuffer sb = new StringBuffer();
			JsonObject tuan = (JsonObject) obj.getJsonObject("tuan");
			String loc = null;
			if (tuan != null) {
				loc = tuan.getString("loc");
			}
			if (loc != null && !("".equals(loc.trim()))) {
				sb.append("<a href=\"");
				sb.append(loc);
				sb.append("\" >");
				sb.append(loc);
				sb.append("</a><br/>");
			}
			user.price = obj.getString("price");
			if(user.price!=null&&!("".equals(user.price.trim()))){
				user.price=user.price+"元/人";
			}
			if (data != null || !("".equals(data.trim()))) {
				sb.append(data);
			}
			data = sb.toString();
			if (data != null || !("".equals(data.trim()))) {
				user.contents = ParserContent.getParser().parser(
						data.toCharArray());
				// user.charSequence = combineContent(context, user.contents,
				// isCombine);
			}
			user.charSequence = getCharSqence(context, null, user.contents);
//			if (user.contents != null) {
//				// sb = new StringBuffer();
//				// for (ContentData d : user.contents) {
//				// sb.append(d.str);
//				// }
//				// user.content = sb.toString();
//				
//			}
			obj = null;
		}
		JsonObject placeJson = json.getJsonObject("place");
		parserPlace(user, placeJson);
		
		// json.getString("has_img");
		JsonArray jsonArray = json.getJsonArray("va_img_info");
		parserContentImgArray(user, jsonArray);// 图片信息
		user.time = json.getString("create_time");
		user.timeLine=json.getString("time_line");
		user.place = json.getString("plname");
		String where = json.getString("from_where");
		if (where != null && "0".equals(where)) {
			user.fromWhere = context.getResources().getString(
					R.string.feed_from_net);
		} else if (where != null && "1".equals(where)) {
			user.fromWhere = context.getResources().getString(
					R.string.feed_from_android);
		} else if (where != null && "2".equals(where)) {
			user.fromWhere = context.getResources().getString(
					R.string.feed_from_apple);
		}
		// case 3:
		// user.fromWhere = "来自iphone客户端";
		// break;
		// case 4:
		// user.fromWhere = "来自ipad客户端";
		// break;
		// case 5:
		// user.fromWhere = "来自安卓pad客户端";
		// break;
		// case 6:
		// user.fromWhere = "来自winpone客户端";
		// break;
		// case 7:
		// user.fromWhere = "来自winpad客户端";
		// break;
		// }
		user.like_num = json.getString("like_num");
		user.comment_num = json.getString("comment_num");
		user.transpond_num = json.getString("transmit_num");
		// ===================================================================
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

	private void parserUserInfo(User user, JsonObject jsonO) {
		if (user == null || jsonO == null) {
			return;
		}
		user.uId = jsonO.getString("userid");
		user.name = jsonO.getString("nickname");//username
		user.img_head_id = jsonO.getString("head_imid");
		//0:普通 1:专家2：达人 3商户
		String type = jsonO.getString("type");
		if(!TextUtils.isEmpty(type)){
			user.type= Integer.parseInt(type);
		}
		int sex = 0;
		try {
			sex = Integer.parseInt(jsonO.getString("sex"));
		} catch (Exception e) {
			sex = 0;
		}
		if (sex < 2) {
			user.sex = User.MEN;
		} else if (sex >= 2) {
			user.sex = User.WOMEN;
		}
	}

	private ForegroundColorSpan foregroundColorSpan;

	public CharSequence createStyle(Context context, int end) {
		if (end < 1) {
			return null;
		}
		// if (contentView == null) {
		// contentView = new TextView(context);
		// }
		if (foregroundColorSpan == null) {
			foregroundColorSpan = new ForegroundColorSpan(0XFF5F911B);
		}
		// contentView.setText(str);
		CharSequence text = contentView.getText();
		if (end > text.length()) {
			return null;
		}
		SpannableStringBuilder style = new SpannableStringBuilder(text);
		// style.clearSpans();
		style.setSpan(foregroundColorSpan, 0, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		contentView.setText(style);
		style = null;
		return contentView.getText();
	}
	
	public CharSequence createStyle(String str) {
		if (str==null||str.trim().length()<1) {
			return null;
		}
		if (foregroundColorSpan == null) {
			foregroundColorSpan = new ForegroundColorSpan(0XFF5F911B);
		}
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.clearSpans();
		style.setSpan(foregroundColorSpan, 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		contentView.setText(style);
		style = null;
		return contentView.getText();
	}

	private TextView contentView;

	protected CharSequence combineContent(Context context,
			ContentData[] contents, boolean isComibine) {
		if (contents == null || !isComibine) {
			return null;
		}
		if (contentView == null) {
			contentView = new TextView(context);
		} else {
			contentView.setText(null);
		}
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].type == ContentData.TYPE_AT) {
				contentView.append(Html.fromHtml("<a href=\"\" >"
						+ contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_TOPIC) {
				contentView.append(Html.fromHtml("<a href=\"\" >"
						+ contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_EMOTION) {
				Spanned span = EmotionHelper.parseToImageText(context,
						contents[i].str, 20);
				contentView.append(span);
			} else {
				contentView.append(contents[i].str);
			}
		}
		// ContentActivity.textStyle.setTextStyle(contentView, 0XFF5F911B,
		// Color.YELLOW, null);
		return contentView.getText();
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

	private JsonObject isResponseOk(JsonObject object) {
		String responseCode = object.getString("err");
		if (!"mcphp.ok".equals(responseCode)) {
			// homeList.removeFooter(footer);
			// 错误信息处理
			return null;
		}
		return object;
	}

	// -----------------------------------------addMe----------------------------
	// ----------------------
	public HomeData parserAddMeItem(Context context, JsonObject object) {
		HomeData homeData = new HomeData();
		// ----------------------------发布者------------------------------------
		homeData.PublicUser = new User();
		parserAddMePublicUser(context, homeData.PublicUser, object);
		// ----------------------------源-----------------------------------------
		homeData.originUser = new User();

		parserAddMeOriginUser(context, homeData.originUser, object);
		// ------------------------------数据处理------------------------------------
		if ("1".equals(homeData.PublicUser.feedType)) {// 0原创1转发2喜欢
			if (NewsUtil.isEmpty(homeData.originUser.name)) {
				homeData.originUser = null;
			} else {
				// homeData.originUser.content = homeData.originUser.name + "  "
				// + homeData.originUser.content;
				// homeData.originUser.charSequence = createStyle(context,
				// homeData.originUser.content,
				// homeData.originUser.name.length());
//				getCharSqence(context, homeData.originUser.name,
//						homeData.originUser.contents);
//				int end = 0;
//				if (homeData.originUser.name != null) {
//					end = homeData.originUser.name.length();
//				}
//				homeData.originUser.charSequence = createStyle(context, end);
				homeData.originUser.nameCharSequence=createStyle(homeData.originUser.name);
			}
			// homeData.originUser.content = homeData.originUser.name + "  "
			// + homeData.originUser.content;
			// homeData.originUser.charSequence = createStyle(context,
			// homeData.originUser.content,
			// homeData.originUser.name.length());
		}
		return homeData;
	}

	private void parserAddMeOriginUser(Context context, User user,
			JsonObject json) {
		if (user == null || json == null) {
			return;
		}
		// -----------------------------------------------------
		user.postId = json.getString("org_post_postid");
		user.uId = json.getString("org_post_uid");
		user.name = json.getString("org_post_uname");
		// 取转发消息的名称和内容。在sb时，拼进去。
		JsonObject obj = json.getJsonObject("org_content");
		if (obj != null) {// 内容
			String data = obj.getString("content");
			StringBuffer sb = new StringBuffer();
			JsonObject tuan = (JsonObject) obj.getJsonObject("tuan");
			String loc = null;
			if (tuan != null) {
				loc = tuan.getString("loc");
			}
			if (loc != null && !("".equals(loc.trim()))) {
				sb.append("<a href=\"");
				sb.append(loc);
				sb.append("\" >");
				sb.append(loc);
				sb.append("</a><br/>");
			}
			user.price = obj.getString("price");
			if(user.price!=null&&!("".equals(user.price.trim()))){
				user.price=user.price+"元/人";
			}
			if (data != null || !("".equals(data.trim()))) {
				sb.append(data);
			}
			data = sb.toString();
			if (data != null || !("".equals(data.trim()))) {
				user.contents = ParserContent.getParser().parser(
						data.toCharArray());
				// user.charSequence = combineContent(context, user.contents,
				// isCombine);
			}
			if (user.contents != null) {
				// sb = new StringBuffer();
				// for (ContentData d : user.contents) {
				// sb.append(d.str);
				// }
				// user.content = sb.toString();
				user.charSequence = getCharSqence(context, null, user.contents);
			}
			obj = null;
		}
		// json.getString("has_img");
		JsonArray jsonArray = json.getJsonArray("arr_org_img_id");
		parserContentImgArray(user, jsonArray);// 图片信息
		user.place = json.getString("org_place");
		user.like_num = json.getString("like_num");
		user.comment_num = json.getString("cmt_num");
		user.transpond_num = json.getString("trans_num");
	}

	private void parserAddMePublicUser(Context context, User user,
			JsonObject json) {
		if (user == null || json == null) {
			return;
		}
		user.postId = json.getString("postid");
		user.uId = json.getString("post_uid");
		user.name = json.getString("post_uname");
		user.img_head_id = json.getString("head_imgid");
		//1:men 2:women
		String sex = json.getString("sex");
		user.sex=1;
		if(!TextUtils.isEmpty(sex)){
			user.sex =Integer.parseInt(sex);
		}
		
		//0:普通 1:专家2：达人 3商户
		String type = json.getString("user_type");
		if(!TextUtils.isEmpty(type)){
			user.type= Integer.parseInt(type);
		}
		user.feedType = json.getString("type");// 消息类型：0表示原创,1表示转发，2表示喜欢"
		if (user.feedType == null) {
			user.feedType = "" + json.getNum("type", 0);
		}
		if (user.feedType != null) {
			user.feedType = user.feedType.trim();
		}
		// 取转发消息的名称和内容。在sb时，拼进去。
		JsonObject obj = json.getJsonObject("va_post_content");
		if (obj != null) {// 内容
			String data = obj.getString("content");
			StringBuffer sb = new StringBuffer();
			JsonObject tuan = (JsonObject) obj.getJsonObject("tuan");
			String loc = null;
			if (tuan != null) {
				loc = tuan.getString("loc");
			}
			if (loc != null && !("".equals(loc.trim()))) {
				sb.append("<a href=\"");
				sb.append(loc);
				sb.append("\" >");
				sb.append(loc);
				sb.append("</a><br/>");
			}
			user.price = obj.getString("price");
			if(user.price!=null&&!("".equals(user.price.trim()))){
				user.price=user.price+"元/人";
			}
			if (data != null || !("".equals(data.trim()))) {
				sb.append(data);
			}
			data = sb.toString();
			if (data != null || !("".equals(data.trim()))) {
				user.contents = ParserContent.getParser().parser(
						data.toCharArray());
				// user.charSequence = combineContent(context, user.contents,
				// isCombine);
			}
			if (user.contents != null) {
				// sb = new StringBuffer();
				// for (ContentData d : user.contents) {
				// sb.append(d.str);
				// }
				// user.content = sb.toString();
				user.charSequence = getCharSqence(context, null, user.contents);
			}
			obj = null;
		}
		// json.getString("has_img");
		JsonArray jsonArray = json.getJsonArray("va_img_info");
		parserContentImgArray(user, jsonArray);// 图片信息
		user.time = json.getString("modify_time");
		user.place = json.getString("place");
		String where = json.getString("from_where");
		if (where != null && "0".equals(where)) {
			user.fromWhere = context.getResources().getString(
					R.string.feed_from_net);
		} else if (where != null && "1".equals(where)) {
			user.fromWhere = context.getResources().getString(
					R.string.feed_from_android);
		} else if (where != null && "2".equals(where)) {
			user.fromWhere = context.getResources().getString(
					R.string.feed_from_apple);
		}
		user.like_num = json.getString("like_num");
		user.comment_num = json.getString("cmt_num");
		user.transpond_num = json.getString("trans_num");
	}

	public List<HomeData> parserAddMe(Context context, JsonObject object) {
		if (isResponseOk(object) == null) {
			return null;
		}
		JsonObject dataObj = object.getJsonObject("data");
		JsonObject data = dataObj.getJsonObject("rpcret");
		JsonArray jsonArray = data.getJsonArray("info");
		if (jsonArray == null || jsonArray.size() < 1) {
			return null;
		}
		List<HomeData> list = new ArrayList<HomeData>();
		JsonObject json = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			json = (JsonObject) jsonArray.get(i);
			list.add(parserAddMeItem(context, json));
		}
		clear();
		return list;
	}

	public void parserPlace(User user, JsonObject json){
		if(json != null){
			try {
				user.lat = json.getString("place_y");
				user.lon = json.getString("place_x");
				user.address = json.getString("address");
				int placeType = -1;
				try {
					placeType = Integer.parseInt(json.getString("type"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				user.placeType = placeType;
				user.refId = json.getString("ref_id");
				user.place = json.getString("place_name");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
