package com.youa.mobile.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.news.action.requestFavoritePeopleListAction;
import com.youa.mobile.news.data.FavoritePeopleData;
import com.youa.mobile.news.util.NewsUtil;

public class FavoritePeopleListPage extends BasePage {

	private final static String TAG = "FavoritePeopleListPage";
	public final static String KEY_FEED_ID = "F";

	private ImageButton mBackButton;
	private ListView mPeopleListView;
	private PeopleListAdapter mPeopleListAdapter;
	private List<FavoritePeopleData> mDataList = new ArrayList<FavoritePeopleData>();
	private String feedId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_favorite_people);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			feedId = getIntent().getExtras().getString(KEY_FEED_ID);
		}
		initViews();
		loadDate();
	}

	private void loadDate() {
		if (feedId == null) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(requestFavoritePeopleListAction.KEY_FEED_ID, feedId);
		ActionController
				.post(this,
						requestFavoritePeopleListAction.class,
						params,
						new requestFavoritePeopleListAction.IFavoriteFavoritePeopleResultListener() {

							@Override
							public void onFinish(List<FavoritePeopleData> list) {
								hiddenProgressView();
								updateViews(list);
							}

							@Override
							public void onFail(int resourceID) {
								hiddenProgressView();
								showToast(FavoritePeopleListPage.this,
										resourceID);
							}

							@Override
							public void onStart() {
								showProgressView();
							}
						}, true);
	}

	private void updateViews(final List<FavoritePeopleData> list) {
		if (!NewsUtil.isEmpty(list)) {
			NewsUtil.LOGD(TAG,
					" enter onFinish() data <list.size> : " + list.size());
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mDataList.clear();
					mDataList.addAll(list);
					mPeopleListAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	private void initViews() {
		mBackButton = (ImageButton) findViewById(R.id.back);
		mBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mPeopleListAdapter = new PeopleListAdapter();
		mPeopleListView = (ListView) findViewById(R.id.people_list);
		mPeopleListView.setAdapter(mPeopleListAdapter);
		mPeopleListView.setOnItemClickListener(onItemClickListener);
		mProcessView = (ProgressBar) findViewById(R.id.progressBar);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			FavoritePeopleData data = mDataList.get(position);
			startPeoplePage(data);
		}

	};

	private void startPeoplePage(FavoritePeopleData data) {
		Intent intent = new Intent(this, PersonnalInforPage.class);
		intent.putExtra(PersonnalInforPage.KEY_USER_ID, data.getUId());
		intent.putExtra(PersonnalInforPage.KEY_USER_NAME, data.getUName());
		startActivity(intent);

	}

	private class PeopleListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(FavoritePeopleListPage.this);
				convertView = inflater.inflate(
						R.layout.news_favorite_people_item, null);
			}
			FavoritePeopleData data = mDataList.get(position);
			ImageView head = (ImageView) convertView
					.findViewById(R.id.head_image);
			TextView name = (TextView) convertView.findViewById(R.id.name);
			int resId = R.drawable.head_men;
			if ("2".equals(data.getSex())) {
				resId = R.drawable.head_women;
			}
			ImageUtil.setImageView(FavoritePeopleListPage.this, head,
					data.getUImageId(), ImageUtil.HEADER_SIZE_BIG,
					ImageUtil.HEADER_SIZE_BIG, resId);
			name.setText(data.getUName());
			convertView.setTag(R.id.TOPIC_TAG_KEY, data);
			return convertView;
		}
	}

}
