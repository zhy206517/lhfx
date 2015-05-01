package com.youa.mobile.friend.friendsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.UserListView;
import com.youa.mobile.friend.action.FriendSearchAction;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.action.RequestSearchAction;
import com.youa.mobile.life.data.SuperPeopleData;

public class FriendSearchResultActivity extends BasePage implements
		BaseListView.OnScrollEndListener, OnItemClickListener {
	private UserListView userList;
	private ListView mListView;
	private View footer;
	private LinearLayout header;
	private TextView mTextView;
	private TextView mTextNull;
	private LayoutInflater mInflater;
	private int mPageIndex = -1;
	private ImageButton mback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_friend_manager_search);
		initViews();
		loadData(true);
	}

	private void initViews() {
		mProcessView = (ProgressBar) findViewById(R.id.progressBar);
		findViewById(R.id.search_input_area).setVisibility(View.GONE);
		findViewById(R.id.serch_people_lable).setVisibility(View.GONE);
		mback = (ImageButton) findViewById(R.id.back);
		mback.setVisibility(View.VISIBLE);
		mback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FriendSearchResultActivity.this.finish();
			}
		});
		mInflater = LayoutInflater.from(this);
		mTextView = (TextView) findViewById(R.id.title);
		mTextView.setText(getResources().getString(
				R.string.search_people_result));
		header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		footer = getInflaterLayout(R.layout.feed_footer);
		mListView = (ListView) findViewById(R.id.search_gridView);
		mListView.setOnItemClickListener(this);
		mTextNull = (TextView) findViewById(R.id.text_null);
		userList = new UserListView(mListView, header, footer, mHandler);
		userList.setOnScrollEndListener(this);
		toTopInit();
		toEndInit();
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position,
			long id) {
		if (userList.getHeader() != null) {
			position -= 1;
		}
		List<SuperPeopleData> list = userList.getData();
		if (position < 0 || position >= list.size()) {
			return;
		}
		SuperPeopleData mUserData = userList.getData().get(position);
		Intent intent = new Intent(this, PersonnalInforPage.class);
		intent.putExtra(PersonnalInforPage.KEY_USER_ID, mUserData.getUserId());
		intent.putExtra(PersonnalInforPage.KEY_USER_NAME,
				mUserData.getUserName());
		startActivity(intent);
	}

	private void loadData(final boolean isRefreshOrGetMore) {
		String searchKey = this.getIntent().getStringExtra(
				RequestSearchAction.KEY_SEARCH_KEY);
		if (InputUtil.isEmpty(searchKey)) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FriendSearchAction.KEY_SEARCH_KEY, searchKey);
		if (isRefreshOrGetMore) {
			params.put(FriendSearchAction.KEY_START_POSTION, 0);
		} else {
			int pageIndex = mPageIndex + 1;
			params.put(FriendSearchAction.KEY_START_POSTION, pageIndex);
		}
		ActionController.post(FriendSearchResultActivity.this,
				FriendSearchAction.class, params,
				new FriendSearchAction.ISearchResultListener() {
					@Override
					public void onFail(int resourceID) {
						hiddenProgressView();
					}

					@Override
					public void onStart() {
						showProgressView();
					}

					@Override
					public void onFinish(List<SuperPeopleData> list) {
						hiddenProgressView();
						updateViews(list, isRefreshOrGetMore);
					}
				});
	}

	private void updateViews(final List<SuperPeopleData> list,
			final boolean isRefreshOrGetMore) {
		mHandler.post(new Runnable() {
			public void run() {
				userList.closeHeaderFooter();
				mListView.setVisibility(View.VISIBLE);
				if (isRefreshOrGetMore) {// 刷新
					if (list != null && list.size() > 0) {
						mTextNull.setVisibility(View.GONE);
						mListView.setVisibility(View.VISIBLE);
						userList.setData(list, PageSize.HOME_FEED_PAGESIZE);

						if (list.size() < 20) {
							userList.setLockEnd(true);
						}
						mPageIndex = 0;
					} else {
						mTextNull.setVisibility(View.VISIBLE);
						mListView.setVisibility(View.GONE);
					}
				} else {
					userList.addData(list, PageSize.HOME_FEED_PAGESIZE);
					mPageIndex++;
					userList.setLockEnd(false);
				}

			}
		});
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			List<SuperPeopleData> list = userList.getData();
			SuperPeopleData data = list.get(position);
			if (data != null) {
				Intent intent = new Intent(FriendSearchResultActivity.this,
						PersonnalInforPage.class);
				intent.putExtra(PersonnalInforPage.KEY_USER_ID,
						data.getUserId());
				intent.putExtra(PersonnalInforPage.KEY_USER_NAME,
						data.getUserName());
				startActivity(intent);
			}

		}
	};
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private TextView footer_tipsTextview;
	private TextView footer_lastUpdatedTextView;
	private ImageView footer_arrowImageView;
	private ProgressBar footer_progressBar;

	private void toEndInit() {
		footer_arrowImageView = (ImageView) footer
				.findViewById(R.id.head_arrowImageView);
		footer_progressBar = (ProgressBar) footer
				.findViewById(R.id.head_progressBar);
		footer_tipsTextview = (TextView) footer
				.findViewById(R.id.head_tipsTextView);
		footer_lastUpdatedTextView = (TextView) footer
				.findViewById(R.id.head_lastUpdatedTextView);
	}

	private void toTopInit() {
		arrowImageView = (ImageView) header
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) header.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) header.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) header
				.findViewById(R.id.head_lastUpdatedTextView);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}

	public View getInflaterLayout(int resource) {
		return mInflater.inflate(resource, null);
	}

	@Override
	public void onScrollHeader() {
		loadData(true);

	}

	@Override
	public void onScrollEnd() {
		loadData(false);

	}
}
