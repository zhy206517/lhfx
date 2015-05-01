package com.youa.mobile.content;

import java.util.List;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageData;
import com.youa.mobile.common.util.picture.ImageUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ContentImgAdapter extends BaseAdapter{
	
	private class ContentImgHolder{
		private ImageView imageView;
		private ImageView[] imageButtons;
		private RelativeLayout progressBar;
	}
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<ImageData> mDataList;
	private int mCurrentPoint;
	
	public ContentImgAdapter(Context context) {
		mContext=context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setData(List<ImageData> list){
		mDataList=list;
		notifyDataSetChanged();
	}
	
	public void setCurrentPoint(int position){
		mCurrentPoint = position;
	}
	
	@Override
	public int getCount() {
		if(mDataList==null){
			return 0 ;
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
		ContentImgHolder holder =null;
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.feed_content_head_contentimg_item, null);
			holder = new ContentImgHolder();
			holder.imageView=(ImageView)convertView.findViewById(R.id.content_item_img);
			holder.imageButtons= new ImageView[8];
			for (int i = 0; i < holder.imageButtons.length; i++) {
				holder.imageButtons[i]=(ImageView)convertView.findViewById(getPointId(i+1));//getPointId(i+1)
			}
			holder.progressBar=(RelativeLayout)convertView.findViewById(R.id.img_load_progressBar);
			convertView.setTag(holder);
		}else{
			holder=(ContentImgHolder)convertView.getTag();
		}
		//-------------------------------------------------------
		ImageData data=mDataList.get(position);
		int size = 0;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// 背景86,图片80
			size = ImageUtil.CONTENT_SIZE_LITTLE;
		} else {// 背景126,图片120
			size = ImageUtil.CONTENT_SIZE_LARGE;
		}
		ImageUtil.setImageView(mContext, holder.imageView,data.id, size, size, data.angle, holder.progressBar);
//		ImageUtil.setImageView(
//				mContext,
//				holder.imageView,
//				data.id,
//				size,
//				size,
//				-1);
		//-------------------------------------------------------
		showPoint(holder);
		return convertView;
	}
	
	private int getPointId(int i) {
		switch (i) {
		case 1:
			return R.id.point1;
		case 2:
			return R.id.point2;
		case 3:
			return R.id.point3;
		case 4:
			return R.id.point4;
		case 5:
			return R.id.point5;
		case 6:
			return R.id.point6;
		case 7:
			return R.id.point7;
		case 8:
			return R.id.point8;
		}
		return 0;
	}
	
	private void showPoint(ContentImgHolder holder){
		if(mDataList==null||mDataList.size()<=0){
			return;
		}
		int size = mDataList.size();
		for (int i = 0; i < size; i++) {
			holder.imageButtons[i].setVisibility(View.VISIBLE);
		}
		holder.imageButtons[mCurrentPoint].setImageResource(R.drawable.point_white_focus);
	}

}
