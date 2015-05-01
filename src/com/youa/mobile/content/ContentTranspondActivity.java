package com.youa.mobile.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.content.action.CommentAction;
import com.youa.mobile.content.action.FriendContentAction;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.friend.data.HomeData;

public class ContentTranspondActivity extends ContentActivity {
	final public static String TRANSPOND_UESER_ID = "user_id";
	final public static String TRANSPOND_FEED_ID = "transpond_id";

	// ---------------------------------updata-data----------------------------------
	private void updateFriendContentData(final HomeData data) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (data == null) {
					showToast(ContentTranspondActivity.this,
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

	//
	// private void updateContent(HomeData data) {
	// if (data.public_name != null) {
	// nameView.setText(data.public_name);
	// nameView.setVisibility(View.VISIBLE);
	// } else {
	// nameView.setVisibility(View.GONE);
	// }
	// // public不为空，是对public的解析
	// if (data.publicContents != null) {// 转发者内容
	// // publicContent.setText(data.publicContent);
	// publicContent.setVisibility(View.VISIBLE);
	// combineContent(publicContent, data.publicContents);
	// transpond.setBackgroundResource(R.drawable.feed_transpond_bg);
	// }
	// // else if(data.feedType==){//是喜欢
	// // publicContent.append(Html.fromHtml("<img src='"
	// // + R.drawable.ic_like_focus + "'/>", getImage(), null));//喜欢
	// //
	// // }
	// else {
	// transpond.setBackgroundDrawable(null);
	// publicContent.setVisibility(View.GONE);
	// }
	// // if (data.contentImg != null) {
	// contentImgView.setVisibility(View.VISIBLE);// //针对不同分辨率，进行处理
	// contentImgView.setBackgroundResource(R.drawable.feed_img_bg);
	// // contentImgView
	// // .setBackgroundResource(data.contentImg.length > 1 ?
	// // R.drawable.feed_img_array_bg
	// // : R.drawable.feed_img_bg);
	// // } else {
	// // holder.contentImgView.setVisibility(View.GONE);
	// // }
	// if (data.content != null) {// 引用内容
	// // content.setEllipsize(TruncateAt.END);
	// content.setVisibility(View.VISIBLE);
	// content.setText(data.content);
	// Tools.setLimitText(content, ApplicationManager.getInstance()
	// .getWidth(), 3, 0);
	// } else {
	// content.setVisibility(View.GONE);
	// }
	// // if (data.place == null || data.price == null) {
	// // commerial.setVisibility(View.GONE);
	// // } else {
	// commerial.setVisibility(View.VISIBLE);
	// place.setText(data.place);
	// price.setText(data.price);
	// // }
	// if (data.fromWhere != null) {
	// formWhere.setText(data.fromWhere);
	// formWhere.setVisibility(View.VISIBLE);
	// } else {
	// formWhere.setVisibility(View.GONE);
	// }
	// like.setText(data.like_num);
	// comment.setText(data.comment_num);
	// transport.setText(data.transpond_num);
	// }

	@Override
	protected void setTranpondC(boolean isOrigin) {
		if (isOrigin) {
			publicContentView.setVisibility(View.GONE);
			publicContentView.setText("");
			contentView.setVisibility(View.VISIBLE);
			combineContent(contentView, mData.PublicUser.contents);
		} else {
			publicContentView.setVisibility(View.VISIBLE);
			transpondCase.setBackgroundResource(R.drawable.feed_reply_area_bg);
//			 transpondCase.setPadding(18, 0, 0, 0);
			// publicContentView.setText(data.PublicUser.charSequence);
			combineContent(publicContentView, mData.PublicUser.contents);
		}
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
//					data.addAll(commentList);
					contentListView.addData(commentList, PageSize.HOME_CONTENT_PAGESIZE);
				} else {
					contentListView.setData(commentList, PageSize.HOME_CONTENT_PAGESIZE);
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
		String postId = bundle.getString(TRANSPOND_FEED_ID);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FriendContentAction.PARAM_FEED_ID, postId);
		ActionController.post(this, FriendContentAction.class, params,
				new FriendContentAction.ISearchResultListener() {

					@Override
					public void onFail(int resourceID) {
						showToast(ContentTranspondActivity.this, resourceID);
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
		String postId = bundle.getString(TRANSPOND_FEED_ID);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(CommentAction.PARAM_POST_ID, postId);// "396341"
		params.put(CommentAction.PARAM_OFFSET, isRefresh ? "-1" : getMinId());
		isRefresh = false;
		ActionController.post(this, CommentAction.class, params,
				new CommentAction.ISearchResultListener() {

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								progressBar.setVisibility(View.GONE);
								showToast(ContentTranspondActivity.this,
										resourceID);
							}
						});
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
