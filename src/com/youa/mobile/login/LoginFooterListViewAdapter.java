package com.youa.mobile.login;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.login.data.LoginFooterItem;

public class LoginFooterListViewAdapter extends BaseAdapter {
	private Context context;
	private List<LoginFooterItem> list = new ArrayList<LoginFooterItem>();
	public LoginFooterListViewAdapter(Context context, List<LoginFooterItem> list) {
		this.context = context;
		this.list = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
			convertView = inflater.inflate(R.layout.login_login_footer_item, null);
		}
		TextView itemText = (TextView) convertView.findViewById(R.id.login_footer_item_text);
		ImageView logo = (ImageView) convertView.findViewById(R.id.login_footer_item_logo);
		LoginFooterItem item = (LoginFooterItem)getItem(position);
		itemText.setText(item.getItemText());
		logo.setImageDrawable(context.getResources().getDrawable(item.getLogoResId()));
		return convertView;
	}
}
