package com.youa.mobile.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.youa.mobile.LehoApp;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.location.LocationAction.ILocationActionResultListener;
import com.youa.mobile.location.SuggestPlaceAction.ISuggestLocationActionResultListener;
import com.youa.mobile.location.data.LocationData;

public class MapPage extends MapActivity {
	// BasePage info
	protected View mLoadView;
	protected int mIconSuccResId = R.drawable.toast_succ;
	protected int mIconFailResId = R.drawable.toast_fail;
	public final static String SEARCH_KEY = "search_key";
	public final static String SEARCH_ALL = "all";
	public final static String SEARCH_FROM_DB = "search_db";
	public final static String INSERT_FROM_DB = "insert_db";
	public final static String SEARCH_BY_KEYWORD = "word";
	private static MapView mMapView = null;
	private static View mPopView = null;
	OverItemT overItemT = null;
	GeoPoint mPoint = null;
	// datamResultList
	private List<LocationData> mLocChahe = new ArrayList<LocationData>(0);
	private List<LocationData> mResultList = new ArrayList<LocationData>(0);

	private EditText mSearchLocationKeyword;
	private ListView mLocationListView;
	private TextView mTitle;
	private ImageButton mImageButton;
	private Button mSearchButton;
	private ProgressBar mProgressBar;
	private LocationItemAdapter mLocationAdapter;

	public final static String KEY_GEOPOINT = "geopoint", KEY_TITLE = "title",
			KEY_SNIPPET = "snippet", KEY_LAT = "place_x", KEY_LON = "place_y",
			KEY_PID = "pid", KEY_PlACE_NAME = "place_name", KEY_CITY = "city",
			KEY_PlACE_ADDRESS = "place_address", KEY_PlACE_TYPE = "place_type",
			SEARCH_TYPE = "type";
	private int mLatitude;
	private int mLongitude;
	private String mAdress;
	private String mAdressSelector;
	private String mPlid;
	private Drawable mCurMarker = null;
	private Map<String, Object> myLocation = null;
	private Drawable mOtherMarker = null;
	protected Handler mHandler = new Handler();
	private final int SEARCH_REQUEST_CODE = 50;
	private LinearLayout mLinearNull = null;
	private TextView mlistNull = null;
	private String DISTANCE = "2000";
	private String CITY = "北京";
	LocationListener mLocationListener;
	// public long UPDATA_
	private ILocationActionResultListener resultListener = new LocationAction.ILocationActionResultListener() {
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

		@Override
		public void onStart(Integer resourceID) {
			if (null != resourceID)
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mProgressBar.setVisibility(View.VISIBLE);
					}
				});
		}

		@Override
		public void onGetAllFinish(List<LocationData> locList) {
			mLocChahe = locList;
			if (null != mLocChahe && mLocChahe.size() > 0) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mResultList != null) {
							mResultList.clear();
						}
						if (!TextUtils.isEmpty(mAdress)) {
							LocationData locationData = new LocationData();
							locationData.locName = mAdress;
							locationData.addName = "当前所在区域";
							mResultList.add(locationData);
						}
						mResultList.addAll(mLocChahe);
						mLocationAdapter.notifyDataSetChanged();
						mLocationListView.setVisibility(View.VISIBLE);
						refreshMapView();
						mProgressBar.setVisibility(View.GONE);
					}
				});
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						setNullInfo(R.string.get_address_null);
					}
				});
			}
		}

		@Override
		public void onFinish(int resourceID, int position,
				boolean topicSubStatus) {
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BasePage.activityList.add(this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.map_view);
		getDataFromIntent();
		initTile();
		initListViews();
		mLinearNull = (LinearLayout) findViewById(R.id.linear_null);
		mlistNull = (TextView) findViewById(R.id.list_null);
		if (((LehoApp) MapPage.this.getApplication()).mBMapMan == null) {
			((LehoApp) MapPage.this.getApplication()).initMap();
		}
		((LehoApp) MapPage.this.getApplication()).mBMapMan.start();
		super.initMapActivity(((LehoApp) MapPage.this.getApplication()).mBMapMan);
		if (((LehoApp) MapPage.this.getApplication()).isStartLoc) {
			initMapView();
		} else {
			setNullInfo(R.string.get_address_null);
			return;
		}
	}

	private void getDataFromIntent() {
		mAdressSelector = getIntent().getStringExtra(KEY_PlACE_NAME);
		mPlid = getIntent().getStringExtra(KEY_PID);
	}

	private void setNullInfo(int id) {
		mLinearNull.setVisibility(View.VISIBLE);
		mlistNull.setVisibility(View.VISIBLE);
		mlistNull.setText(id);
	}

	@Override
	protected void onPause() {
		if (((LehoApp) MapPage.this.getApplication()).mBMapMan != null) {
			((LehoApp) MapPage.this.getApplication()).mBMapMan.stop();
			((LehoApp) MapPage.this.getApplication()).isMapActivityOpen = false;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		loadData();
		if (((LehoApp) MapPage.this.getApplication()).mBMapMan != null) {
			((LehoApp) MapPage.this.getApplication()).isMapActivityOpen = true;
			((LehoApp) MapPage.this.getApplication()).mBMapMan.start();
		}
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	void goBackWithData(LocationData loc) {
		saveData(loc);
		Intent data = new Intent();
		data.putExtra(KEY_PlACE_NAME, loc.locName);
		data.putExtra(KEY_PID, loc.sPid);
		data.putExtra(KEY_LAT, loc.latitude);
		data.putExtra(KEY_LON, loc.longitude);
		setResult(RESULT_OK, data);
		finish();
	}

	private void saveData(LocationData loc) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(KEY_PlACE_NAME, loc.locName);
		params.put(KEY_PID, loc.sPid);
		params.put(KEY_LAT, loc.latitude);
		params.put(KEY_LON, loc.longitude);
		params.put(KEY_PlACE_ADDRESS, loc.addName);
		params.put(KEY_PlACE_TYPE, loc.type);
		params.put(SEARCH_TYPE, INSERT_FROM_DB);
		ActionController.post(this, SuggestPlaceAction.class, params,
				new ISuggestLocationActionResultListener() {
					@Override
					public void onFail(int resourceID) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFinish(List<LocationData> locList) {
						// TODO Auto-generated method stub

					}
				}, true);
	}

	private void loadData() {
		if (!((LehoApp) MapPage.this.getApplication()).isStartLoc) {
			return;
		}
		SharedPreferences sp = this.getSharedPreferences(
				SystemConfig.XML_FILE_LOCATION_GUIDE,
				Context.MODE_WORLD_READABLE);
		Map<String, Object> params = new HashMap<String, Object>();
		upDataXY();
		params.put(MapPage.KEY_LAT, mLongitude);
		params.put(MapPage.KEY_LON, mLatitude);
		params.put("distance", DISTANCE);
		params.put(KEY_CITY, sp.getString(SystemConfig.KEY_CITY_NAME, CITY));
		params.put(SEARCH_TYPE, SEARCH_ALL);
		ActionController.post(this, LocationAction.class, params,
				resultListener, true);
	}

	private void loadSuggestData() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SEARCH_TYPE, SEARCH_FROM_DB);
		ActionController.post(this, LocationAction.class, params,
				resultListener, true);
	}

	private void refreshMapView() {
		if (!((LehoApp) MapPage.this.getApplication()).isStartLoc) {
			return;
		}
		List<Map> overItemList = null;
		Map<String, Object> map = null;
		if (myLocation == null) {
			myLocation = new HashMap<String, Object>();
		}
		myLocation.put(KEY_GEOPOINT, new GeoPoint(mLatitude, mLongitude));
		mMapView.getController().animateTo(new GeoPoint(mLatitude, mLongitude));
		if (mLocChahe != null && mLocChahe.size() > 0) {
			overItemList = new ArrayList<Map>();
			for (LocationData loc : mLocChahe) {
				map = new HashMap<String, Object>();
				map.put(KEY_GEOPOINT, new GeoPoint(loc.longitude, loc.latitude));
				map.put(KEY_SNIPPET, loc.locName);
				map.put(KEY_TITLE, loc.sPid);
				overItemList.add(map);
			}
			if(mMapView.getOverlays()!=null){
				mMapView.getOverlays().clear();
			}
			mMapView.getOverlays().add(
					new OverItemT(mCurMarker, this, myLocation));
			mMapView.getOverlays().add(
					new OverItemT(mOtherMarker, this, overItemList));
			mMapView.invalidate();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& findViewById(R.id.map_panle).getVisibility() != View.VISIBLE
				&& ((LehoApp) MapPage.this.getApplication()).isStartLoc) {
			goBack();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void goBack() {
		findViewById(R.id.map_panle).setVisibility(View.VISIBLE);
		mSearchLocationKeyword.setText("");
		hideSoftInput();
		// updateLocListView();
		this.loadData();
	}

	private void initListViews() {
		mLocationListView = (ListView) findViewById(R.id.location_list);
		mLocationAdapter = new LocationItemAdapter(MapPage.this, mResultList,
				mAdressSelector, mPlid);
		mLocationListView.setAdapter(mLocationAdapter);
		mLocationListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goBackWithData(mResultList.get(position));
			}
		});

	}

	private void initTile() {
		mSearchLocationKeyword = (EditText) findViewById(R.id.topic_keyword);
		mTitle = (TextView) findViewById(R.id.title);
		mImageButton = (ImageButton) findViewById(R.id.back);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mTitle.setText(R.string.address_title);
		mSearchButton = (Button) findViewById(R.id.location_search);
		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchKey = mSearchLocationKeyword.getText().toString();
				if (InputUtil.isEmpty(searchKey)) {
					return;
				}
				Intent intent = new Intent(MapPage.this,
						SearchLocationPage.class);
				intent.putExtra(SEARCH_KEY, searchKey);
				startActivityForResult(intent, SEARCH_REQUEST_CODE);

			}
		});
		mImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mSearchLocationKeyword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.map_panle).setVisibility(View.GONE);
				loadSuggestData();
				// mLocationListView.setVisibility(View.VISIBLE);
				// mLinearNull.setVisibility(View.GONE);
				// mlistNull.setVisibility(View.GONE);
			}
		});
		mSearchLocationKeyword.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable keyWord) {
			}

			@Override
			public void beforeTextChanged(CharSequence text, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence keyword, int arg1, int arg2,
					int arg3) {
				updateView(keyword.toString());
			}
		});
	}

	private void hideSoftInput() {
		InputMethodManager im = (InputMethodManager) mSearchLocationKeyword
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(MapPage.this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void updateView(final String keyword) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null != mResultList) {
					mResultList.clear();
				}
				// 根据当前输入的关键字，让listView显示相关的item。下文中的mTopicChahe为加载的所有热门话题。
				if (!"".equals(keyword) && !"".equals(keyword.trim())) {
					// 首先将输入的关键字作为一个话题添加到adapter的data中。
					LocationData info = new LocationData();
					info.locName = keyword.trim();
					mResultList.add(info);
					// 根据关键字从话题缓存mTopicChahe中检索与输入关键字相关的话题，并添加到adapter的data中。
					String locName;
					if (null != mLocChahe && mLocChahe.size() > 0) {
						for (LocationData loc : mLocChahe) {
							locName = loc.locName;
							if (!TextUtils.isEmpty(locName)
									&& locName.contains(keyword)
									&& !locName.equals(keyword)) {
								mResultList.add(loc);
							}
						}
					}
				} else if (mLocChahe != null) {
					mResultList.addAll(mLocChahe);
				}
				mLocationAdapter.notifyDataSetChanged();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mLocationListView.setSelection(0);
					}
				});
			}
		});
	}

	private void initMapView() {
		findViewById(R.id.map_panle).setVisibility(View.VISIBLE);
		mMapView = (MapView) findViewById(R.id.bmapView);
		// 设定地图的缩放条
		mMapView.setBuiltInZoomControls(false);
		mMapView.setDrawOverlayWhenZooming(true);
		mMapView.getController().setZoom(15);
		// 添加ItemizedOverlay
		mCurMarker = getResources().getDrawable(R.drawable.cur_marka); // 得到需要标在地图上的资源
		mCurMarker.setBounds(0, 0, mCurMarker.getIntrinsicWidth(),
				mCurMarker.getIntrinsicHeight()); // 为maker定义位置和边界
		mOtherMarker = getResources().getDrawable(R.drawable.oth_marka);
		mOtherMarker.setBounds(0, 0, mOtherMarker.getIntrinsicWidth(),
				mOtherMarker.getIntrinsicHeight()); // 为maker定义位置和边界
		if (myLocation == null) {
			myLocation = new HashMap<String, Object>();
		}

		upDataXY();
		if (mLatitude != 0 && mLongitude != 0) {
			myLocation.put(KEY_GEOPOINT, new GeoPoint(mLatitude, mLongitude));
			mMapView.getOverlays().add(
					new OverItemT(mCurMarker, this, myLocation)); // 添加ItemizedOverlay实例到mMapView

			mMapView.getController().animateTo(
					new GeoPoint(mLatitude, mLongitude));
		}
		// 创建点击mark时的弹出泡泡
		mPopView = super.getLayoutInflater().inflate(R.layout.popview, null);
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);
	}

	private void upDataXY() {
		SharedPreferences sp = getSharedPreferences(
				SystemConfig.XML_FILE_LOCATION_GUIDE,
				Context.MODE_WORLD_READABLE);
		if (!TextUtils.isEmpty(sp.getString(SystemConfig.KEY_PLACE_X, ""))
				&& !TextUtils.isEmpty(sp
						.getString(SystemConfig.KEY_PLACE_Y, ""))) {
			mLatitude = Integer.parseInt(sp.getString(SystemConfig.KEY_PLACE_Y,
					""));
			mLongitude = Integer.parseInt(sp.getString(
					SystemConfig.KEY_PLACE_X, ""));
			mAdress = sp.getString(SystemConfig.KEY_LOCATION_NAME, "");
		}
	}

	protected void showToast(final Context context, final int resourceID) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context.getApplicationContext(),
						getString(resourceID), Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void showToastWithIcon(final int resourceID, final int iconId) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(getApplicationContext(),
						getString(resourceID), Toast.LENGTH_SHORT);
				LinearLayout toastView = (LinearLayout) toast.getView();
				toastView.setGravity(Gravity.CENTER);
				toastView.setOrientation(LinearLayout.HORIZONTAL);
				ImageView iconView = new ImageView(getApplicationContext());
				iconView.setImageResource(iconId);
				toastView.addView(iconView, 0);
				toast.show();
			}
		});
	}

	protected void showToast(final int resourceID) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), getString(resourceID),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected ProgressDialog mProgressDialog;

	protected void showProgressDialog(final Context context, final int title,
			final int tip) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressDialog = ProgressDialog.show(context, getResources()
						.getString(title), getResources().getString(tip), true,
						true);
			}
		});
	}

	protected void hideProgressDialog() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null != mProgressDialog && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		});
	}

	protected boolean checkInput(final TextView textView, int lblStringRes) {
		String text = textView.getText().toString();
		if (text == null || "".equals(text)) {
			showInputPrompt(lblStringRes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							textView.requestFocus();
						}
					});
			return false;
		}
		return true;
	}

	protected boolean checkInput(final RadioGroup radioGroup, int lblStringRes) {
		int checkId = radioGroup.getCheckedRadioButtonId();
		if (checkId == -1) {
			showInputPrompt(lblStringRes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							radioGroup.getChildAt(0).requestFocus();
						}
					});
			return false;
		}
		return true;
	}

	private void showInputPrompt(final int lblStringRes,
			DialogInterface.OnClickListener onClickListener) {
		String paramtxt = getResources().getString(lblStringRes);
		String showText = getResources().getString(
				R.string.common_error_input_null, paramtxt);
		new AlertDialog.Builder(this).setTitle(R.string.common_error_lbl)
				.setMessage(showText)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton(R.string.common_ok, onClickListener

				).show();
	}

	protected boolean checkLength(final TextView textView, int lblStringRes,
			int maxlength) {
		String text = textView.getText().toString();
		if (text != null && text.length() >= maxlength) {
			showInputPrompt(lblStringRes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							textView.requestFocus();
						}
					});
			return false;
		}
		return true;
	}

	// 显示周围的商铺
	class OverItemT extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Drawable marker;
		private Context mContext;
		private String mAddressName = null;
		private String mPid = null;
		private int mSelLatitude;
		private int mSelLongitude;

		public OverItemT(Drawable marker, Context context, List<Map> geoList) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			this.mContext = context;
			for (int i = 0; i < geoList.size(); i++) {
				mGeoList.add(new OverlayItem((GeoPoint) (geoList.get(i)
						.get(MapPage.KEY_GEOPOINT)), (String) geoList.get(i)
						.get(MapPage.KEY_TITLE), (String) geoList.get(i).get(
						MapPage.KEY_SNIPPET)));
			}
			populate();
		}

		public OverItemT(Drawable marker, Context context,
				Map<String, Object> map) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			this.mContext = context;
			mGeoList.add(new OverlayItem((GeoPoint) (map
					.get(MapPage.KEY_GEOPOINT)), (String) map
					.get(MapPage.KEY_TITLE), (String) map
					.get(MapPage.KEY_SNIPPET)));
			populate();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mGeoList.size();
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			setFocus(mGeoList.get(i));
			// 更新气泡位置,并使之显示
			GeoPoint pt = mGeoList.get(i).getPoint();
			mAddressName = mGeoList.get(i).getSnippet();
			mSelLatitude = mGeoList.get(i).getPoint().getLatitudeE6();
			mSelLongitude = mGeoList.get(i).getPoint().getLongitudeE6();
			mPid = mGeoList.get(i).getTitle();
			String str = mGeoList.get(i).getSnippet();
			MapPage.mPopView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (!TextUtils.isEmpty(mAddressName)) {
						LocationData loc = new LocationData();
						loc.latitude = mSelLatitude;
						loc.longitude = mSelLongitude;
						loc.locName = mAddressName;
						loc.sPid = mPid;
						MapPage.this.goBackWithData(loc);
					}
				}
			});
			if (!TextUtils.isEmpty(str)) {
				TextView tx = (TextView) (MapPage.mPopView
						.findViewById(R.id.address));
				tx.setText(str);
				// MapPage.mPopView.setPadding(0, 0, 0,
				// marker.getIntrinsicHeight() / 2);
				MapPage.mMapView.updateViewLayout(MapPage.mPopView,
						new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, pt,
								MapView.LayoutParams.BOTTOM_CENTER));
				MapPage.mPopView.setVisibility(View.VISIBLE);
			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint point, MapView map) {
			// TODO Auto-generated method stub
			// 消去弹出的气泡
			MapPage.mPopView.setVisibility(View.GONE);
			return super.onTap(point, map);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SEARCH_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				LocationData locationData = new LocationData();
				locationData.locName = data.getStringExtra(KEY_PlACE_NAME);
				locationData.latitude = data.getIntExtra(KEY_LAT, 0);
				locationData.longitude = data.getIntExtra(KEY_LON, 0);
				locationData.sPid = data.getStringExtra(KEY_PID);
				saveData(locationData);
				setResult(RESULT_OK, data);
				finish();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
