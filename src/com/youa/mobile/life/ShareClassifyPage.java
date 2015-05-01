package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.youa.mobile.R;
import com.youa.mobile.boutique.WaterfallPage;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.life.action.LoadFeedAction;
import com.youa.mobile.life.action.LoadShareClassifyAction;
import com.youa.mobile.life.data.ShareClassifyData;
import com.youa.mobile.theme.action.TopicAction;

public class ShareClassifyPage extends BasePage {
	private List<ShareClassifyData> mShareify = new ArrayList<ShareClassifyData>(
			0);
	// private List<TopicData> mResultList = new ArrayList<TopicData>(0);
	private LinearLayout mBack;
	private ListView mShareifyListView;
	private ProgressBar mProgressBar;
	private LifeSearchResultAdapter<ShareClassifyData> mShareifyAdapter;
	private String mShareclassifyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_share_classify);
		mShareclassifyId = getIntent().getStringExtra(
				LoadFeedAction.SHARECLASSIFY_ID);
		initViews();
		loadData();
		// mHandler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// mHandler.post(new Runnable() {
		// @Override
		// public void run() {
		// Animation alphaAnimation = new AlphaAnimation(0.0f,
		// 1.0f);
		// alphaAnimation.setDuration(300);
		// // 设置动画时间 alphaAnimation.setDuration(3000);
		// mBack.startAnimation(alphaAnimation);
		// mBack.setVisibility(View.VISIBLE);
		// }
		// });
		// }
		// }, 300);

	}

	private void initViews() {
		mBack = (LinearLayout) findViewById(R.id.back);
		mBack.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:
					switch (v.getId()) {
					case R.id.back:
						// Animation alphaAnimation = new AlphaAnimation(1.0f,
						// 0.0f);
						// alphaAnimation.setDuration(300);
						// //设置动画时间 alphaAnimation.setDuration(3000);
						// mBack.startAnimation(alphaAnimation);
						// mBack.setVisibility(View.INVISIBLE);
						ShareClassifyPage.this.finish();
						// overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
						break;
					}
				}
				return true;
			}
		});
		mShareifyListView = (ListView) findViewById(R.id.shareify_list);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mShareifyAdapter = new LifeSearchResultAdapter<ShareClassifyData>(this,
				R.layout.life_search_user, R.id.name, mShareify, null);
		mShareifyListView.setAdapter(mShareifyAdapter);
		mShareifyListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object item = view.getTag();
				Intent i = new Intent(ShareClassifyPage.this,
						WaterfallPage.class);
				i.putExtra(CommonFeedPage.TITLE,
						((ShareClassifyData) item).name);
				i.putExtra(LoadFeedAction.SHARECLASSIFY_ID,
						((ShareClassifyData) item).id);
				if (position == 0) {
					i.putExtra(CommonFeedPage.TITLE, ShareClassifyPage.this
							.getString(R.string.home_title_lable));
				}
				setResult(RESULT_OK, i);
				ShareClassifyPage.this.finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// mBack.setVisibility(View.INVISIBLE);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void loadData() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TopicAction.ACTION_TYPE, TopicAction.ACTION_TYPE_GETALL);
		params.put(TopicAction.TOPIC_TYPE, TopicAction.TOPIC_UI_COMMEND);
		ActionController.post(this, LoadShareClassifyAction.class, params,
				new LoadShareClassifyAction.ShareClassifyActionListener() {

					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mProgressBar.setVisibility(View.VISIBLE);
							}
						});
					}

					@Override
					public void onFinish(List<ShareClassifyData> data) {
						mShareify.clear();
						ShareClassifyData shareClassifyData = new ShareClassifyData();
						shareClassifyData.name = "精选";
						shareClassifyData.id="";
						if (TextUtils.isEmpty(mShareclassifyId)) {
							shareClassifyData.rightImage = R.drawable.list_select_done;
						}
						mShareify.add(shareClassifyData);
						boolean flag = false;
						for (ShareClassifyData shareData : data) {
							if (shareData.id.equals(mShareclassifyId)) {
								shareData.rightImage = R.drawable.list_select_done;
								flag = true;
							}
							mShareify.add(shareData);
						}
						updateView();
					}

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(ShareClassifyPage.this, resourceID);
								mProgressBar.setVisibility(View.GONE);
							}
						});
					}
				}, true);
	}

	private void updateView() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mShareifyAdapter.notifyDataSetChanged();
						mProgressBar.setVisibility(View.GONE);
					}
				});
			}
		});
	}
}
