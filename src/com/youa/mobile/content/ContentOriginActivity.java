package com.youa.mobile.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.content.action.CommentAction;
import com.youa.mobile.content.action.FriendContentAction;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.utils.Tools;

public class ContentOriginActivity extends ContentActivity {
	final public static String ORIGIN_UESER_ID = "user_id";
	final public static String ORIGIN_FEED_ID = "feed_id";

	// ---------------------------------updata-data----------------------------------
	private void updateFriendContentData(final HomeData data) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (data == null) {
					showToast(ContentOriginActivity.this,
							R.string.feed_friend_content);
					return;
				}
				listView.setVisibility(View.VISIBLE);
				contentListView.setFeedId(data.PublicUser.postId);
				updateContent(data);
				isContentGet = true;
			}
		});

	}

	@Override
	protected void setTranpondC(boolean isOrigin) {
		if (mData.PublicUser.contents == null) {
			return;
		}
		if (isOrigin) {
//			contentView.setVisibility(View.GONE);
//			contentView.setText("");
//			publicContentView.setVisibility(View.VISIBLE);
//			combineContent(publicContentView, mData.PublicUser.contents);
			publicContentView.setVisibility(View.GONE);
			publicContentView.setText("");
			contentView.setText("");
			contentView.setVisibility(View.VISIBLE);
			combineContent(contentView, mData.PublicUser.contents);
		} else {
			publicContentView.setVisibility(View.VISIBLE);
			// transpondCase.setBackgroundResource(R.drawable.feed_transpond_bg);
			// transpondCase.setPadding(10, 10, 10, 10);
			// publicContentView.setText(data.PublicUser.charSequence);
			combineContent(publicContentView, mData.PublicUser.contents);
		}
	}

	protected void combineContent(TextView contentView, ContentData[] contents) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].type == ContentData.TYPE_AT) {
				contentView
						.append(Html.fromHtml("<a href=\"" + contents[i].href
								+ "\" >" + contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_TOPIC) {
				contentView
						.append(Html.fromHtml("<a href=\"" + contents[i].href
								+ "\" >" + contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_LINK) {
				contentView
						.append(Html.fromHtml("<a href=\"" + contents[i].href
								+ "\" >" + contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_EMOTION) {
				// Spanned span = EmotionHelper.parseToImageText(this,
				// contents[i].str, 16);
				// contentView.append(span);
				contentView.append(Html.fromHtml("<a href=\"img\" >"
						+ contents[i].str + "</a>"));
			} else {
				contentView.append(contents[i].str);
			}
		}
//		int exWidth = 41;
//		if (ApplicationManager.getInstance().getDensityDpi() >= 160) {
//			exWidth = 64;
//		}
//		exWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//				exWidth, getResources().getDisplayMetrics());
		textStyle.setTextStyle(contentView, 0XFF5F911B, Color.YELLOW, null);
//		contentView.setTag(contentView.getText());
//		Tools.setLimitText(contentView, ApplicationManager.getInstance()
//				.getWidth(), 5, exWidth);// 根据不同分辨率写不同的大小
//		CharSequence charSequence = (CharSequence) contentView.getTag();
//		if (contentView.getText().length() < charSequence.length()) {
//			contentView.append(" ");
//			contentView.append(Html.fromHtml("<a href=\"\" >" + "全文" + "</a>"));
//		}
//		textStyle.setTextStyle(contentView, 0XFF5F911B, Color.YELLOW,"全文");
	}

	private void updateCommentData(
			final List<FeedContentCommentData> commentList, final String commId) {
		while (!isContentGet) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
		isContentGet = true;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				contentListView.closeHeaderFooter();
				if (commentList == null) {
					progressBar.setVisibility(View.GONE);
					contentListView.addData(null,
							PageSize.HOME_CONTENT_PAGESIZE);
					return;
				}
				progressBar.setVisibility(View.GONE);
				List<FeedContentCommentData> data = contentListView.getData();
				if (!"-1".equals(commId) && data != null) {
					// data.addAll(commentList);
					contentListView.addData(commentList,
							PageSize.HOME_CONTENT_PAGESIZE);
				} else {// 刷新
					contentListView.setData(commentList,
							PageSize.HOME_CONTENT_PAGESIZE);
				}
			}
		});
	}

	// -----------------------------------load-data-------------------------------------
	@Override
	protected void loadFriendContentData() {
		listView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		isContentGet = false;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String postId = bundle.getString(ORIGIN_FEED_ID);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FriendContentAction.PARAM_FEED_ID, postId);
		ActionController.post(this, FriendContentAction.class, params,
				new FriendContentAction.ISearchResultListener() {

					@Override
					public void onFail(int resourceID) {
						showToast(ContentOriginActivity.this, resourceID);
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onEnd(HomeData contentData) {
						updateFriendContentData(contentData);
					}
				}, true);
	}

	@Override
	protected void loadCommentData(boolean isRefresh) {
		if (isRefresh) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String postId = bundle.getString(ORIGIN_FEED_ID);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(CommentAction.PARAM_POST_ID, postId);
		params.put(CommentAction.PARAM_OFFSET, isRefresh ? "-1" : getMinId());
		isRefresh = false;
		ActionController.post(this, CommentAction.class, params,
				new CommentAction.ISearchResultListener() {

					@Override
					public void onFail(int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								progressBar.setVisibility(View.GONE);
							}
						});
						showToast(ContentOriginActivity.this, resourceID);
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onEnd(List<FeedContentCommentData> commentList,
							String commId) {
						updateCommentData(commentList, commId);
					}
				}, true);
	}

}
