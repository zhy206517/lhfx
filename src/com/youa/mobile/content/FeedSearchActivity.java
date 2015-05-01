package com.youa.mobile.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.AbstractListView.ShowEmptyViewListener;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.friendsearch.MyEditText;
import com.youa.mobile.friend.friendsearch.MyEditText.Listener;
import com.youa.mobile.theme.action.ThemeAction;
import com.youa.mobile.utils.LogUtil;

public class FeedSearchActivity extends BasePage {

	private WaterfallListView mListView;
	// private ArrayList<HomeData> mDataList;
	private MyEditText mEditView;
	private ImageButton mImageButton;
	private TextView mTextView;
	private TextView mTextNull;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_search);
		// mDataList = new ArrayList<HomeData>();
		initViews();
	}

	private void initViews() {
		mImageButton = (ImageButton) findViewById(R.id.back);
		mImageButton.setVisibility(View.VISIBLE);
		mTextView = (TextView) findViewById(R.id.title);
		mTextView.setText(getResources().getString(R.string.life_search));
		mEditView = (MyEditText) findViewById(R.id.search_edit_text);
		mTextNull = (TextView) findViewById(R.id.text_null);
		// ------------------------------------------------------
		mImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mEditView.setListener(new Listener() {

			@Override
			public void onClick() {
				mTextNull.setVisibility(View.GONE);
			}
		});

		ListView listView = (ListView) findViewById(R.id.feed_list);
		mListView = new WaterfallListView(listView, null, null);
		mListView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HomeData data = (HomeData) v.getTag();
				if (data == null || data.PublicUser == null) {
					return;
				}
				Intent intent = new Intent();
				if ("0".equals(data.PublicUser.feedType)) {
					intent.setClass(FeedSearchActivity.this, ContentOriginActivity.class);
					// 源动态id
					intent.putExtra(ContentOriginActivity.ORIGIN_FEED_ID, data.PublicUser.postId);
				} else {
					if (data.originUser == null) {
						return;
					}
					intent.setClass(FeedSearchActivity.this, ContentTranspondActivity.class);
					// 转发动态id
					intent.putExtra(ContentTranspondActivity.TRANSPOND_FEED_ID, data.originUser.postId);
				}
				startActivity(intent);
			}
		});
		mListView.setShowListener(new ShowEmptyViewListener() {

			@Override
			public void onShowEmptyView() {
				mTextNull.setVisibility(View.VISIBLE);
			}
		});
		mProcessView = findViewById(R.id.progressBar);
		mLoadView = listView;
	}

	public void toSearch(View view) {
		String searchKey = mEditView.getText().toString().trim();
		if (TextUtils.isEmpty(searchKey)) {
			return;
		}

		mTextNull.setVisibility(View.GONE);
		showProgressView();
		loadData(true, searchKey);
	}

	private void loadData(boolean isRefreshOrGetMore, String searchKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ThemeAction.PARAM_KEYWORD, searchKey);
		ActionController.post(FeedSearchActivity.this, ThemeAction.class, params, new ThemeAction.ISearchResultListener() {
			@Override
			public void onFail(int resourceID) {
				showToast(FeedSearchActivity.this, resourceID);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						hiddenProgressView();
					}
				});
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onEnd(List<HomeData> homeDataList) {
				if (homeDataList == null) {
					homeDataList = new ArrayList<HomeData>();
				}
				LogUtil.d(TAG, "onEnd. search result count = " + homeDataList.size());
				// ArrayList<HomeData> list = new ArrayList<HomeData>();
				// for (HomeData data : homeDataList) {
				// if (data != null) {
				// if (data.originUser != null) {
				// if (data.originUser.contentImg != null && data.originUser.contentImg.length > 0) {
				// list.add(data);
				// }
				// } else if (data.PublicUser != null) {
				// if (data.PublicUser.contentImg != null && data.PublicUser.contentImg.length > 0) {
				// list.add(data);
				// }
				// }
				// }
				// }
				updateViews(homeDataList);
				hiddenProgressView();
			}
		}, true);
	}

	private void updateViews(final List<HomeData> feedDataList) {
		mHandler.post(new Runnable() {
			public void run() {
				mListView.closeHeaderFooter();
				// mDataList.clear();
				if (feedDataList == null || feedDataList.isEmpty()) {
					mTextNull.setVisibility(View.VISIBLE);
				} else {
					mTextNull.setVisibility(View.GONE);
					// mDataList.addAll(feedDataList);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mEditView.getWindowToken(), 0);
				}
				mListView.setData(feedDataList, PageSize.INFO_FEED_PAGESIZE);
			}
		});
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
			toSearch(new View(this));
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	// public void toSearchOrLogin(View view) {
	// if ("0".equals(view.getTag())) {
	// startActivity(new Intent(this, LoginPage.class));
	// } else {
	// startActivity(new Intent(this, FeedSearchActivity.class));
	// }
	// }
}
