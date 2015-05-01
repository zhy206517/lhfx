package com.youa.mobile.input;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.theme.data.TopicData;

public class BaseTopicAdapter extends BaseAdapter {
	private Context context;
	private List<TopicData> list = new ArrayList<TopicData>();
	public BaseTopicAdapter(Context context, List<TopicData> list) {
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
			convertView = inflater.inflate(R.layout.input_topic_list_item, null);
		}
		TextView mTopicName = (TextView) convertView.findViewById(R.id.topic_name);
		/*if(position == 0){
			LayoutParams lps = mTopicName.getLayoutParams();
			if(null != lps){
				if(theFirstItemHeigth!=0){
					theFirstItemHeigth = lps.height + 5;
				}
				lps.height = theFirstItemHeigth;
				LoginUtil.LOGD("TOPIC_ADAPTER", position + "   " + theFirstItemHeigth);
				mTopicName.setLayoutParams(lps);
			}
		}*/
		TopicData info = (TopicData)getItem(position);
		mTopicName.setText("#"+info.name+"#");
		return convertView;
	}
}
