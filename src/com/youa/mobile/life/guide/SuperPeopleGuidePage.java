package com.youa.mobile.life.guide;

import java.util.ArrayList;
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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.PreparePage;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.information.action.AddCancelAttentAction;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.action.RequestFindSuperPeopleAction;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.news.util.NewsUtil;

public class SuperPeopleGuidePage extends BasePage {

	private final static String TAG = "SuperPeopleGuidePage";
	public final static String SEARCH_KEY = "search_key";
	public final static String SEARCH_TYPE = "search_type";
	public final static int RADIO_TYPE_PEOPLE = 1;
	private static final int REQUEST_SHOW_PEOPLE = 100;
	private static int mAttPosition = 0;
	public static final String UPDATE_SUP_STATUS = "topic_status";
	private ListView 	mPeopleListView;
	private TextView 	mNullText;

	private MyListAdapter mPeopleListAdaper;
	private List<SuperPeopleData> mDataList = new ArrayList<SuperPeopleData>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_super_people);
		initViews();
		loadDate();
	}

	private void loadDate() {

		ActionController.post(
				this,
				RequestFindSuperPeopleAction.class,
				null,
				new RequestFindSuperPeopleAction.ISearchSuperPeopleResultListener() {

					@Override
					public void onFinish(List<SuperPeopleData> list) {
						hiddenProgressView();
						updateViews(list);
					}

					@Override
					public void onFail(int resourceID) {
						hiddenProgressView();
						showToast(SuperPeopleGuidePage.this, resourceID);
					}

					@Override
					public void onStart() {
						showProgressView();
					}
				},
				true);
	}

	private void updateViews(final List<SuperPeopleData> list){
		if (!NewsUtil.isEmpty(list)) {
			NewsUtil.LOGD(TAG, " enter onFinish() data <list.size> : " + list.size());
			mHandler.post(new Runnable() {
				public void run() {
					mNullText.setVisibility(View.GONE);
					mDataList.clear();
					mDataList.addAll(list);
					mPeopleListAdaper.notifyDataSetChanged();									
				}
			});
		} else {
			mHandler.post(new Runnable() {
				public void run() {
					mNullText.setVisibility(View.VISIBLE);
				}
			});
		}
    }

	private void initViews() {
		findViewById(R.id.search_bar_layout).setVisibility(View.GONE);
		findViewById(R.id.title).setVisibility(View.GONE);
		mNullText 		= (TextView) findViewById(R.id.text_null);
		mPeopleListView = (ListView) findViewById(R.id.list);
		mProcessView 	= (ProgressBar) findViewById(R.id.progressBar);
		mPeopleListAdaper = new MyListAdapter(this);
		mPeopleListView.setAdapter(mPeopleListAdaper);
//		mPeopleListView.setOnItemClickListener(onItemClickListener);
	}

//	OnItemClickListener onItemClickListener = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			/*if(getIntent().getBooleanExtra(TopicSubGuidePage.KEY_ITEM_CAN_CLICK, true)){
//				SuperPeopleData data = mDataList.get(position);
//				mAttPosition=position;
//				startPeoplePage(data);
//			}*/
//		}
//    	
//    };

//    private void startPeoplePage(SuperPeopleData data) {
//    	Intent intent = new Intent(this, WXShareMainPage.class);
//    	intent.putExtra(WXShareMainPage.KEY_USER_ID, data.getUserId());
//    	intent.putExtra(WXShareMainPage.KEY_USER_NAME, data.getUserName());
//    	intent.putExtra(UPDATE_SUP_STATUS, data.isPayAttentionTo());	
//		if(getParent() == null){
//			startActivityForResult(intent, REQUEST_SHOW_PEOPLE);
//		} else {
//			getParent().startActivityForResult(intent, REQUEST_SHOW_PEOPLE);
//		}
//    }

	private class MyListAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;

		private final class ViewHolder {
			public ImageView headImage;
			public TextView text;
			public Button button;
			public TextView signature;
			
		}
		public MyListAdapter(Context context) {
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

		public void updateView(SuperPeopleData data, boolean isGuanzhu) {
			data.setPayAttentionTo(isGuanzhu);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					notifyDataSetChanged();
				}
			});
			
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.life_super_people_list_item, null);
				holder.headImage = (ImageView) convertView.findViewById(R.id.head_image);
				holder.text = (TextView) convertView.findViewById(R.id.name);
				holder.button = (Button) convertView.findViewById(R.id.pay_attention_to);
				holder.signature = (Button) convertView.findViewById(R.id.signature);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final SuperPeopleData data = mDataList.get(position);
			String username = data.getUserName();
			String imgId = data.getUserImage();
			holder.text.setText(username);
			if(!TextUtils.isEmpty(data.getSignature())){
				holder.signature.setText(data.getSignature());
			}
			if (!InputUtil.isEmpty(imgId)) {
				ImageUtil.setImageView(
						context,
						holder.headImage, 
						imgId,
						ImageUtil.HEADER_SIZE_BIG,
						ImageUtil.HEADER_SIZE_BIG,
						R.drawable.head_men);
			} else {
				holder.headImage.setImageResource(("1".equals(data.getSex())) ? R.drawable.head_men : R.drawable.head_women);
			}
			final boolean hasGuanZhu = data.isPayAttentionTo();
			if (hasGuanZhu) {
				holder.button.setBackgroundResource(R.drawable.topic_unsub_btn_bg);
				holder.button.setText(R.string.life_guanzhu_not);
			} else {
				holder.button.setBackgroundResource(R.drawable.life_guanzhu_bg);
				holder.button.setText(R.string.life_guanzhu);
			}
			final String operateType = hasGuanZhu ? AddCancelAttentAction.TYPE_CANCEL : AddCancelAttentAction.TYPE_ADD;
			holder.button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(AddCancelAttentAction.KEY_FOLLOW_UID, data.getUserId());
					paramMap.put(AddCancelAttentAction.KEY_OPERATE_TYPE, operateType);
					if (!hasGuanZhu) {
						paramMap.put(AddCancelAttentAction.KEY_ADD_IMAGEID, data.getUserImage());
						paramMap.put(AddCancelAttentAction.KEY_ADD_SEXINT, data.getSex());
						paramMap.put(AddCancelAttentAction.KEY_ADD_UNAME, data.getUserName());
					}
					ActionController.post(
							 SuperPeopleGuidePage.this, 
							 AddCancelAttentAction.class, 
							 paramMap, 
							 new AddCancelAttentAction.IOperateResult(){
								@Override
								public void onEnd(final boolean flag) {
									hiddenProgressView();
									updateView(data, !hasGuanZhu);
									showToast(hasGuanZhu ? R.string.life_guanzhu_not_succ : R.string.life_guanzhu_succ);
									sendBroadcast(new Intent(LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE));
								}

								@Override
								public void onStart() {
									showProgressView();
								}

								@Override
								public void onFail(int resourceID) {
									hiddenProgressView();
									showToast(resourceID);
								}
							 },
				    	true);
				}
				
			});
			return convertView;
		}
		
	}
//	public  void onActivityResult(int requestCode, int resultCode, final Intent data) {
//		 super.onActivityResult(requestCode, resultCode, data);
//				if(resultCode == RESULT_OK) {
//					mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							SuperPeopleData peopledData = mDataList.get(mAttPosition);
//							boolean mTopicStatus = data.getBooleanExtra(UPDATE_SUP_STATUS, peopledData.isPayAttentionTo());
//							mPeopleListAdaper.updateView(peopledData, mTopicStatus);
//						}
//					});
//				}
//	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			goHome();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	public void goHome() {
		Intent intent = new Intent();
		intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, PreparePage.class);
		startActivity(intent);
	}
}
