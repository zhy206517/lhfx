package com.youa.mobile.theme;

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
import com.youa.mobile.theme.data.TopicData;

public class TopicSubItemAdapter extends BaseAdapter {
	private Context context;
	private List<TopicData> list = new ArrayList<TopicData>();
	
	public TopicSubItemAdapter(Context context, List<TopicData> list) {
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
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.topic_add_list_item, null);
		}
		TextView mTopName = (TextView) convertView.findViewById(R.id.topic_name);
		TopicData topic = (TopicData)getItem(position);
		mTopName.setText("#"+topic.name+"#");
		TextView mTopicAddBtn = (TextView) convertView.findViewById(R.id.sub_unsub_topic_btn);
		if(topic.isSubscribe){
			mTopicAddBtn.setBackgroundResource(R.drawable.topic_unsub_btn_bg);
			mTopicAddBtn.setText(context.getString(R.string.topic_unsub_btn_lable));
		}else{
			mTopicAddBtn.setBackgroundResource(R.drawable.feed_take_theme_button_bg);
			mTopicAddBtn.setText(context.getString(R.string.topic_sub_btn_lable));
		}
		mTopicAddBtn.setTag(position);
		//mTopicAddBtn.setTag(R.id.TOPIC_TAG_KEY, topic);
		mTopicAddBtn.setOnClickListener((OnClickListener)context);
		convertView.setTag(R.id.TOPIC_TAG_KEY, topic);
		return convertView; 
	}

}
