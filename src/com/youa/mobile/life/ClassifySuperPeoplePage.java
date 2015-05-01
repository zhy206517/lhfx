package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.information.action.AddCancelAttentAction;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.action.RequestFindSuperPeopleAction;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.news.util.NewsUtil;

public class ClassifySuperPeoplePage extends BasePage {

	private final static String TAG = "ClassifySuperPeoplePage";

	public final static String TITLE = "title";

	public final static int RADIO_TYPE_PEOPLE = 1;
	private static final int REQUEST_SHOW_PEOPLE = 100;
	private static int mAttPosition = 0;
	public static final String UPDATE_SUP_STATUS = "topic_status";
	private ImageView mBackButton;
	private ListView mPeopleListView;
	private TextView mNullText;
	private TextView mTitleText;
	private String mTitle;
	private MyListAdapter mPeopleListAdaper;
	private List<SuperPeopleData> mDataList = new ArrayList<SuperPeopleData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_super_people);
		initViews();
		loadDate();
	}

	// ('hot_recommend', // 热门推荐
	// 'share_cate', // 乐享美食
	// 'play_group', // 玩乐帮派
	// 'city_beauty', // 都市丽人
	// 'mother_baby', // 辣妈萌宝
	// 'roman_mary', // 浪漫婚恋
	// 'happy_house' // 幸福居家
	// );
	public String getSuperKey(String Value) {
		String key = Value;
		if ("hot_recommend".equals(Value)) {
			key = "热门推荐";
		} else if ("share_cate".equals(Value)) {
			key = "乐享美食";
		} else if ("play_group".equals(Value)) {
			key = "玩乐帮派";
		} else if ("city_beauty".equals(Value)) {
			key = "都市丽人";
		} else if ("mother_baby".equals(Value)) {
			key = "辣妈萌宝";
		} else if ("roman_mary".equals(Value)) {
			key = "浪漫婚恋";
		} else if ("happy_house".equals(Value)) {
			key = "幸福居家";
		}
		return key;
	}

	private void loadDate() {
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("key",mTitle);
		ActionController
				.post(this,
						RequestFindSuperPeopleAction.class,
						para,
						new RequestFindSuperPeopleAction.ISearchSuperPeopleResultListener() {

							@Override
							public void onFinish(List<SuperPeopleData> list) {
								hiddenProgressView();
								updateViews(list);
							}

							@Override
							public void onFail(int resourceID) {
								hiddenProgressView();
								showToast(ClassifySuperPeoplePage.this,
										resourceID);
							}

							@Override
							public void onStart() {
								showProgressView();
							}
						}, true);
	}

	private void updateViews(final List<SuperPeopleData> list) {
		if (!NewsUtil.isEmpty(list)) {
			NewsUtil.LOGD(TAG,
					" enter onFinish() data <list.size> : " + list.size());
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

	OnClickListener btnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// case R.id.search:
			// String searchKey = mSearchEdit.getText().toString();
			// if (InputUtil.isEmpty(searchKey)) {
			// return;
			// }
			// Intent intent = new Intent(ClassifySuperPeoplePage.this,
			// SearchPage.class);
			// intent.putExtra(SEARCH_KEY, searchKey);
			// intent.putExtra(SEARCH_TYPE, RADIO_TYPE_PEOPLE);
			// startActivity(intent);
			// break;
			case R.id.back:
				finish();
				break;
			}
		}
	};

	private void initViews() {
		findViewById(R.id.next_btn).setVisibility(View.GONE);
		mBackButton = (ImageView) findViewById(R.id.back);
		mBackButton.setOnClickListener(btnOnClickListener);
		mNullText = (TextView) findViewById(R.id.text_null);
		mPeopleListView = (ListView) findViewById(R.id.list);
		mProcessView = (ProgressBar) findViewById(R.id.progressBar);
		mTitleText = (TextView) findViewById(R.id.title_id);
		Intent intent = this.getIntent();
		if (intent != null) {
			mTitle = intent.getStringExtra(ClassifySuperPeoplePage.TITLE);
			if (!TextUtils.isEmpty(mTitle)) {
				mTitleText.setText(getSuperKey(mTitle));
			}
		}
		Handler handle = new Handler();
		mPeopleListAdaper = new MyListAdapter(this);
		mPeopleListView.setAdapter(mPeopleListAdaper);
		mPeopleListView.setOnItemClickListener(onItemClickListener);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SuperPeopleData data = mDataList.get(position);
			mAttPosition = position;
			startPeoplePage(data);
		}

	};

	private void startPeoplePage(SuperPeopleData data) {
		Intent intent = new Intent(this, PersonnalInforPage.class);
		intent.putExtra(PersonnalInforPage.KEY_USER_ID, data.getUserId());
		intent.putExtra(PersonnalInforPage.KEY_USER_NAME, data.getUserName());
		intent.putExtra(UPDATE_SUP_STATUS, data.isPayAttentionTo());
		if (getParent() == null) {
			startActivityForResult(intent, REQUEST_SHOW_PEOPLE);
		} else {
			getParent().startActivityForResult(intent, REQUEST_SHOW_PEOPLE);
		}
	}

	private class MyListAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;

		private final class ViewHolder {
			public ImageView headImage;
			public TextView text;
			public ImageButton button;
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
				convertView = inflater.inflate(
						R.layout.life_super_people_list_item, null);
				holder.headImage = (ImageView) convertView
						.findViewById(R.id.head_image);
				holder.text = (TextView) convertView.findViewById(R.id.name);
				holder.button = (ImageButton) convertView
						.findViewById(R.id.pay_attention_to);
				holder.signature = (TextView) convertView
						.findViewById(R.id.signature);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final SuperPeopleData data = mDataList.get(position);
			String username = data.getUserName();
			String imgId = data.getUserImage();
			holder.text.setText(username);
			holder.signature.setText(getSuperKey(mTitle));
			if (!TextUtils.isEmpty(data.getSignature())) {
				holder.signature.setText(data.getSignature());
			}
			if (!InputUtil.isEmpty(imgId)) {
				ImageUtil.setHeaderImageView(context, holder.headImage, imgId,
						R.drawable.head_men);
			} else {
				holder.headImage
						.setImageResource(("1".equals(data.getSex())) ? R.drawable.head_men
								: R.drawable.head_women);
			}
			final boolean hasGuanZhu = data.isPayAttentionTo();
			holder.button.setFocusable(false);
			if (hasGuanZhu) {
				holder.button.setBackgroundResource(R.drawable.bt_on_switch_bg_seletor);
				holder.button.setImageResource(R.drawable.image_on_switch);
			} else {
				holder.button.setBackgroundResource(R.drawable.bt_off_switch_bg_seletor);
				holder.button.setImageResource(R.drawable.image_off_switch);
			}
			final String operateType = hasGuanZhu ? AddCancelAttentAction.TYPE_CANCEL
					: AddCancelAttentAction.TYPE_ADD;
			holder.button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(AddCancelAttentAction.KEY_FOLLOW_UID,
							data.getUserId());
					paramMap.put(AddCancelAttentAction.KEY_OPERATE_TYPE,
							operateType);
					if (!hasGuanZhu) {
						paramMap.put(AddCancelAttentAction.KEY_ADD_IMAGEID,
								data.getUserImage());
						paramMap.put(AddCancelAttentAction.KEY_ADD_SEXINT,
								data.getSex());
						paramMap.put(AddCancelAttentAction.KEY_ADD_UNAME,
								data.getUserName());
					}
					ActionController.post(ClassifySuperPeoplePage.this,
							AddCancelAttentAction.class, paramMap,
							new AddCancelAttentAction.IOperateResult() {
								@Override
								public void onEnd(final boolean flag) {
									hiddenProgressView();
									updateView(data, !hasGuanZhu);
									showToast(hasGuanZhu ? R.string.life_guanzhu_not_succ
											: R.string.life_guanzhu_succ);
									sendBroadcast(new Intent(
											LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE));
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
							}, true);
				}

			});
			return convertView;
		}

	}

	public void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if(mDataList!=null&&mDataList.size()>mAttPosition){
						SuperPeopleData peopledData = mDataList.get(mAttPosition);
						boolean mTopicStatus = data.getBooleanExtra(
								UPDATE_SUP_STATUS, peopledData.isPayAttentionTo());
						mPeopleListAdaper.updateView(peopledData, mTopicStatus);
					}
				}
			});
		}
	}

}
