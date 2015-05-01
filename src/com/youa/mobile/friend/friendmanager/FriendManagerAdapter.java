package com.youa.mobile.friend.friendmanager;

import java.util.List;

import com.youa.mobile.R;
import com.youa.mobile.friend.friendmanager.ManagerListView.AttentionHolder;
import com.youa.mobile.ui.base.BaseHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendManagerAdapter extends BaseAdapter{
	
	public class AttentionHolder extends BaseHolder{
		private ImageView imgView;
		private TextView textView;
	}
	
	private LayoutInflater mInflater;
	private List<ManagerSuperMenCalssifyData> mDataList;
	public FriendManagerAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	public void setData(List<ManagerSuperMenCalssifyData> list){
		this.mDataList=list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(mDataList==null){
			return 0;
		}
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		if(mDataList==null){
			return null;
		}
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AttentionHolder holder=null;
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.feed_friend_manager_item, null);
			holder = new AttentionHolder();
			holder.textView=(TextView)convertView.findViewById(R.id.manager_text);
			holder.imgView=(ImageView)convertView.findViewById(R.id.manager_img);
			convertView.setTag(holder);
		}else{
			holder = (AttentionHolder)convertView.getTag();
		}
//		if(position==0){
////			convertView.setBackgroundResource(R.drawable.friend_manager_list_up);
//			convertView.setBackgroundColor(0XFFFF00FF);
//		}else{
//			convertView.setBackgroundColor(0XFFFF0FFF);
//		}
//		else if(position==mDataList.size()-1){
//			convertView.setBackgroundResource(R.drawable.friend_manager_list_down);
//		}else{
//			convertView.setBackgroundResource(R.drawable.friend_manager_list_middle);
//		}
		ManagerSuperMenCalssifyData data= mDataList.get(position);
		holder.textView.setText(getSuperName(data.name));	
		holder.imgView.setImageResource(getSuperImgId(data.name));
		return convertView;
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
