package com.youa.mobile.circum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youa.mobile.LehoApp;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.circum.action.CircumAction;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.common.base.AbstractListView.OnScrollEndListener;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.input.PublishPage;
import com.youa.mobile.jingxuan.TagClassFeedPage;
import com.youa.mobile.location.MapPage;
import com.youa.mobile.login.ThirdLoginWebPage;

public class CircumFeedActivity extends BasePage implements OnScrollEndListener {
	public int REFRESH = 1;
	public int NEXT_PAGE = 2;
	public final static int REQUEST_COUNT = 50;
	private CircumListView homeList;
	private ListView homeListView;
	private View footer;
	private LinearLayout header;
	private LayoutInflater mInflater;
	private Handler mHandler = new Handler();
	private ProgressBar homeProgressBar;
	// private LinearLayout linearNull;
	// private TextView listNull;
	String mHomeDeleteId = null;
	private ImageButton title_refresh;
	private ImageButton title_write;
	private boolean isRefreshing;
	private boolean isCoorNull = true;
	final public static int request_delete_code = 20, result_delete_code = 50;
	private String placeX, placeY;
	private int start_pos=0;
	LinearLayout mEmptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_home);
		initView();
		if (isCoorNull) {
			new Thread() {
				public void run() {
					refreshCoor();
				}
			}.start();
		}
		registerReceiver();
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if(((LehoApp)CircumFeedActivity.this.getApplication()).mBMapMan!=null){
					((LehoApp)CircumFeedActivity.this.getApplication()).isMapActivityOpen=true;
					((LehoApp)CircumFeedActivity.this.getApplication()).mBMapMan.start();
				}
			}
		});
		// HintPageUtil.checkHint(HintPageUtil.HINT_HOME, this, mHandler);
	}

	
	public void refreshCoor() {
		while (true) {
			if (TextUtils.isEmpty(placeX) || TextUtils.isEmpty(placeY)
					|| "0".equals(placeX) || "0".equals(placeY)) {
				if (homeProgressBar.getVisibility() == View.GONE) {
					homeProgressBar.setVisibility(View.VISIBLE);
				}
				getCoordinates();
			} else {
				isCoorNull = false;
				break;
			}

		}
		loadCircumData(placeX, placeY, 0);
	}

	@Override
	protected void onResume() {
		
//		((LehoApp) CircumFeedActivity.this.getApplication()).requestLocation();
		super.onResume();
//		if(homeList!=null){
////			homeList.locationName = null;
//			homeList.getAdapter().notifyDataSetChanged();
//		}
	}

	@Override
	protected void onPause() {
		((LehoApp) CircumFeedActivity.this.getApplication())
				.requestStopLocation();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}

	public void initView() {
		mInflater = LayoutInflater.from(this);
		homeListView = (ListView) findViewById(R.id.list);

		header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		footer = getInflaterLayout(R.layout.feed_footer);
		toTopInit();
		toEndInit();
		homeList = new CircumListView(homeListView, header, footer);
		homeList.setOnScrollEndListener(this);
		homeList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeData data = (HomeData) v.getTag();
				if (data == null || data.PublicUser == null) {
					return;
				}
				Intent intent = new Intent();
				if ("0".equals(data.PublicUser.feedType)) {
					intent.setClass(CircumFeedActivity.this,
							ContentOriginActivity.class);
					// 源动态id
					intent.putExtra(ContentOriginActivity.ORIGIN_FEED_ID,
							data.PublicUser.postId);
				} else {
					if (data.originUser == null) {
						return;
					}
					intent.setClass(CircumFeedActivity.this,
							ContentTranspondActivity.class);
					// 转发动态id
					intent.putExtra(ContentTranspondActivity.TRANSPOND_FEED_ID,
							data.originUser.postId);
				}
				startActivity(intent);
			}
		});
		RelativeLayout title = (RelativeLayout) findViewById(R.id.title);
		((TextView) (findViewById(R.id.title_text)))
				.setText(R.string.feed_circum_title);
		title_refresh = (ImageButton) title.findViewById(R.id.turnpage);
		title_refresh.setVisibility(View.GONE);
		title_write = (ImageButton) title.findViewById(R.id.write);
		title_write.setVisibility(View.GONE);
		title_refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				homeList.refresh();
			}
		});
		mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
		mEmptyView.findViewById(R.id.wxcb_empty_btn).setVisibility(View.GONE);
		homeProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		getCoordinates();

	}

	public void getCoordinates() {
		SharedPreferences sp = this.getSharedPreferences(
				SystemConfig.XML_FILE_LOCATION_GUIDE,
				Context.MODE_WORLD_READABLE);
		placeX = sp.getString(SystemConfig.KEY_PLACE_X, "");
		placeY = sp.getString(SystemConfig.KEY_PLACE_Y, "");
		refreshLocation();
	}

	private void updateCircum(final List<HomeData> homeDataList,final int type) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				homeProgressBar.setVisibility(View.GONE);
				mEmptyView.setVisibility(View.GONE);
				// listNull.setVisibility(View.GONE);
				homeListView.setVisibility(View.VISIBLE);
				homeList.closeHeaderFooter();
				isRefreshing = false;
				if (homeDataList == null || homeDataList.size() < 1) {
					homeListView.setVisibility(View.GONE);
					((TextView) mEmptyView.findViewById(R.id.wxcb_tishi))
							.setText(CircumFeedActivity.this
									.getString(R.string.no_has_circum_warn));
					mEmptyView.setVisibility(View.VISIBLE);
					homeList.setLockEnd(true);
					return;
				}
				if (type == REFRESH) {
					homeList.setData(homeDataList, PageSize.HOME_FEED_MAXSIZE);
				} else {
					homeList.addData(homeDataList, PageSize.HOME_FEED_MAXSIZE);
				}
				homeList.setLockEnd(false);
			}
		});
	}

	private void loadCircumData(String place_x, String place_y, final int type) {
		if (!((LehoApp) this.getApplication()).isStartLoc) {
			homeProgressBar.setVisibility(View.GONE);
			homeListView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.VISIBLE);
			((TextView) mEmptyView.findViewById(R.id.wxcb_tishi))
					.setText(getResources().getString(R.string.lbs_no_setting));
			return;
		}
		if (isRefreshing) {
			return;
		}
		isRefreshing = true;
		if (type == REFRESH && homeList.getData() != null
				&& homeList.getData().size() > 0) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					homeListView.setSelection(0);
				}
			});
		}
		if(type == REFRESH){
			start_pos=0;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(CircumAction.PARAM_PLACE_X, place_x);
		params.put(CircumAction.PARAM_PLACE_Y, place_y);
		params.put(CircumAction.PARAM_START_POS, start_pos);
		ActionController.post(CircumFeedActivity.this, CircumAction.class,
				params, new CircumAction.ISearchResultListener() {
					@Override
					public void onFail(int resourceID) {
						showToast(CircumFeedActivity.this, resourceID);

						mHandler.post(new Runnable() {
							@Override
							public void run() {
								homeProgressBar.setVisibility(View.GONE);
								homeList.closeHeaderFooter();
								isRefreshing = false;
							}
						});
					}

					@Override
					public void onStart() {
						homeProgressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onEnd(List<HomeData> homeDataList) {
						updateCircum(homeDataList,type);
						start_pos++;
					}

				}, true);
	}

	public View getInflaterLayout(int resource) {
		return mInflater.inflate(resource, null);
	}

//	private class HomeItemClickListener implements OnItemClickListener {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			// ----------
//			if (homeList.getHeader() != null) {
//				position -= 1;
//			}
//			List<HomeData> list = homeList.getData();
//			if (position < 0 || position >= list.size()) {
//				return;
//			}
//			HomeData data = list.get(position);
//			Bundle bundle = new Bundle();
//			Class c = null;
//			// --------------------
//			if ("0".equals(data.PublicUser.feedType)) {
//				c = ContentOriginActivity.class;
//				bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
//						data.PublicUser.postId);// 源动态id
//			} else {
//				c = ContentTranspondActivity.class;
//				bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
//						data.PublicUser.postId);// 转发动态id
//			}
//			// --------------------
//			Intent intent = new Intent();
//			intent.putExtras(bundle);
//			intent.setClass(CircumFeedActivity.this, c);
//			startActivityForResult(intent, request_delete_code);
//		}
//	}

	@Override
	public void onScrollEnd() {
		 loadCircumData(placeX, placeY,NEXT_PAGE);
	}

	@Override
	public void onScrollHeader() {
		getCoordinates();
		loadCircumData(placeX, placeY, REFRESH);
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

	final public static int pulbicRequest = 10, publicOk = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case pulbicRequest:
			homeList.refresh();
			break;
		case result_delete_code:
			mHomeDeleteId = data.getStringExtra("postId");
			loadCircumData(placeX, placeY, REFRESH);
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(LehuoIntent.CIRCUM_FEED_UPDATE,
					intent.getAction())) {
				homeList.refresh();
				
			}else if(TextUtils.equals(LehuoIntent.LOCATION_SUCCESS, intent.getAction())){
				refreshLocation();
			}
		}
	};

	private void refreshLocation(){
//		ActivityManager am = (ActivityManager) getApplicationContext()
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//		if (cn.getClassName().equals(
//				CircumFeedActivity.this.getComponentName().getClassName())) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					homeList.locationName = ((LehoApp)CircumFeedActivity.this.getApplication()).locationName;
					homeList.getAdapter().notifyDataSetChanged();
					//homeList.setLocation(((LehoApp)CircumFeedActivity.this.getApplication()).locationName);
					//homeList.getAdapter().notifyDataSetChanged();
				}
			});
//		}
	}
	
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(LehuoIntent.CIRCUM_FEED_UPDATE);
		filter.addAction(LehuoIntent.LOCATION_SUCCESS);
		registerReceiver(mReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}
}
