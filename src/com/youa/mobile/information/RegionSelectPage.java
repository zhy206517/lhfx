package com.youa.mobile.information;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.information.util.RegionDataUtil;
import com.youa.mobile.input.util.InputUtil;

public class RegionSelectPage extends BasePage {
	public final static String RESULT_ADDRESS_PRVINCE = "address_province";
	public final static String RESULT_ADDRESS_CITY = "address_city";
	public final static String RESULT_ADDRESS_COUNTIES = "address_counties";
	private final static String TAG = "RegionSelectPage";
	private ImageButton back;
	private TextView mTitle;
	private ListView mProvinceListView;
	private RegionListViewAdapter mPlvAdapter;
	private ListView mCityListView;
	private ListView mCountiesListView;
	private RegionListViewAdapter mCtlvAdapter;
	private RegionListViewAdapter mColvAdapter;
	private ListViewItemSelestedListener onRegionItemClickListener = new ListViewItemSelestedListener();
	private Button mSendButton ;
	
	private List<String> mProvinceData = new ArrayList<String>(0);
	private List<String> mCityData = new ArrayList<String>(0);
	private List<String> mCountiesData = new ArrayList<String>(0);
	
	private String mProvinceName, mCityName, mCountiesName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_select_region);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}
	private void initView(){
		mSendButton = (Button)findViewById(R.id.send);
		mSendButton.setVisibility(View.GONE);
		back = (ImageButton)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack(false);
			}
		});
		mTitle = (TextView)findViewById(R.id.title);
		mTitle.setText(R.string.regist_error_address);
		mProvinceListView = (ListView)findViewById(R.id.province_list);
		mCityListView = (ListView)findViewById(R.id.city_list);
		mCountiesListView = (ListView) findViewById(R.id.county_list);
		
		mProvinceData = RegionDataUtil.getAllProvince();
		mPlvAdapter = new RegionListViewAdapter(RegionSelectPage.this, mProvinceData);
		mProvinceListView.setAdapter(mPlvAdapter);
		
		mCtlvAdapter = new RegionListViewAdapter(RegionSelectPage.this, mCityData);
		mCityListView.setAdapter(mCtlvAdapter);
		
		mColvAdapter = new RegionListViewAdapter(RegionSelectPage.this, mCountiesData);
		mCountiesListView.setAdapter(mColvAdapter);
		
		mProvinceListView.setOnItemClickListener(onRegionItemClickListener);
		mCityListView.setOnItemClickListener(onRegionItemClickListener);
		mCountiesListView.setOnItemClickListener(onRegionItemClickListener);
	}
	
	private class ListViewItemSelestedListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView mRegionName = (TextView) view.findViewById(R.id.region_name);
			CharSequence regionName = mRegionName.getText();
			switch (parent.getId()) {
				case R.id.province_list:
					mProvinceListView.setVisibility(View.GONE);
					mCountiesListView.setVisibility(View.GONE);
					mCityListView.setVisibility(View.VISIBLE);
					mCityData.clear();
					mCityData.addAll(RegionDataUtil.getCitysByProvince(regionName.toString()));
					mTitle.setText(regionName);
					mProvinceName = regionName.toString();
					mCtlvAdapter.notifyDataSetChanged();
					break;
				case R.id.city_list:
					mProvinceListView.setVisibility(View.GONE);
					mCountiesListView.setVisibility(View.VISIBLE);
					mCityListView.setVisibility(View.GONE);
					mCountiesData.clear();
					List<String> cDataTemp = RegionDataUtil.getAllCountiesByProvinceAndCity(mProvinceName, regionName.toString(),getString(R.string.adderss_default_region));
					mCityName = regionName.toString();
					/*if(null == cDataTemp || cDataTemp.size() == 0){
						goBack(true);
					}else{
					}*/
					mCountiesData.addAll(cDataTemp);
					mTitle.setText(regionName);
					mColvAdapter.notifyDataSetChanged();
					break;
				case R.id.county_list:
					mCountiesName = regionName.toString();
					goBack(true);
					break;
			}
			
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				goBack(false);
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	private void goBack(boolean result) {
		if(mCountiesListView.getVisibility() == View.VISIBLE){
			if(result){
				Intent data = new Intent();
				data.putExtra(RESULT_ADDRESS_PRVINCE, mProvinceName);
				data.putExtra(RESULT_ADDRESS_CITY, mCityName);
				if(mCountiesName.equals(getString(R.string.adderss_default_region))){
					mCountiesName = "";
				}
				data.putExtra(RESULT_ADDRESS_COUNTIES, mCountiesName);
				InputUtil.LOGD(TAG, "return address : " + mProvinceName+mCityName+mCountiesName);
				setResult(RESULT_OK, data);
				finish();
			}
			mCountiesListView.setVisibility(View.GONE);
			//mProvinceListView.setVisibility(View.GONE);
			mCityListView.setVisibility(View.VISIBLE);
			mCountiesName = "";
			mTitle.setText(mProvinceName);
		}
		if(mCityListView.getVisibility() == View.VISIBLE){
			//mCountiesListView.setVisibility(View.GONE);
			mCityListView.setVisibility(View.GONE);
			mProvinceListView.setVisibility(View.VISIBLE);
			mCityName = "";
			mTitle.setText(R.string.regist_error_address);
		}else if(mProvinceListView.getVisibility() == View.VISIBLE){
			finish();
		}
	}
}
