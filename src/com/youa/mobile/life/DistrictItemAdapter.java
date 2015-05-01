package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.life.data.DistrictData;

public class DistrictItemAdapter extends BaseAdapter {
	private Context context;
	private List<DistrictData> list = new ArrayList<DistrictData>();
	
	public DistrictItemAdapter(Context context, List<DistrictData> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.life_district_list_item, null);
		}
		TextView mDistrictName = (TextView) convertView.findViewById(R.id.district_name);
		DistrictData district = (DistrictData)getItem(position);
		mDistrictName.setText(district.name);
		TextView mDistrictBtn = (TextView) convertView.findViewById(R.id.district_status_btn);
		if(district.isFollowed){
			mDistrictBtn.setBackgroundResource(R.drawable.topic_unsub_btn_bg);
			mDistrictBtn.setText(context.getString(R.string.life_guanzhu_not));
		}else{
			mDistrictBtn.setBackgroundResource(R.drawable.feed_take_theme_button_bg);
			mDistrictBtn.setText(context.getString(R.string.life_guanzhu));
		}
		mDistrictBtn.setTag(position);
		mDistrictBtn.setOnClickListener((OnClickListener)context);
		convertView.setTag(R.id.TOPIC_TAG_KEY, district);
		return convertView; 
	}

}