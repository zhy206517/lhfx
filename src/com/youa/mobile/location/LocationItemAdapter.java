package com.youa.mobile.location;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.location.data.LocationData;

public class LocationItemAdapter extends BaseAdapter {
	private Context context;
	private List<LocationData> list = new ArrayList<LocationData>();
	private String address;
	private String plid;

	private class LocationHolder {
		private ImageView addressIcon;
		private TextView placeName;
		private TextView addressName;
		private ImageView addressSel;
	}

	public LocationItemAdapter(Context context, List<LocationData> list) {
		this.context = context;
		this.list = list;
	}

	public LocationItemAdapter(Context context, List<LocationData> list,
			String address, String tagPlid) {
		this.context = context;
		this.list = list;
		this.address = address;
		this.plid = tagPlid;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LocationHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.location_list_item, null);
			holder = new LocationHolder();
			holder.addressIcon = (ImageView) convertView
					.findViewById(R.id.address_icon);
			holder.placeName = (TextView) convertView
					.findViewById(R.id.place_name);
			holder.addressName = (TextView) convertView
					.findViewById(R.id.place_address);
			holder.addressSel = (ImageView) convertView
					.findViewById(R.id.address_selector);
			convertView.setTag(holder);
		} else {
			holder = (LocationHolder) convertView.getTag();
		}
		LocationData loc = (LocationData) getItem(position);
		holder.addressIcon.setImageResource(R.drawable.address_icon);
		holder.addressSel.setVisibility(View.GONE);
		if (TextUtils.isEmpty(plid)) {
			if (!TextUtils.isEmpty(address) && address.equals(loc.locName)) {
				holder.addressIcon
						.setImageResource(R.drawable.address_selector_icon);
				holder.addressSel.setVisibility(View.VISIBLE);
			}
		} else if (plid.equals(loc.sPid)) {
			holder.addressIcon
					.setImageResource(R.drawable.address_selector_icon);
			holder.addressSel.setVisibility(View.VISIBLE);
		}

		holder.placeName.setText(loc.locName);
		holder.addressName.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(loc.addName)) {
			holder.addressName.setText(loc.addName);
			holder.addressName.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

}
