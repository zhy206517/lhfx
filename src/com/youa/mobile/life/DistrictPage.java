package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.life.action.DistrictAction;
import com.youa.mobile.life.action.DistrictAction.RequestType;
import com.youa.mobile.life.data.DistrictData;

public class DistrictPage extends BasePage implements OnClickListener,
		OnItemClickListener {
	private static final String TAG = "DistrictPage";

	private List<DistrictData> mDistrictList = new ArrayList<DistrictData>(0);
	private ImageButton mGoBackBtn;
	private Button mSendBtn;
	private TextView mTitle;
	private ListView mDistrictListView;
	private DistrictItemAdapter mListViewAdapter;
	private ProgressBar mProgressBar;
	private static final int REQUEST_SHOW_DISTRICT_FEEDS = 100;
	public static final String UPDATE_DISTRICT_STATUS = "district_status";
	public static final String UPDATE_DISTRICT_POSITION = "district_position";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_district);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(DistrictAction.KEY_START,
				String.valueOf(mDistrictList.size()));
		params.put(DistrictAction.KEY_REQUEST_TYPE, RequestType.LOADATA);
		loadData(params);
	}

	private void initView() {
		mSendBtn = (Button) findViewById(R.id.send);
		mTitle = (TextView) findViewById(R.id.title);
		mGoBackBtn = (ImageButton) findViewById(R.id.back);
		mSendBtn.setVisibility(View.GONE);
		mTitle.setText(R.string.life_hot_district);
		mGoBackBtn.setOnClickListener(this);

		mDistrictListView = (ListView) findViewById(R.id.district_list);
		mDistrictListView.setOnItemClickListener(this);
		mListViewAdapter = new DistrictItemAdapter(this, mDistrictList);
		mDistrictListView.setAdapter(mListViewAdapter);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	private void loadData(Map<String, Object> params) {
		ActionController.post(this, DistrictAction.class, params,
				new DistrictAction.DistrictActionResultListener() {

					@Override
					public void onStart(Integer resourceID) {

					}

					@Override
					public void onLoadDataFinish(List<DistrictData> data) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mProgressBar.setVisibility(View.GONE);
							}
						});
						if (null == data) {
							showToast(R.string.topic_load_error);
						} else {
							updataView(data);
						}
					}

					@Override
					public void onFinish(final int resourceID,
							final int position, final boolean status) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mProgressBar.setVisibility(View.GONE);
								showToast(resourceID);
								updataBtn(position, status);
							}
						});
					}

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mProgressBar.setVisibility(View.GONE);
								showToast(resourceID);
							}
						});
					}
				}, true);
	}

	private void updataBtn(final int position, boolean status) {
		mDistrictList.get(position).isFollowed = status;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListViewAdapter.notifyDataSetChanged();
				sendBroadcast(new Intent(
						LehuoIntent.ACTION_USER_DISTRICT_CHANGE));
			}
		});
	}

	private void updataView(final List<DistrictData> data) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mDistrictList.clear();
				mDistrictList.addAll(data);
				mListViewAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent();
		i.setClass(this, DistricFeedPage.class);
		DistrictData dis = mDistrictList.get(position);
		i.putExtra(DistricFeedPage.KEYWORD, dis.name);
		i.putExtra(DistricFeedPage.DISTRIC_ID, dis.id);
		i.putExtra(DistricFeedPage.DISTRIC_STATUS, dis.isFollowed);
		i.putExtra(UPDATE_DISTRICT_STATUS, position);
		startActivityForResult(i, REQUEST_SHOW_DISTRICT_FEEDS);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.district_status_btn:
			int position = (Integer) v.getTag();
			DistrictData district = mDistrictList.get(position);
			RequestType requestType;
			if (district.isFollowed) {
				requestType = RequestType.UNFOLLOW;
			} else {
				requestType = RequestType.FOLLOW;
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(DistrictAction.KEY_DISTRICT_ID, district.id);
			params.put(DistrictAction.KEY_REQUEST_TYPE, requestType);
			params.put(DistrictAction.KEY_TOPIC_ITEM_POSITION, position);
			loadData(params);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent intent) {
		if (REQUEST_SHOW_DISTRICT_FEEDS == requestCode
				&& resultCode == RESULT_OK) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					int position = intent.getIntExtra(UPDATE_DISTRICT_POSITION, -1);
					boolean mTopicStatus = intent.getBooleanExtra(UPDATE_DISTRICT_STATUS, mDistrictList.get(position).isFollowed);
					updataBtn(position, mTopicStatus);
				}
			});
		
		}
	}
}
