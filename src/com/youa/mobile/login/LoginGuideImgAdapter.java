package com.youa.mobile.login;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.MainActivity;
import com.youa.mobile.PreparePage;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;

public class LoginGuideImgAdapter extends BaseAdapter {
	private Context context;
	private List<Integer> list = new ArrayList<Integer>();
	public LoginGuideImgAdapter(Context context, List<Integer> list) {
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
			convertView = inflater.inflate(R.layout.login_login_guide_item, null);
		}
//		ImageView itemImg1 = (ImageView) convertView.findViewById(R.id.login_guide_item_img1);
		ImageView itemImg2 = (ImageView) convertView.findViewById(R.id.login_guide_item_img2);
		//		ImageView itemImg3 = (ImageView) convertView.findViewById(R.id.login_guide_item_img3);
		itemImg2.setBackgroundResource((Integer)getItem(position));
//		if(position==0){
//			itemImg1.setImageResource(R.drawable.wenzi1);
//			itemImg3.setImageResource(R.drawable.dian1);
//		}if(position==1){
//			itemImg1.setImageResource(R.drawable.wenzi2);
//			itemImg3.setImageResource(R.drawable.dian2);
//		}if(position==2){
//			itemImg1.setImageResource(R.drawable.wenzi3);
//			itemImg1.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View v) {
//					SharedPreferences	preference=context.getSharedPreferences(SystemConfig.XML_FILE_LOGIN_GUIDE, Context.MODE_WORLD_WRITEABLE);
//					SharedPreferences.Editor editor = preference.edit();
//					editor.putBoolean(PreparePage._IS_GUIDE, true);
//					editor.commit();
//					if(!PreparePage.isFrist){
//						startMainActivity();	
//					}
//					
//				}});
//			itemImg3.setImageResource(R.drawable.dian3);
//		}
		convertView.setBackgroundDrawable(null);
		return convertView;
	}
	public void startMainActivity() {
		PreparePage.isFrist=true;
		PreparePage.GUIDE_POS=3;
		Intent intent = new Intent(context, LehoTabActivity.class);
		context.startActivity(intent);
		((Activity) context).finish();
	}
}
