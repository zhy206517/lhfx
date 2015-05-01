package com.youa.mobile.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MapView.LayoutParams;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.RouteOverlay;
import com.youa.mobile.LehoApp;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.content.ContentActivity;
import com.youa.mobile.location.data.LocationData;
import com.youa.mobile.location.util.LocationUtil;
import com.youa.mobile.location.util.LocationUtil.EarthPoint;
import com.youa.mobile.webshop.WebShopPage;

public class ShopMapPage extends MapActivity {
//
//	static MapView mMapView = null;
//	static View mPopView = null;
//	OverItemT overItemT = null;
//	GeoPoint mPoint = null;

	
//	private LocationItemAdapter mLocationAdapter;
//
	public final static String KEY_GEOPOINT = "geopoint",
			KEY_SNIPPET = "snippet", KEY_LAT = "place_x", KEY_PNAME = "p_name",
			KEY_LON = "place_y", KEY_REFID = "ref_id", SEARCH_TYPE = "type", KEY_SHOP_TYPE = "shop_type";
	private int mLatitude;
	private int mLongitude;
	private String pName ; // 商家名
//	private String pId;	//商家id
	private GeoPoint curPoint;
	private int shopType;
	private String shopId;
	
	private Drawable mCurMarker = null;
	private Drawable mOtherMarker = null;
	private View mPopView = null;
	private TextView 	mTitle;
	private ImageButton mImageButton;
//	private ProgressBar mProgressBar;
	private Button 		showLineBtn = null;		//查看路线
	private Button 		goOnlineShopBtn = null;	//去Wap店铺信息
	private ImageButton back;
	protected Handler mHandler = new Handler();
	private MapView mMapView = null;
	MKSearch mSearch = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BasePage.activityList.add(this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.shop_map_page);
		
		if(((LehoApp)ShopMapPage.this.getApplication()).mBMapMan==null){
			((LehoApp)ShopMapPage.this.getApplication()).initMap();
		}
		init();
		super.initMapActivity(((LehoApp)ShopMapPage.this.getApplication()).mBMapMan);
		initMapView();
		refreshMapView();
//		if (((LehoApp)ShopMapPage.this.getApplication()).isStartLoc) {
//			
//		} else {
//			showToast(R.string.map_localization_off);
//			return;
//		}
	}

	private void init(){
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		back = (ImageButton)findViewById(R.id.back);
		mLatitude = extras.getInt(KEY_LAT);
		mLongitude = extras.getInt(KEY_LON);
		//extras.getInt(KEY_ADDRESS);
		pName = extras.getString(KEY_PNAME);
//		pId = extras.getString(KEY_REFID);
		shopId = extras.getString(KEY_REFID);
		shopType = extras.getInt(KEY_SHOP_TYPE);
		updateCurPoint();
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(pName);
		mImageButton = (ImageButton) findViewById(R.id.back);
		showLineBtn = (Button) findViewById(R.id.look_line);
		goOnlineShopBtn = (Button) findViewById(R.id.go_onlineshop);
		if(shopType != 1){
			goOnlineShopBtn.setVisibility(View.GONE);
		}
		OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.back:
					finish();
					break;
				case R.id.look_line:
					// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
					MKPlanNode stNode = new MKPlanNode();
					GeoPoint goal = new GeoPoint(mLatitude,mLongitude);
					stNode.pt = curPoint;
					MKPlanNode enNode = new MKPlanNode();
					enNode.pt = goal;
					mSearch.walkingSearch(null, stNode, null, enNode);//此为异步，无需在主线程中异步调用
					break;
				case R.id.go_onlineshop:
					if(!TextUtils.isEmpty(shopId)){
						Intent i = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString(WebShopPage.SHOP_ID, shopId);
						i.putExtras(bundle);
						i.setClass(ShopMapPage.this, WebShopPage.class);
						startActivity(i);
					}else{
						//TODU 无法获取商家信息
					}
					break;
				default:
					break;
				}
			}
        };
        mImageButton.setOnClickListener(clickListener);
        showLineBtn.setOnClickListener(clickListener);
        goOnlineShopBtn.setOnClickListener(clickListener);
	}

	@Override
	protected void onPause() {
		if(((LehoApp)ShopMapPage.this.getApplication()).mBMapMan!=null){
			((LehoApp)ShopMapPage.this.getApplication()).mBMapMan.stop();
			((LehoApp)ShopMapPage.this.getApplication()).isMapActivityOpen=false;
		}
		super.onPause();
	}
	@Override
	protected void onResume() {
		if(((LehoApp)ShopMapPage.this.getApplication()).mBMapMan!=null){
			((LehoApp)ShopMapPage.this.getApplication()).isMapActivityOpen=true;
			((LehoApp)ShopMapPage.this.getApplication()).mBMapMan.start();
		}
		updateCurPoint();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void updateCurPoint(){
		LocationUtil lu = new LocationUtil(); 
		EarthPoint ep = lu.getCurLocation(ShopMapPage.this);
		curPoint = new GeoPoint((int)ep.latitude,(int)ep.longitude);
		
	}
	
	private void refreshMapView() {
		if (!((LehoApp)ShopMapPage.this.getApplication()).isStartLoc) {
			showLineBtn.setVisibility(View.GONE);
			showToast(R.string.map_localization_off);
		}else{
			HashMap<String, Object> myLocation = new HashMap<String, Object>();
			myLocation.put(KEY_GEOPOINT, curPoint);
			myLocation.put(KEY_SNIPPET, "我的位置");
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(
					new OverItemT(mCurMarker, this, myLocation));
		}
		HashMap<String, Object> goalMap = new HashMap<String, Object>();
		GeoPoint goal = new GeoPoint(mLatitude,mLongitude);
		goalMap.put(KEY_GEOPOINT, goal);
		goalMap.put(KEY_SNIPPET, pName);
		mMapView.getOverlays().add(
				new OverItemT(mOtherMarker, this, goalMap));
		mMapView.getController().setCenter(goal);
		mMapView.invalidate();
	}

	private void initMapView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		// 设定地图的缩放条
		mMapView.setBuiltInZoomControls(false);
		mMapView.setDrawOverlayWhenZooming(true);
		mMapView.getController().setZoom(15);
		
        mSearch = new MKSearch();
        mSearch.init(((LehoApp)ShopMapPage.this.getApplication()).mBMapMan, new MKSearchListener(){

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(ShopMapPage.this, ShopMapPage.this.getString(R.string.no_result_of_mapsearch), Toast.LENGTH_SHORT).show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(ShopMapPage.this, mMapView);
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.invalidate();
			    mMapView.getController().animateTo(res.getStart().pt);
			}
			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				
			}
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				
			}
        });
		
		// 添加ItemizedOverlay
		mCurMarker = getResources().getDrawable(R.drawable.cur_marka); // 得到需要标在地图上的资源
		mCurMarker.setBounds(0, 0, mCurMarker.getIntrinsicWidth(),
				mCurMarker.getIntrinsicHeight()); // 为maker定义位置和边界
		mOtherMarker = getResources().getDrawable(R.drawable.oth_marka);
		mOtherMarker.setBounds(0, 0, mOtherMarker.getIntrinsicWidth(),
				mOtherMarker.getIntrinsicHeight()); // 为maker定义位置和边界
		mPopView = super.getLayoutInflater().inflate(R.layout.popview, null);
		mPopView.setVisibility(View.GONE);
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		
	}

	protected void showToast(final int resourceID) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(getApplicationContext(),
						getString(resourceID), Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

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
			ShopMapPage.this.mPopView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (!TextUtils.isEmpty(mAddressName)) {
						LocationData loc = new LocationData();
						loc.latitude = mSelLatitude;
						loc.longitude = mSelLongitude;
						loc.locName = mAddressName;
						loc.sPid = mPid;
						//ShopMapPage.this.goBackWithData(loc);
					}
				}});
			if (!TextUtils.isEmpty(str)) {
				TextView tx = (TextView) (ShopMapPage.this.mPopView
						.findViewById(R.id.address));
				tx.setText(str);
//				MapPage.mPopView.setPadding(0, 0, 0,
//						marker.getIntrinsicHeight() / 2);
				ShopMapPage.this.mMapView.updateViewLayout(ShopMapPage.this.mPopView,
						new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, pt,
								MapView.LayoutParams.BOTTOM_CENTER));
				ShopMapPage.this.mPopView.setVisibility(View.VISIBLE);
			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint point, MapView map) {
			// TODO Auto-generated method stub
			// 消去弹出的气泡
			ShopMapPage.this.mPopView.setVisibility(View.GONE);
			return super.onTap(point, map);
		}
	}
	
}
