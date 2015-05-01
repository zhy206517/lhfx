package com.youa.mobile.login.auth;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.renren.api.connect.android.AsyncRenren;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.feed.FeedPublishRequestParam;
import com.renren.api.connect.android.feed.FeedPublishResponseBean;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.http.Callback;
import com.weibo.net.AccessToken;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;
import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.friend.data.ContentImg;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.input.data.ImageData;
import com.youa.mobile.input.data.PublishData;
import com.youa.mobile.login.util.LoginUtil;
import com.youa.mobile.parser.ContentData;

public class ShareUtil {

	public enum HomeDataImageType{
		IMG_URL, LOCAL_IMG_PATH, CACHED_IMG
	}
	
	public static String SHARE_CONTENT_LEHO_URL = "http://www.leho.com/post/";
	private static String TAG = "ShareUtil";

//	public static void shareTo(HomeData mData, BaseAuthPage c,
//			SupportSite site){
//		shareTo(mData, c, site, HomeDataImageType.CACHED_IMG, new ShareToListener(c));
//	}
	
//	public static void shareTo(HomeData mData, Context c,
//			SupportSite site, HomeDataImageType imgType, boolean autoOpenAuthPage){
//		shareTo(mData, c, site, imgType, autoOpenAuthPage, new ShareToListener(c));
//	}
	
	public static void shareTo(HomeData mData, Context c,
			SupportSite site, HomeDataImageType imgType, boolean autoOpenAuthPage, ShareToCallBack callBack) {
		try {
			if (mData == null || mData.PublicUser == null){
				callBack.onFail(site, c.getString(R.string.sns_share_data_null));
				return;
			}
			BaseToken token = BaseToken.getTokenInstanceBySupportSite(site, c);
			if (token != null) {
				token.initToken(c);
				String accessToken = token.token;
				String thirdUid = token.userid;
				if (token.isBinded(c)) {
					if(token.isExpired(c)){
						callBack.onFail(site, c.getString(R.string.sns_auth_expired));
						return;
					}
					callBack.onStart();
					switch (token.site) {
					case QQ:
						shareToQQZone(mData, c, accessToken, thirdUid, imgType, callBack);
						break;
					case SINA:
						shareToSina(mData, c, token, imgType, callBack);
						break;
					case RENREN:
						shareToRenren(mData, c, imgType, callBack);
						break;
					default:
						break;
					}
					return;
				}else if(autoOpenAuthPage){
					if(c instanceof BaseAuthPage){
						BaseAuthPage authPage = (BaseAuthPage)c;
						LoginUtil.openThirdAuthPage(authPage, site);
					}
				}
			}else if(autoOpenAuthPage){
				if(c instanceof BaseAuthPage){
					BaseAuthPage authPage = (BaseAuthPage)c;
					LoginUtil.openThirdAuthPage(authPage, site);
				}
			}else{
				callBack.onFail(site, c.getString(R.string.sns_auth_no_bind_expired));
			}
		} catch (Exception e) {
			callBack.onFail(site, c.getString(R.string.sns_share_fail_unknow));
			e.printStackTrace();
		}
	}

	private static void shareToQQZone(HomeData mData, Context c,
			String accessToken, String thirdUid, HomeDataImageType imgType, final ShareToCallBack callBack) {
		if (mData == null || mData.PublicUser == null) {
			callBack.onFail(SupportSite.QQ, c.getString(R.string.sns_share_data_null));
			return;
		}
		Bundle bundle = null;
		User publicUser = mData.PublicUser;
		// ContentData[] contents = publicUser.contents;
		// StringBuffer content = new StringBuffer();
		// if(contents != null){
		// for(ContentData data : contents){
		// content.append(data.str);
		// }
		// }
		String content = getContent(mData);
		if (!TextUtils.isEmpty(content) && content.length() > 80) {
			content = content.substring(0, 77);
			content += "...";
		}
		String imgs = getImgs(mData, imgType, true, "|", false);

		bundle = new Bundle();
		bundle.putString("title",
				c.getString(R.string.sns_share_title, publicUser.name));// 必须。feeds的标题，最长36个中文字，超出部分会被截断。//如
																		// xxx
																		// 的分享
																		// 爱乐活-发现你对生活的热爱
		bundle.putString("url", SHARE_CONTENT_LEHO_URL + publicUser.postId);// 必须。分享所在网页资源的链接，点击后跳转至第三方网页，
																			// 请以http://开头。
		bundle.putString("comment", "");// 用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
		bundle.putString("summary", content);// 所分享的网页资源的摘要内容，或者是网页的概要描述。
												// 最长80个中文字，超出部分会被截断。
		if (imgs != null && !"".equals(imgs)) {
			bundle.putString("images", imgs);// 所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。//"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif"
		}
		bundle.putString("type", "4");// 分享内容的类型。
		// bundle.putString("playurl",
		// "http://player.youku.com/player.php/Type/Folder/Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");//长度限制为256字节。仅在type=5的时候有效。
		TencentOpenAPI.addShare(accessToken, QQToken.app_Key, thirdUid, bundle,
				new Callback() {
					@Override
					public void onSuccess(Object arg0) {
						callBack.onSuccess(SupportSite.QQ);
					}

					@Override
					public void onFail(int arg0, String arg1) {
						Log.d(TAG, arg1 + "\t" + arg0);
						callBack.onFail(SupportSite.QQ,
								arg1);
					}
				});
	}
	
	private static void shareToSina(HomeData mData, final Context c,
			BaseToken token, HomeDataImageType imgType, final ShareToCallBack callBack) {
		SinaToken sinaToken = SinaToken.getInstance(c);
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(Weibo.APP_KEY, Weibo.APP_SECRET);
		AccessToken accessToken = new AccessToken(sinaToken.token,
				Weibo.APP_SECRET);
		accessToken.setUserid(sinaToken.userid);
		weibo.setAccessToken(accessToken);
		String content = getContent(mData);
//		String imgPath = mData.PublicUser.contentImg[0].img_content_id;
		String imgPath = getImgs(mData, imgType, false, "", true);
//		String imgUrl = getImgs(mData, false, "", false);
		WeiboParameters bundle = new WeiboParameters();
		String url = Weibo.SERVER + "statuses/update.json";
		if (!TextUtils.isEmpty(imgPath)) {
			bundle.add("pic", imgPath);
//			bundle.add("url", imgUrl);
			url = Weibo.SERVER + "statuses/upload.json"; //图片上传
//			url = Weibo.SERVER + "statuses/upload_url_text.json"; // url发布图片
		}
		String lhpostUrl = SHARE_CONTENT_LEHO_URL + mData.PublicUser.postId;
		int contentSize = 140 - lhpostUrl.length() - 3;
		if (contentSize <= 0) {
			content = c.getString(R.string.sns_share_title,
					mData.PublicUser.name) + "..." + lhpostUrl;
		} else {
			if (content.length() > contentSize) {
				content = content.substring(0, contentSize) + "..." + lhpostUrl;
			} else {
				content = content + "..." + lhpostUrl;
			}
		}
		bundle.add("status", content);
		// String url = Weibo.SERVER + "statuses/upload.json";
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
		weiboRunner.request(c, url, bundle, Utility.HTTPMETHOD_POST,
				new RequestListener() {
					@Override
					public void onIOException(IOException e) {
						Log.d(TAG, e.getMessage(), e);
						callBack.onFail(SupportSite.SINA, "");
					}

					@Override
					public void onError(WeiboException e) {
						Log.d(TAG, e.getMessage(), e);
						String msg = "";
						if(e.getStatusCode() == 40025){
							msg = c.getString(R.string.sns_share_same_weibo);
						}
						callBack.onFail(SupportSite.SINA, msg);
					}

					@Override
					public void onComplete(String response) {
						Log.d(TAG, response);
						callBack.onSuccess(SupportSite.SINA);
					}
				});
	}

	private static void shareToRenren(HomeData mData, final Context c, HomeDataImageType imgType,
			final ShareToCallBack callBack) {
		int NAME_MAX_LENGTH = 30;
		int DESCRIPTION_MAX_LENGTH = 200;
		RenrenToken token = RenrenToken.getInstance(c);
		Renren renren = token.getRenren(c);
		if (renren != null) {
			AsyncRenren asyncRenren = new AsyncRenren(renren);

			String name = c.getString(R.string.sns_share_title,
					mData.PublicUser.name); // 新鲜事标题
			if (name.length() > NAME_MAX_LENGTH) {
				name.substring(0, NAME_MAX_LENGTH);
			}
			String url = SHARE_CONTENT_LEHO_URL + mData.PublicUser.postId; // 新鲜事标题和图片指向的链接
			String description = getContent(mData); // 新鲜事主体内容
			if (description != null) {
				if (description.length() > DESCRIPTION_MAX_LENGTH) {
					description = description.substring(0,
							DESCRIPTION_MAX_LENGTH);
				}
			}

			String imgUrl = getImgs(mData, imgType, false, "", false);
			FeedPublishRequestParam param = new FeedPublishRequestParam(name,
					description, url, imgUrl, "爱乐活的分享", "去看看", url, description);
			asyncRenren.publishFeed(param,
					new AbstractRequestListener<FeedPublishResponseBean>() {

						@Override
						public void onRenrenError(RenrenError err) {
							Log.d(TAG, err.getOrgResponse());
							Log.d(TAG, "", err);
							String errMsg = ","+err.getMessage();
							int errCode = err.getErrorCode();
							if( errCode == 303 || errCode == 7){
								errMsg= c.getString(R.string.sns_share_too_more);
							}
							callBack.onFail(SupportSite.RENREN, errMsg);
						}

						@Override
						public void onFault(Throwable err) {
							Log.d(TAG, "", err);
							callBack.onFail(SupportSite.RENREN, "");
						}

						@Override
						public void onComplete(FeedPublishResponseBean arg0) {
							// TODO Auto-generated method stub
							Log.d(TAG, "发布成功 ， 文章编号为：" + arg0.getPostId());
							callBack.onSuccess(SupportSite.RENREN);
						}
					}, true);
		} else {
			callBack.onFail(SupportSite.RENREN, c.getString(R.string.sns_share_getbindinfo_fail));
		}
	}

	private static String getContent(HomeData mData) {
		User publicUser = mData.PublicUser;
		ContentData[] contents = publicUser.contents;
		StringBuffer content = new StringBuffer();
		if (contents != null) {
			for (ContentData data : contents) {
				content.append(data.str);
				if (data.type == ContentData.TYPE_AT)
					content.append(" ");
			}
		}
		return content.toString();
	}

	/**
	 * 
	 * @param mData
	 * @param mutil
	 *            多个还是单个
	 * @param split
	 *            多个时的分隔符
	 * @param isPath
	 *            (path / url);
	 * @return
	 */
	public static String getImgs(HomeData mData, HomeDataImageType imgType, boolean mutil, String split, boolean isPath) {
		User publishUser = mData.PublicUser;
		if(publishUser == null || publishUser.contentImg == null || publishUser.contentImg.length < 1){
			return null;
		}
		switch (imgType) {
		case CACHED_IMG:
			return getImgs(publishUser, mutil, split, isPath);
		case IMG_URL:
		case LOCAL_IMG_PATH:
			StringBuffer imgsBuf = null;
			if(mutil){
				for(ContentImg img : publishUser.contentImg){
					if (imgsBuf == null) {
						imgsBuf = new StringBuffer();
						imgsBuf.append(img.img_content_id);
					}else{
						if(TextUtils.isEmpty(split))
							split = "";
						imgsBuf.append(split).append(img.img_content_id);
					}
				}
			}else{
				imgsBuf = new StringBuffer();
				imgsBuf.append(publishUser.contentImg[0].img_content_id);
			}
			return imgsBuf.toString();
		default:
			return "";
		}
	}

	/**
	 * 
	 * @param mData
	 * @param mutil
	 *            多个还是单个
	 * @param split
	 *            多个时的分隔符
	 * @param isPath
	 *            (path / url);
	 * @return
	 */
	public static String getImgs(User user, boolean mutil, String split,
			boolean isPath) {
		String imgs = "";
		int width;
		int height;
		if (user.contentImgHeight != 0 || user.contentImgWidth != 0) {
			width = user.contentImgWidth;
			height = user.contentImgHeight;
		} else {
			if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
				// 背景86,图片80
				width = ImageUtil.CONTENT_SIZE_LITTLE;
				height = ImageUtil.CONTENT_SIZE_LITTLE;
			} else {// 背景126,图片120
				width = ImageUtil.CONTENT_SIZE_LARGE;
				height = ImageUtil.CONTENT_SIZE_LARGE;
			}

		}

		if (user.contentImg != null) {
			StringBuffer imgsBuf = null;
			for (ContentImg img : user.contentImg) {
				if (isPath) {
					if (mutil) {
						if (imgsBuf == null) {
							imgsBuf = new StringBuffer();
						} else {
							imgsBuf.append(split);
						}
					} else {
						if (imgsBuf == null) {
							imgsBuf = new StringBuffer();
							String imgPath = SavePathManager.getImagePath()
									+ ImageUtil.getImageURL(img.img_content_id,
											width, height);
							imgsBuf.append(imgPath);
							break;
						}
					}
					String imgPath = SavePathManager.getImagePath()
							+ ImageUtil.getImageURL(img.img_content_id, width,
									height);
					imgsBuf.append(imgPath);
				} else {
					if (mutil) {
						if (imgsBuf == null) {
							imgsBuf = new StringBuffer();
						} else {
							imgsBuf.append(split);
						}
					} else {
						if (imgsBuf == null) {
							imgsBuf = new StringBuffer();
							String imgUrl = ImageUtil.getImageURL(
									img.img_content_id);//, width, height
							imgsBuf.append(imgUrl);
							break;
						}
					}
					String imgId_key = img.img_content_id;
					String imgUrl = ImageUtil.getImageURL(imgId_key);
					imgsBuf.append(imgUrl);
				}

			}
			if (imgsBuf != null) {
				imgs = imgsBuf.toString();
			}
		}
		return imgs;
	}

	public interface ShareToCallBack {
		public void onStart();

		public void onSuccess(SupportSite site);

		public void onFail(SupportSite site, final String msg);
	}
	
//	public static class ShareToListener implements ShareToCallBack{
//		private Context c;
//		
//		public ShareToListener(Context c) {
//			this.c = c;
//		}
//
//		@Override
//		public void onStart() {
//			showTost(c.getString(R.string.sns_shareding));
//		}
//
//		@Override
//		public void onSuccess(SupportSite site) {
//			String msg = c.getString(R.string.share_sns_success, site.getSiteName(c));
//			showTost(msg);
//		}
//
//		@Override
//		public void onFail(SupportSite site, final String msg) {
//			String msgStr = c.getString(R.string.share_sns_fail_msg, site.getSiteName(c), msg);
//			showTost(msgStr);
//		}
//		
//		private void showTost(final String msg){
//			((Activity)c).runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					Handler mHander = new Handler();
//					mHander.post(new Runnable() {
//						@Override
//						public void run() {
//							Toast.makeText(c.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//						}
//					});
//				}
//			});
//		}
//	}
	
	private static HomeData copyHomeDataUsedShare(HomeData feed) throws Exception{
		User publicUser = new User();
		User publicUserOld = feed.PublicUser ;
		if(publicUserOld != null && publicUserOld.contentImg != null){
			publicUser.contentImg = new ContentImg[publicUserOld.contentImg.length];
			for(int i = 0 ; i < publicUserOld.contentImg.length; i++){
				publicUser.contentImg[i] = new ContentImg();
				publicUser.contentImg[i].height = publicUserOld.contentImg[i].height;
				publicUser.contentImg[i].width = publicUserOld.contentImg[i].width;
				publicUser.contentImg[i].img_content_id = new String (publicUserOld.contentImg[i].img_content_id);
				publicUser.contentImg[i].img_desc = publicUserOld.contentImg[i].img_desc;
			}
		}
		ContentData contentData = new ContentData();
		contentData.str = publicUserOld.contents[0].str;
		ContentData[] contents = {contentData};
		publicUser.contents = contents;
		publicUser.postId = publicUserOld.postId;
		publicUser.name = publicUserOld.name;
		feed = new HomeData();
		feed.PublicUser = publicUser;
		return feed;
	}
	
	public static void shareToThird(final Context c, final HomeData feed, final PublishData data, final ShareToCallBack callBack){
		List<ImageData> images = data.getContentImage();
		final List<BaseToken> shareTokens = data.getTokens();
		final String firstImagePath = (images != null && images.size() > 0) ? images.get(0).imagePath : null;
		if(shareTokens == null)
			return;
		for (final BaseToken token : shareTokens) {
			if(token != null && token.isSessionValid(c) && token.isSync){
				new Thread() {
					public void run() {
						HomeData shareFeed = feed;
						HomeDataImageType type = HomeDataImageType.CACHED_IMG;
						try {
							if(token.site == SupportSite.SINA){
								if(shareFeed.PublicUser != null && shareFeed.PublicUser.contentImg!=null && shareFeed.PublicUser.contentImg.length > 0){
									shareFeed = ShareUtil.copyHomeDataUsedShare(feed);
									shareFeed.PublicUser.contentImg[0].img_content_id = firstImagePath; //新浪微博直接取路径
									type = HomeDataImageType.LOCAL_IMG_PATH;
								}
							}
							ShareUtil.shareTo(shareFeed, c, token.site, type, false, callBack);
						} catch (Exception e) {
							String msg = c.getString(R.string.sns_share_fail_unknow);
							//msg = c.getString(R.string.share_sns_fail_msg, token.site.getSiteName(c), msg);
							callBack.onFail(token.site, msg);
//							new Thread(new Runnable() {
//								@Override
//								public void run() {
//									Handler mHander = new Handler();
//									mHander.post(new Runnable() {
//										@Override
//										public void run() {
//											String msg = c.getString(R.string.sns_share_fail_unknow);
//											msg = c.getString(R.string.share_sns_fail_msg, token.site.getSiteName(c), msg);
//											Toast.makeText(c.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//										}
//									});
//								}
//							}).start();
							e.printStackTrace();
						}
		 			}
				}.start();
			}
		}
	}
	
}
