package com.youa.mobile.friend.friendsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
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
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.UserListView;
import com.youa.mobile.friend.action.FriendSearchAction;
import com.youa.mobile.friend.friendsearch.MyEditText.Listener;
import com.youa.mobile.information.AttentUserListPage;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.ClassifySuperPeoplePage;
import com.youa.mobile.life.action.RequestFindSuperPeopleAction;
import com.youa.mobile.life.action.RequestSearchAction;
import com.youa.mobile.life.data.SuperPeopleData;

public class FriendSearchActivity extends BasePage implements
		BaseListView.OnScrollEndListener, OnItemClickListener {

	private MyEditText mEditView;
	private Button mButton;
	private UserListView userList;
	private ListView mListView;
	private View footer;
	private LinearLayout header;
	private ImageButton mImageButton;
	private TextView mTextView;
	private TextView mTextNull;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_friend_manager_search);
		initViews();
		loadSuperPeopleData();
	}

	private void initViews() {
		mProcessView = (ProgressBar) findViewById(R.id.progressBar);
		mInflater = LayoutInflater.from(this);
		mImageButton = (ImageButton) findViewById(R.id.back);
		mImageButton.setVisibility(View.VISIBLE);
		mTextView = (TextView) findViewById(R.id.title);
		mTextView.setText(getResources().getString(
				R.string.feed_friend_manager_addfriend));
		mEditView = (MyEditText) findViewById(R.id.search_edit);
		mButton = (Button) findViewById(R.id.search_button);
		header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		footer = getInflaterLayout(R.layout.feed_footer);
		mListView = (ListView) findViewById(R.id.search_gridView);
		mListView.setOnItemClickListener(this);
		mTextNull = (TextView) findViewById(R.id.text_null);
		userList = new UserListView(mListView, header, footer, mHandler);
		// ------------------------------------------------------
		SearchClickLisener clickListener = new SearchClickLisener();
		mButton.setOnClickListener(clickListener);
		mImageButton.setOnClickListener(clickListener);
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

	private void updateData(final List<SuperPeopleData> list) {
		mHandler.post(new Runnable() {
			public void run() {
				userList.closeHeaderFooter();
				mListView.setVisibility(View.VISIBLE);
				if (list != null && list.size() > 0) {
					mTextNull.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					userList.setData(list, PageSize.HOME_FEED_PAGESIZE);
					userList.setLockEnd(true);
					if (list.size() < 20) {
						userList.setLockEnd(true);
					}
				}
			}
		});
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
				&& event.getAction() == KeyEvent.ACTION_UP) {
			String searchKey = mEditView.getText().toString().trim();
			if (InputUtil.isEmpty(searchKey)) {
				return true;
			}
			Intent i = new Intent(FriendSearchActivity.this,
					FriendSearchResultActivity.class);
			i.putExtra(RequestSearchAction.KEY_SEARCH_KEY, searchKey);
			startActivity(i);
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			List<SuperPeopleData> list = userList.getData();
			SuperPeopleData data = list.get(position);
			if (data != null) {
				// Intent intent = new Intent(FriendSearchActivity.this,
				// UserInfoPage.class);
				// // intent.putExtra(KEY_USER_NAME, mTitleView.getText());
				// intent.putExtra(UserInfoPage.KEY_UID, data.userId);
				// startActivity(intent);
				// ----------------------------
				Intent intent = new Intent(FriendSearchActivity.this,
						PersonnalInforPage.class);
				intent.putExtra(PersonnalInforPage.KEY_USER_ID,
						data.getUserId());
				intent.putExtra(PersonnalInforPage.KEY_USER_NAME,
						data.getUserName());
				startActivity(intent);
			}

		}
	};

	private class SearchClickLisener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				FriendSearchActivity.this.finish();
				break;
			case R.id.search_button:
				String searchKey = mEditView.getText().toString().trim();
				if (InputUtil.isEmpty(searchKey)) {
					return;
				}
				Intent i = new Intent(FriendSearchActivity.this,
						FriendSearchResultActivity.class);
				i.putExtra(RequestSearchAction.KEY_SEARCH_KEY, searchKey);
				startActivity(i);
				break;
			}

		}

	}

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollEnd() {
		// TODO Auto-generated method stub

	}

	private void loadSuperPeopleData() {
		Map<String, Object> para = new HashMap<String, Object>();
		ActionController
				.post(this,
						RequestFindSuperPeopleAction.class,
						para,
						new RequestFindSuperPeopleAction.ISearchSuperPeopleResultListener() {

							@Override
							public void onFinish(List<SuperPeopleData> list) {
								hiddenProgressView();
								updateData(list);
							}

							@Override
							public void onFail(int resourceID) {
								hiddenProgressView();
								showToast(FriendSearchActivity.this, resourceID);
							}

							@Override
							public void onStart() {
								showProgressView();
							}
						}, true);
	}
}
