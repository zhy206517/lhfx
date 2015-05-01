package com.youa.mobile.friend.friendsearch;

import java.util.List;

import com.youa.mobile.R;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.ui.base.BaseHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendSearchAdapter extends BaseAdapter{
	
	public class SearchHolder extends BaseHolder{
		private ImageView imgView;
		private TextView textView;
	}
	
	private LayoutInflater mInflater;
	private List<UserInfo> mDataList;
	private Context mContext;
	public FriendSearchAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setData(List<UserInfo> list) {
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
		SearchHolder holder= null;
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.feed_friend_manager_search_item, null);
			holder = new SearchHolder();
			holder.imgView=(ImageView)convertView.findViewById(R.id.search_head);
			holder.textView=(TextView)convertView.findViewById(R.id.search_text);
			convertView.setTag(holder);
		}else{		
			holder = (SearchHolder)convertView.getTag();
		}
		UserInfo data=mDataList.get(position);
		ImageUtil.setHeaderImageView(mContext, holder.imgView,
				data.heardImgId, data.getSexResId());
		holder.textView.setText(data.userName);
		return convertView;
	}
	
}
