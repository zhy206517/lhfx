package com.youa.mobile.friend.friendmanager;

import java.util.List;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.action.ManagerAttentionCountAction;
import com.youa.mobile.friend.action.ManagerSuperClassifyAction;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.HomePageListConfig;
import com.youa.mobile.friend.friendsearch.FriendSearchActivity;
import com.youa.mobile.friend.friendsearch.FriendSearchAdapter;
import com.youa.mobile.information.AttentUserListPage;
import com.youa.mobile.information.data.ShowCountData;
import com.youa.mobile.life.ClassifySuperPeoplePage;
import com.youa.mobile.life.SupperPeopleClassifyPage;
import com.youa.mobile.life.data.FindPeopleData;
import com.youa.mobile.life.data.SuperPeopleClassify;

public class FriendManagerActivity extends BasePage {
	private LinearLayout mRight;
	private List<ManagerSuperMenCalssifyData> mDataList;
	// private ListView mListView;
	// private FriendManagerAdapter mAdapter;
	private LinearLayoutForListView mListView;
	private AdapterForLinearLayout mAdaper;
	private ManagerListView mManagerListView;
	private ProgressBar mProgressBar;
	private Handler mHandler = new Handler();
	private RelativeLayout mAddFriend;
	private RelativeLayout mAttentionedFriend;
	private TextView mAttentionText;
	private UpdataBroadCastReceiver mUpdataBroadCastReceiver = new UpdataBroadCastReceiver();

	class UpdataBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE.equals(action)) {
				loadAttentionCount();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_friend_manager);
		initViews();
		loadSuperClassifyData();
		loadAttentionCount();
		IntentFilter fileter = new IntentFilter();
		fileter.addAction(LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE);
		this.registerReceiver(mUpdataBroadCastReceiver, fileter);
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mHandler.post(new Runnable() {
//					@Override
//					public void run() {
//						Animation alphaAnimation = new AlphaAnimation(0.0f,
//								1.0f);
//						alphaAnimation.setDuration(300);
//						// 设置动画时间 alphaAnimation.setDuration(3000);
//						mRight.startAnimation(alphaAnimation);
//						mRight.setVisibility(View.VISIBLE);
//					}
//				});
//			}
//		}, 300);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mUpdataBroadCastReceiver);
	}

	private void initViews() {
		ManagerClickLisener clickListener = new ManagerClickLisener();
		// ------------
		mRight = (LinearLayout) findViewById(R.id.back);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		// -----
		mAddFriend = (RelativeLayout) findViewById(R.id.friend_addfriend);
		mAttentionedFriend = (RelativeLayout) findViewById(R.id.friend_attention);
		mAttentionText = (TextView) findViewById(R.id.attentionText);
		View header = LayoutInflater.from(FriendManagerActivity.this).inflate(
				R.layout.feed_friend_manager_header, null);
		// mListView=(ListView)findViewById(R.id.friend_list);
		// mAdapter = new FriendManagerAdapter(this);
		// mListView.setAdapter(mAdapter);
		// -------------
		mListView = (LinearLayoutForListView) findViewById(R.id.friend_list);
		mAdaper = new AdapterForLinearLayout(this);

		// mListView.addHeaderView(header);
		//

		// mManagerListView = new ManagerListView(mListView, header, null);
		// ------------
		mRight.setOnClickListener(clickListener);
		// mListView.setOnItemClickListener(mOnItemClickListener);
		mListView.setOnclickLinstener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int positon = ((Integer) v.getTag()).intValue();
				ManagerSuperMenCalssifyData data = mDataList.get(positon);
				if (data != null) {
					Intent i = new Intent(FriendManagerActivity.this,
							ClassifySuperPeoplePage.class);
					i.putExtra(ClassifySuperPeoplePage.TITLE, data.name);
					// i.putExtra(LoadFeedAction.SHARECLASSIFY_ID, classid);
					startActivity(i);
				}

			}
		});
		mAddFriend.setOnClickListener(clickListener);
		mAttentionedFriend.setOnClickListener(clickListener);
		// mButtonSearch.setOnClickListener(clickListener);
		// ------------
	}

	private void loadAttentionCount() {
		ActionController.post(FriendManagerActivity.this,
				ManagerAttentionCountAction.class, null,
				new ManagerAttentionCountAction.ISearchResultListener() {

					@Override
					public void onFail(int resourceID) {

					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFinish(ShowCountData data) {
						mAttentionText.setText("已关注(" + data.getAttentCount()
								+ ")");
					}
				});
	}

	private void loadSuperClassifyData() {
		mProgressBar.setVisibility(View.VISIBLE);
		ActionController.post(FriendManagerActivity.this,
				ManagerSuperClassifyAction.class, null,
				new ManagerSuperClassifyAction.ISearchResultListener() {

					@Override
					public void onFail(int resourceID) {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mProgressBar.setVisibility(View.GONE);
							}
						});
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFinish(List<ManagerSuperMenCalssifyData> list) {
						updateView(list);
					}
				});
	}

	private void updateView(List<ManagerSuperMenCalssifyData> list) {
		mDataList = list;
		mAdaper.setData(list);
		mListView.setAdapter(mAdaper);
		// mAdapter.setData(list);
		// mManagerListView.setData(list, HomePageListConfig.REQUEST_COUNT);
		mProgressBar.setVisibility(View.GONE);
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ManagerSuperMenCalssifyData data = mDataList.get(position);
			if (data != null) {
				Intent i = new Intent(FriendManagerActivity.this,
						ClassifySuperPeoplePage.class);
				i.putExtra(ClassifySuperPeoplePage.TITLE, data.name);
				// i.putExtra(LoadFeedAction.SHARECLASSIFY_ID, classid);
				startActivity(i);
			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
//			mRight.setVisibility(View.INVISIBLE);
		}
		return super.onKeyDown(keyCode, event);
	}

	private class ManagerClickLisener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
//				mRight.setVisibility(View.INVISIBLE);
				FriendManagerActivity.this.finish();
				break;
			case R.id.friend_addfriend:
				Intent intent = new Intent(FriendManagerActivity.this,
						FriendSearchActivity.class);
				startActivity(intent);
				break;
			case R.id.friend_attention:
				intent = new Intent(FriendManagerActivity.this,
						AttentUserListPage.class);
				intent.putExtra(AttentUserListPage.KEY_UID, ApplicationManager
						.getInstance().getUserId());
				intent.putExtra(AttentUserListPage.KEY_ATTENT_TYPE,
						AttentUserListPage.ATTENT_TYPE_ATTENT);
				startActivity(intent);
				break;
			}

		}

	}
}
