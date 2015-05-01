package com.youa.mobile.information;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youa.mobile.R;

public class RegionListViewAdapter extends BaseAdapter {
	private Context context;
	private List<String> list = new ArrayList<String>();
	public RegionListViewAdapter(Context context, List<String> list) {
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.information_select_region_item, null);
		}
		TextView mRegionName = (TextView) convertView.findViewById(R.id.region_name);
		mRegionName.setText((String)getItem(position));
		return convertView;
	}
}
