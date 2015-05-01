package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.action.RequestSearchAction;
import com.youa.mobile.life.data.FindPeopleData;
import com.youa.mobile.news.util.NewsUtil;

public class SearchPage extends BasePage {

	private static final String TAG = "SearchPage";
	private ImageButton 		mBackButton;
	private EditText 			mSearchEdit;
	private Button 				mSearchButton;
//	private RelativeRadioGroup 	mSearchTypeRadioGroup;
	private ListView 			mListView;
	private TextView 			mNullText;
	private MyListAdpter mListAdpter;
	private List<FindPeopleData> mDataList = new ArrayList<FindPeopleData>();
	private String mSearchKey;
	private int mRadioType;
	public final static int RADIO_TYPE_PEOPLE = 1;
	public final static int RADIO_TYPE_TOPIC = 2;
	public final static int RADIO_TYPE_SHOP = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_search);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mSearchKey = getIntent().getExtras().getString(FindLifeMainPage.SEARCH_KEY);
			mRadioType = getIntent().getExtras().getInt(FindLifeMainPage.SEARCH_TYPE, RADIO_TYPE_PEOPLE);
		} else {
			finish();
		}
		initViews();
		loadData(mSearchKey, mRadioType);
	}

	private void loadData(String key, int type) {
		NewsUtil.LOGD(TAG, "enter loadData  <ke y> : " + key);
		NewsUtil.LOGD(TAG, "enter loadData  <type> : " + type);
    	Map<String,Object>	params = new HashMap<String, Object>();
    	params.put(RequestSearchAction.KEY_SEARCH_KEY, key);
		params.put(RequestSearchAction.KEY_SEARCH_TYPE, type);
//    	ActionController.post(
//    			this,
//    			RequestSearchAction.class,
//    			params,
//    			new RequestSearchAction.ISearchResultListener() {
//					@Override
//					public void onFinish(List<FindPeopleData> list) {
//						hiddenProgressView();
//						updateViews(list);
//					}
//					@Override
//					public void onStart() {
//						showProgressView();
//					}
//
//					public void onFail(int resourceID) {
//						hiddenProgressView();
//						showToast(resourceID);
//					}
//    			},
//    			true);
		
	}

	private void updateViews(final List<FindPeopleData> list){
		if (!NewsUtil.isEmpty(list)) {
			mHandler.post(new Runnable(){
				public void run() {
					mNullText.setVisibility(View.GONE);
					mDataList.clear();
					mDataList.addAll(list);
					mListAdpter.notifyDataSetChanged();
				}
			});
		} else {
			mHandler.post(new Runnable(){
				public void run() {
					mNullText.setVisibility(View.VISIBLE);
					mDataList.clear();
					mListAdpter.notifyDataSetChanged();
				}
			});
		}
    }

	private void initViews() {
		mBackButton				= (ImageButton) findViewById(R.id.back);
		mSearchEdit 			= (EditText) findViewById(R.id.search_keyword);
		mSearchButton 			= (Button) findViewById(R.id.search);
//		mSearchTypeRadioGroup 	= (RelativeRadioGroup) findViewById(R.id.radio_group);
		mListView 				= (ListView) findViewById(R.id.list);
		mProcessView 			= (ProgressBar) findViewById(R.id.progressBar);
		mNullText 				= (TextView) findViewById(R.id.text_null);
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
		mListAdpter = new MyListAdpter(this);
		mListView.setAdapter(mListAdpter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		mSearchButton.setOnClickListener(mOnClickListener);
//		mSearchTypeRadioGroup.check(R.id.radio_find_people);
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
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			FindPeopleData data = mDataList.get(position);
			if (data != null) {
				startPeoplePage(data);
			}
			
		}
	};

	private void startPeoplePage(FindPeopleData data) {
    	Intent intent = new Intent(this, PersonnalInforPage.class);
    	intent.putExtra(PersonnalInforPage.KEY_USER_ID, data.getUserId());
    	intent.putExtra(PersonnalInforPage.KEY_USER_NAME, data.getUserName());
    	startActivity(intent);
    	
    }


	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String searchKey = mSearchEdit.getText().toString();
			if (InputUtil.isEmpty(searchKey)) {
				return;
			}
			loadData(searchKey, mRadioType);
			
		}
		
	};
	private class MyListAdpter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		private final class ViewHolder {
			TextView tv;
		}
		
		public MyListAdpter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
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
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.life_find_people_item, null);
				holder.tv = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final FindPeopleData data = mDataList.get(position);
			holder.tv.setText(data.getUserName());
			return convertView;
		}
		
	}
}
