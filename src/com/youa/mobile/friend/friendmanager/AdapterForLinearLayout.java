package com.youa.mobile.friend.friendmanager;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youa.mobile.R;

public class AdapterForLinearLayout extends BaseAdapter {
	
	private List<ManagerSuperMenCalssifyData> mDataList;
	
	public void setData(List<ManagerSuperMenCalssifyData> list){
		this.mDataList=list;
		notifyDataSetChanged();
	}
	
    private LayoutInflater mInflater;

    public AdapterForLinearLayout(Context context) {
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		convertView = mInflater
				.inflate(R.layout.feed_friend_manager_item, null);
		TextView textView = (TextView) convertView
				.findViewById(R.id.manager_text);
		ImageView imgView = (ImageView) convertView
				.findViewById(R.id.manager_img);
		ManagerSuperMenCalssifyData data = mDataList.get(position);
		textView.setText(getSuperName(data.name));
		imgView.setImageResource(getSuperImgId(data.name));
		 convertView.setTag(position);
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
		//丽人  美食   居家   文化   休闲    婚嫁   母婴
		int id=0;
		if ("hot_recommend".equals(Value)) {
//			key = "热门推荐";
		} else if ("share_cate".equals(Value)||"美食".equals(Value)) {
//			key = "乐享美食";
			id = R.drawable.lexiangmeishi;
		} else if ("play_group".equals(Value)||"文化".equals(Value)||"休闲".equals(Value)) {
//			key = "玩乐帮派";
			id=R.drawable.wanlebangpai;
		} else if ("city_beauty".equals(Value)||"丽人".equals(Value)) {
//			key = "都市丽人";
			id= R.drawable.dushiliren;
		} else if ("mother_baby".equals(Value)||"母婴".equals(Value)) {
//			key = "辣妈萌宝";
			id = R.drawable.mengmalabao;
		} else if ("roman_mary".equals(Value)||"婚嫁".equals(Value)) {
//			key = "浪漫婚恋";
			id = R.drawable.langmanhunlian;
		} else if ("happy_house".equals(Value)||"居家".equals(Value)) {
//			key = "幸福居家";
			id=R.drawable.xingfujujia;
		}
		return id;
	}
	

    @SuppressWarnings("unchecked")
    public String get(int position, Object key) {
        Map<String, ?> map = (Map<String, ?>) getItem(position);
        return map.get(key).toString();
    }

    private void bindView(View view, Map<String, ?> item, String from) {
        Object data = item.get(from);
        if (view instanceof TextView) {
            ((TextView) view).setText(data == null ? "" : data.toString());
        }
    }
}

