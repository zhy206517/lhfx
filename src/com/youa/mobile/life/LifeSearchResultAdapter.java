package com.youa.mobile.life;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.life.data.DistrictData;
import com.youa.mobile.life.data.LifeItemData;
import com.youa.mobile.life.data.ShareClassifyData;
import com.youa.mobile.life.data.SuperPeopleClassify;
import com.youa.mobile.life.data.UserInfo;
import com.youa.mobile.theme.data.TopicData;

public class LifeSearchResultAdapter<T> extends ArrayAdapter<T> {
	//private List<T> mObjects;
	private Context mContext;    
	private int mResource;
	private OnClickListener onClickListener;
	
    private LayoutInflater mInflater;
	public LifeSearchResultAdapter(Context context, int resource, int textViewResourceId, List<T> objects, OnClickListener listener) {
		super(context, resource, textViewResourceId, objects);
		this.mContext = context;
		this.mResource = resource;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //this.mObjects = objects;
        this.onClickListener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		T itemObj = getItem(position);
		//Object obj = convertView.getTag();
        if (convertView == null) {
        	convertView = mInflater.inflate(mResource, parent, false);
        }
        ImageView image;
        TextView text;
        Button button;
        ImageView guide;
        guide = (ImageView)convertView.findViewById(R.id.guide);
        image = (ImageView)convertView.findViewById(R.id.image);
    	text = (TextView)convertView.findViewById(R.id.name);
    	button = (Button)convertView.findViewById(R.id.button);
    	guide.setVisibility(View.GONE);
        if (itemObj instanceof UserInfo) {
        	UserInfo user = (UserInfo)itemObj;
//        	if("0".equals(user.heardImgId) || "".equals(user.heardImgId)){
//        		image.setImageResource(user.getSexResId());
//        	}else{
//        		ImageUtil.setImageViewOriginal(mContext, image, user.heardImgId, user.getSexResId());
//        	}
        	image.setVisibility(View.GONE);
        	text.setText(user.userName);
//        	button.setOnClickListener(btnOnClickListener);
        	button.setVisibility(View.GONE);
        	convertView.setTag(user.userId);
        } else if (itemObj instanceof DistrictData){
        	DistrictData district = (DistrictData)itemObj;
        	image.setVisibility(View.GONE);
            text.setText(district.name);
            button.setOnClickListener(onClickListener);
        } else if (itemObj instanceof TopicData){
        	TopicData topic = (TopicData)itemObj;
        	image.setVisibility(View.GONE);
        	text.setText(topic.name);
        	button.setOnClickListener(onClickListener);
        	button.setVisibility(View.GONE);
        	convertView.setTag(topic);
        } else if (itemObj instanceof LifeItemData) {
        	LifeItemData leftItem = (LifeItemData)itemObj;
        	image.setImageResource(leftItem.getResId());
        	text.setText(leftItem.getTitle());
        	button.setVisibility(View.GONE);
        } else if(itemObj instanceof ShareClassifyData){
        	guide.setImageResource(((ShareClassifyData) itemObj).rightImage);
        	guide.setVisibility(View.VISIBLE);
        	button.setVisibility(View.GONE);
        	image.setVisibility(View.VISIBLE);
			int resid = 0;
			switch (position) {
			case 0:
				resid=R.drawable.fengmian;
				break;
			case 1:
				resid=R.drawable.langmanhunlian;
				break;
			case 2:
				resid=R.drawable.mengmalabao;
				break;
			case 3:
				resid=R.drawable.xingfujujia;
				break;
			case 4:
				resid=R.drawable.lexiangmeishi;
				break;
			case 5:
				resid=R.drawable.wanlebangpai;
				break;
			case 6:
				resid=R.drawable.dushiliren;
				break;
			}
			image.setBackgroundDrawable(null);
        	image.setImageResource(resid);
        	ShareClassifyData classify = (ShareClassifyData)itemObj;
        	text.setText(classify.name);
        } else if(itemObj instanceof SuperPeopleClassify){
        	guide.setVisibility(View.VISIBLE);
        	button.setVisibility(View.GONE);
        	image.setVisibility(View.GONE);
        	SuperPeopleClassify classify = (SuperPeopleClassify)itemObj;
        	text.setText(getSuperName(classify.name));
        }
        convertView.setTag(itemObj);
        return convertView;
	}
	public String getSuperName(String Value) {
		String key = Value;
		if ("hot_recommend".equals(Value)) {
			key = "热门推荐";
		} else if ("share_cate".equals(Value)) {
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
}
