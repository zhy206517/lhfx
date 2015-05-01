package com.youa.mobile.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.action.RequestSearchAction;
import com.youa.mobile.location.data.LocationData;
import com.youa.mobile.news.util.NewsUtil;

public class SearchLocationPage extends BasePage {
	private static final String TAG = "SearchLocationPage";
	private ImageButton mBackButton;
	private EditText mSearchEdit;
	private Button mSearchButton;
	private ListView mListView;
	private TextView mNullText;
	private SearchLocationAdapter mListAdpter;
	private List<LocationData> mDataList = new ArrayList<LocationData>();
	private String mSearchKey;
	private LocationData mLocationData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_search);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mSearchKey = getIntent().getExtras().getString(MapPage.SEARCH_KEY);
		} else {
			finish();
		}
		initViews();
		mLocationData = new LocationData();
		loadData(mSearchKey);
	}

	private void loadData(String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(RequestSearchAction.KEY_SEARCH_KEY, key);
		params.put(MapPage.SEARCH_TYPE, MapPage.SEARCH_BY_KEYWORD);
		mSearchKey = key;
		ActionController.post(this, LocationAction.class, params,
				new LocationAction.ILocationActionResultListener() {
					@Override
					public void onFail(final int resourceID) {
						hiddenProgressView();
						showToast(resourceID);
					}

					@Override
					public void onStart(Integer resourceID) {
						showProgressView();
					}

					@Override
					public void onGetAllFinish(List<LocationData> locList) {
						hiddenProgressView();
						mLocationData.locName = mSearchKey;
						updateViews(locList);
					}

					@Override
					public void onFinish(int resourceID, int position,
							boolean topicSubStatus) {
					}
				}, true);

	}

	private void updateViews(final List<LocationData> list) {
		if (!NewsUtil.isEmpty(list)) {
			mHandler.post(new Runnable() {
				public void run() {
					mNullText.setVisibility(View.GONE);
					mDataList.clear();
					mDataList.add(mLocationData);
					mDataList.addAll(list);
					mListAdpter.notifyDataSetChanged();
				}
			});
		} else {
			mHandler.post(new Runnable() {
				public void run() {
					// mNullText.setVisibility(View.VISIBLE);
					mDataList.clear();
					mDataList.add(mLocationData);
					mListAdpter.notifyDataSetChanged();
				}
			});
		}
	}

	private void initViews() {
		mBackButton = (ImageButton) findViewById(R.id.back);
		mSearchEdit = (EditText) findViewById(R.id.search_keyword);
		mSearchButton = (Button) findViewById(R.id.search);
		mListView = (ListView) findViewById(R.id.list);
		mProcessView = (ProgressBar) findViewById(R.id.progressBar);
		mNullText = (TextView) findViewById(R.id.text_null);
		mSearchEdit.setText(mSearchKey);
		mSearchEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				checkSearchButtonShouldClickOrNot();
			}
		});
		mListAdpter = new SearchLocationAdapter(this, mDataList);
		mListView.setAdapter(mListAdpter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchKey = mSearchEdit.getText().toString();
				if (InputUtil.isEmpty(searchKey)) {
					return;
				}
				loadData(searchKey);
			}
		});
		// mSearchTypeRadioGroup.check(R.id.radio_find_people);
		mBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mSearchEdit.setSelection(mSearchEdit.getText().length());
				checkSearchButtonShouldClickOrNot();
			}
		});
	}

	private void checkSearchButtonShouldClickOrNot() {
		String searchKey = mSearchEdit.getText().toString();
		if (InputUtil.isEmpty(searchKey)) {
			mSearchButton.setTextColor(0xffa7a7a7);
		} else {
			mSearchButton.setTextColor(Color.BLACK);
		}
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LocationData data = mDataList.get(position);
			if (data != null) {
				Intent intent = new Intent();
				intent.putExtra(MapPage.KEY_PlACE_NAME, data.locName);
				intent.putExtra(MapPage.KEY_PID, data.sPid);
				intent.putExtra(MapPage.KEY_LAT, String.valueOf(data.latitude));
				intent.putExtra(MapPage.KEY_LON, String.valueOf(data.longitude));
				setResult(RESULT_OK, intent);
				finish();
			}

		}
	};

	public class SearchLocationAdapter extends BaseAdapter {
		private Context context;
		private List<LocationData> list = new ArrayList<LocationData>();
		private String address;
		private String plid;

		private class LocationHolder {
			private ImageView addressIcon;
			private TextView placeName;
			private TextView addressName;
			private ImageView addressSel;
		}

		public SearchLocationAdapter(Context context, List<LocationData> list) {
			this.context = context;
			this.list = list;
		}

		public SearchLocationAdapter(Context context, List<LocationData> list,
				String address, String tagPlid) {
			this.context = context;
			this.list = list;
			this.address = address;
			this.plid = tagPlid;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LocationHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.location_list_item,
						null);
				holder = new LocationHolder();
				holder.addressIcon = (ImageView) convertView
						.findViewById(R.id.address_icon);
				holder.placeName = (TextView) convertView
						.findViewById(R.id.place_name);
				holder.addressName = (TextView) convertView
						.findViewById(R.id.place_address);
				holder.addressSel = (ImageView) convertView
						.findViewById(R.id.address_selector);
				convertView.setTag(holder);
			} else {
				holder = (LocationHolder) convertView.getTag();
			}
			LocationData loc = (LocationData) getItem(position);
			if(loc==null){
				return convertView;
			}
			holder.addressIcon.setImageResource(R.drawable.address_icon);
			holder.addressSel.setVisibility(View.GONE);
			if (TextUtils.isEmpty(plid)) {
				if (!TextUtils.isEmpty(address) && address.equals(loc.locName)) {
					holder.addressIcon
							.setImageResource(R.drawable.address_selector_icon);
					holder.addressSel.setVisibility(View.VISIBLE);
				}
			} else if (plid.equals(loc.sPid)) {
				holder.addressIcon
						.setImageResource(R.drawable.address_selector_icon);
				holder.addressSel.setVisibility(View.VISIBLE);
			}

			if(position==0){
				ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.argb(255, 0, 0, 255));
				SpannableStringBuilder style = new SpannableStringBuilder(
						loc.locName);
				style.setSpan(colorSpan, 0, loc.locName.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.placeName.setText(style);
			}else{
				holder.placeName.setText(loc.locName);
			}
			holder.addressName.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(loc.addName)) {
				holder.addressName.setText(loc.addName);
				holder.addressName.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

	}
}
