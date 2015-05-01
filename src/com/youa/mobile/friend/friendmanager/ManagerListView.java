package com.youa.mobile.friend.friendmanager;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.friend.friendmanager.ManagerListView.AttentionHolder;
import com.youa.mobile.ui.base.BaseHolder;

public class ManagerListView extends BaseListView<AttentionHolder, List<ManagerSuperMenCalssifyData>>{
	//废弃了
	private LayoutInflater mInflater;
	
	public class AttentionHolder extends BaseHolder{
		private ImageView imgView;
		private TextView textView;
	}

	public ManagerListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		mInflater = LayoutInflater.from(listView.getContext());
	}

	@Override
	protected View createTemplateView(int pos) {
		return mInflater.inflate(R.layout.feed_friend_manager_item, null);
	}

	@Override
	protected AttentionHolder getHolder(View convertView,int pos) {
		AttentionHolder holder = new AttentionHolder();
		holder.textView=(TextView)convertView.findViewById(R.id.manager_text);
		holder.imgView=(ImageView)convertView.findViewById(R.id.manager_img);
		return holder;
	}

	@Override
	protected void setDataWithHolder(AttentionHolder holder, int position,
			boolean isMoving) {
		ManagerSuperMenCalssifyData data= mDataList.get(position);
		holder.textView.setText(getSuperName(data.name));	
		holder.imgView.setImageResource(getSuperImgId(data.name));
		holder.textView.setVisibility(View.VISIBLE);
	}
	
	public String getSuperName(String Value) {
		String key=Value;
//		if ("hot_recommend".equals(Value)) {
//			key = "热门推荐";
//		} else
			if ("share_cate".equals(Value)) {
			key = "乐享美食";
		} else if ("play_group".equals(Value)) {
			key = "玩乐帮派";
		} else if ("city_beauty".equals(Value)) {
			key = "都市丽人";
		} else if ("mother_baby".equals(Value)) {
			key = "辣妈萌宝";
		} else if ("roman_mary".equals(Value)) {
			key = "浪漫婚恋";
		} else if ("happy_house".equals(Value)) {
			key = "幸福居家";
		}
		return key;
	}
	
	public int getSuperImgId(String Value) {
		int id=0;
		if ("hot_recommend".equals(Value)) {
//			key = "热门推荐";
		} else if ("share_cate".equals(Value)) {
//			key = "乐享美食";
			id = R.drawable.lexiangmeishi;
		} else if ("play_group".equals(Value)) {
//			key = "玩乐帮派";
			id=R.drawable.wanlebangpai;
		} else if ("city_beauty".equals(Value)) {
//			key = "都市丽人";
			id= R.drawable.dushiliren;
		} else if ("mother_baby".equals(Value)) {
//			key = "辣妈萌宝";
			id = R.drawable.mengmalabao;
		} else if ("roman_mary".equals(Value)) {
//			key = "浪漫婚恋";
			id = R.drawable.langmanhunlian;
		} else if ("happy_house".equals(Value)) {
//			key = "幸福居家";
			id=R.drawable.xingfujujia;
		}
		return id;
	}
}
