package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.action.LoadFeedAction;
import com.youa.mobile.life.action.RequestSearchAction;
import com.youa.mobile.life.data.DistrictData;
import com.youa.mobile.life.data.LifeItemData;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.life.data.UserInfo;
import com.youa.mobile.theme.TopicFeedPage;
import com.youa.mobile.theme.TopicSubPage;
import com.youa.mobile.theme.data.TopicData;

public class FindLifeMainPage extends BasePage implements RelativeRadioGroup.OnCheckedChangeListener {

	private EditText 			mSearchEdit;
	private Button 				mSearchButton;
	private RelativeRadioGroup 	mSearchTypeRadioGroup;
	private ListView 			mListView;
	public final static int RADIO_TYPE_PEOPLE = 1;
	public final static int RADIO_TYPE_TOPIC = 2;
	public final static int RADIO_TYPE_SHOP = 3;

	public final static String SEARCH_KEY = "search_key";
	public final static String SEARCH_TYPE = "search_type";
	private int radioType;
	
	private LifeSearchResultAdapter<?> mSearchResultAdapter;
	private List<LifeItemData> 	mDatas 					= new ArrayList<LifeItemData>(0);
	private List<UserInfo> 		userSearchResult 		= new ArrayList<UserInfo>(0);
	private List<TopicData> 	topicSearchResult 		= new ArrayList<TopicData>(0);
	private List<DistrictData> 	districtSearchResult 	= new ArrayList<DistrictData>(0);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_main);
		initListData();
		initViews();
	}

	private void initListData() {
		mDatas = new ArrayList<LifeItemData>();
		LifeItemData data = new LifeItemData(
				getResources().getString(R.string.life_hot_topic),
				R.drawable.life_hot_topic,
				TopicSubPage.class);
		mDatas.add(data);
		data = new LifeItemData(
				getResources().getString(R.string.life_hot_share),
				R.drawable.life_hot_share,
				CommonFeedPage.class);
		mDatas.add(data);
		data = new LifeItemData(
				getResources().getString(R.string.life_super_people),
				R.drawable.life_super_people,
				SupperPeopleClassifyPage.class);//SuperPeoplePage.class
		mDatas.add(data);
		data = new LifeItemData(
				getResources().getString(R.string.life_hot_district),//life_they_talking
				R.drawable.life_hot_district,//life_they_talking
				DistrictPage.class);
		mDatas.add(data);
		data = new LifeItemData(
				getResources().getString(R.string.life_suggest_share),
				R.drawable.life_suggest_share,
				ShareClassifyPage.class);
		mDatas.add(data);
	}

	private void initViews() {
		mSearchEdit 			= (EditText) findViewById(R.id.search_keyword);
		mSearchButton 			= (Button) findViewById(R.id.search);
		mSearchTypeRadioGroup 	= (RelativeRadioGroup) findViewById(R.id.radio_group);
		mListView 				= (ListView) findViewById(R.id.list);
		mSearchResultAdapter = new LifeSearchResultAdapter<LifeItemData>(FindLifeMainPage.this, R.layout.life_search_user, R.id.name, mDatas, mOnClickListener);
		mListView.setAdapter(mSearchResultAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		mSearchButton.setOnClickListener(mOnClickListener);
		mSearchTypeRadioGroup.setOnCheckedChangeListener(this);
		mSearchTypeRadioGroup.check(R.id.radio_find_people);
		//radioType = RADIO_TYPE_PEOPLE;
		mSearchEdit.addTextChangedListener(mSearchTextWatcher);
	}
	TextWatcher	mSearchTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if(s != null){
				String keyWord = s.toString();
				if("".equals(keyWord)){
					mSearchResultAdapter = new LifeSearchResultAdapter<LifeItemData>(FindLifeMainPage.this, R.layout.life_search_user, R.id.name, mDatas, mOnClickListener);
					mListView.setAdapter(mSearchResultAdapter);
					mSearchResultAdapter.notifyDataSetChanged();
					return;
				}
			}
		}
	};
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object itemObj = view.getTag();
			startPage(itemObj);
		}
	};

	private void startPage(Object itemObj) {
		Intent intent = null;
		if(itemObj instanceof UserInfo){
			intent = new Intent(this, PersonnalInforPage.class);
			UserInfo user = (UserInfo)itemObj;
	    	intent.putExtra(PersonnalInforPage.KEY_USER_ID, user.userId);
	    	intent.putExtra(PersonnalInforPage.KEY_USER_NAME, user.userName);
		} else if(itemObj instanceof TopicData){
			intent = new Intent(this, TopicFeedPage.class);
			TopicData topic = (TopicData)itemObj;
//	    	intent.putExtra(TopicFeedPage.TOPIC_ID, topic.sUid);
	    	intent.putExtra(TopicFeedPage.KEYWORD, topic.name);
//	    	intent.putExtra(TopicFeedPage.TOPIC_STATUS, topic.isSubscribe);
		}else if (itemObj instanceof LifeItemData){
			LifeItemData lifeItem = (LifeItemData)itemObj;
			Class<?> target = lifeItem.getTurnTo();
			intent = target != null ? new Intent(this, target) : null;
			String targetClassName = intent.getComponent().getShortClassName();
			if(targetClassName != null){
				if(targetClassName.contains(CommonFeedPage.class.getSimpleName())){
					intent.putExtra(LoadFeedAction.REQUEST_TYPE, LoadFeedAction.RequestType.SHARE_CLASSIFY);
					intent.putExtra(CommonFeedPage.TITLE, lifeItem.getTitle());
				}else if(targetClassName.contains(TopicSubPage.class.getSimpleName())){
					intent.putExtra(TopicSubPage.INTENT_FROM_KEY, TopicSubPage.INTENT_FROM_FEED);
				}
//				else if(targetClassName.contains(SuperPeoplePage.class.getSimpleName())){
//					intent.putExtra(SuperPeoplePage.INTENT_FROM_KEY, SuperPeoplePage.INTENT_FROM_FEED);
//				}
			}
		}
		if(intent != null){
			startActivity(intent);
		}
	}


	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.search:
				String searchKey = mSearchEdit.getText().toString();
				if (InputUtil.isEmpty(searchKey)) {
					showToast(R.string.life_input_null);
					return;
				}
				switch (radioType) {
					case RADIO_TYPE_PEOPLE:
						userSearchResult.clear();
						mSearchResultAdapter = new LifeSearchResultAdapter<UserInfo>(FindLifeMainPage.this, R.layout.life_search_user, R.id.name, userSearchResult, this);
						break;
					case RADIO_TYPE_SHOP:
						districtSearchResult.clear();
						mSearchResultAdapter = new LifeSearchResultAdapter<DistrictData>(FindLifeMainPage.this, R.layout.life_search_user, R.id.name, districtSearchResult, this);
						break;
					case RADIO_TYPE_TOPIC:
						topicSearchResult.clear();
						mSearchResultAdapter = new LifeSearchResultAdapter<TopicData>(FindLifeMainPage.this, R.layout.life_search_user, R.id.name, topicSearchResult, this);
						break;
				}
				mListView.setAdapter(mSearchResultAdapter);
//				Intent intent = new Intent(FindLifeMainPage.this, SearchPage.class);
//				intent.putExtra(SEARCH_KEY, searchKey);
//				intent.putExtra(SEARCH_TYPE, radioType);
//				startActivity(intent);
				Map<String,Object>	params = new HashMap<String, Object>();
		    	params.put(RequestSearchAction.KEY_SEARCH_KEY, searchKey);
				params.put(RequestSearchAction.KEY_SEARCH_TYPE, radioType);
		    	ActionController.post(
		    			FindLifeMainPage.this,
		    			RequestSearchAction.class,
		    			params,
		    			new RequestSearchAction.ISearchResultListener() {
							@Override
							public void onFinish(List list) {
								//hiddenProgressView();
								//updateViews(list);
								//mDatas.clear();
								if(radioType ==RADIO_TYPE_PEOPLE){
									userSearchResult.clear();
									if(list!=null){
										userSearchResult.addAll(list);
									}
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											mSearchResultAdapter.notifyDataSetChanged();
										}
									});	
								}else if(radioType ==RADIO_TYPE_TOPIC){
									topicSearchResult.clear();
									if(list!=null){
										topicSearchResult.addAll(list);
									}
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											mSearchResultAdapter.notifyDataSetChanged();
										}
									});	
								
								}

							}
							@Override
							public void onStart() {
								//showProgressView();
							}

							public void onFail(int resourceID) {
								//hiddenProgressView();
								showToast(resourceID);
							}
		    			},
		    			true);
				
				break;
			case R.id.button:
				switch (radioType) {
					case RADIO_TYPE_PEOPLE:
						break;
					case RADIO_TYPE_SHOP:
						break;
					case RADIO_TYPE_TOPIC:
						break;
				}
				break;
			}
		}
	};

	private void startPeoplePage(SuperPeopleData data) {
    	Intent intent = new Intent(this, PersonnalInforPage.class);
    	intent.putExtra(PersonnalInforPage.KEY_USER_ID, data.getUserId());
    	intent.putExtra(PersonnalInforPage.KEY_USER_NAME, data.getUserName());
    	startActivity(intent);
    }

//	private class MyListAdpter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			return mDatas.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return mDatas.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			LifeItemData data = mDatas.get(position);
//			LayoutInflater inflater = LayoutInflater.from(FindLifeMainPage.this);
//			View view = inflater.inflate(R.layout.life_main_list_item, null);
//			ImageView image = (ImageView) view.findViewById(R.id.head_image);
//			image.setImageResource(data.getResId());
//			TextView text = (TextView) view.findViewById(R.id.name);
//			text.setText(data.getTitle());
//			return view;
//		}
//	}

	@Override
	public void onCheckedChanged(RelativeRadioGroup informationRadioGroup,
			int checkedId) {
		switch (checkedId) {
		case R.id.radio_find_people:
			radioType = RADIO_TYPE_PEOPLE;
			break;
		// case R.id.radio_find_shop:
		// radioType = RADIO_TYPE_SHOP;
		// break;
		case R.id.radio_find_topic:
			radioType = RADIO_TYPE_TOPIC;
			break;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		String keyword = mSearchEdit.getText().toString();
		if(keyword != null && !"".equals(keyword)){
			mSearchEdit.setText("");
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
