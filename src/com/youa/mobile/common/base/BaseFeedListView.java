package com.youa.mobile.common.base;

import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.data.FeedData;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.Tools;
import com.youa.mobile.common.base.BaseFeedListView.BaseFeedHolder;

public class BaseFeedListView extends BaseListView<BaseFeedHolder, List<FeedData>> {
	public static final String TAG = "BaseFeedListView";
	private LayoutInflater mInflater;
	
//	private List<FeedData> mDataList;
	public class BaseFeedHolder extends BaseHolder {
		public ImageView headerImageView;
		public TextView nameView;
		public TextView timeView;
		// private LinearLayout contentView;
		public TextView contentView;
		public ImageView contentImgView;
		public TextView likeView;
		public TextView commentView;
		public TextView transpondView;
		// --
		public boolean isHeadImgGet;
		public boolean isContentImgGet;

	}

	public BaseFeedListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		mInflater = LayoutInflater.from(listView.getContext());
	}

	@Override
	protected View createTemplateView(int pos) {
		return mInflater.inflate(R.layout.feed_home_item, null);
	}

	@Override
	protected BaseFeedHolder getHolder(View convertView,int pos) {
		BaseFeedHolder holder = new BaseFeedHolder();
		holder.headerImageView = (ImageView) convertView.findViewById(R.id.user_head);
		holder.nameView = (TextView) convertView.findViewById(R.id.user_name);
		holder.timeView = (TextView) convertView.findViewById(R.id.time);
		holder.contentView = (TextView) convertView.findViewById(R.id.content);
		holder.contentImgView = (ImageView) convertView.findViewById(R.id.content_img);
		LinearLayout view = (LinearLayout) convertView
				.findViewById(R.id.bottom);
		holder.likeView = (TextView) view.findViewById(R.id.like);
		holder.commentView = (TextView) view.findViewById(R.id.comment);
		holder.transpondView = (TextView) view.findViewById(R.id.transport);
		return holder;
	}

	@Override
	protected void setDataWithHolder(
			BaseFeedHolder holder, 
			int position,
			boolean isMoving) {
		FeedData data = mDataList.get(position);
		setUserInfo(data, holder, position, isMoving);
		if (holder.timeView != null) {
			holder.timeView.setText(Tools.translateToString(Long.valueOf(data
					.getTime()) * 1000));
			holder.timeView.setVisibility(View.VISIBLE);
		} else {
			holder.timeView.setVisibility(View.GONE);
		}
		if (data.getContent() != null) {
			// createContent(data.contents, holder.contentView);
			holder.contentView.setVisibility(View.VISIBLE);
			holder.contentView.setText(data.getContent());
		} else {
			holder.contentView.setVisibility(View.GONE);
		}
		
		holder.likeView.setText(data.getLike_num());
		holder.commentView.setText(data.getComment_num());
		holder.transpondView.setText(data.getTranspond_num());
		// 设置图片
		Log.d(TAG, ">>headerID:" + data.getHeaderImgid());
		ImageUtil.setImageView(
				getContext(),
				holder.headerImageView, 
				data.getHeaderImgid(),
				ImageUtil.HEADER_SIZE_BIG,
				ImageUtil.HEADER_SIZE_BIG,
				R.drawable.head_men);
		setContentImage(holder.contentImgView, data.getImageData());
	}

	private void setUserInfo(
			FeedData data, 
			BaseFeedHolder holder, 
			int position,
			boolean isMoving) {
		if (data.getUserName() != null) {
			holder.nameView.setText(data.getUserName());
			holder.nameView.setVisibility(View.VISIBLE);
		} else {
			holder.nameView.setText("" + position);
		}
	}
	
	private void setContentImage(ImageView imageView, List<FeedData.ImageData> imageDataList) {		
		if(imageDataList != null && imageDataList.size() > 0) {
			FeedData.ImageData imageData = imageDataList.get(0);
			String imageid = imageData.getImageId();
			Log.d(TAG, ">>setContentImage:" + imageid);
			ImageUtil.setImageView(
					getContext(),
					imageView, 
					imageid,
					ImageUtil.HEADER_SIZE_BIG,
					ImageUtil.HEADER_SIZE_BIG,
					R.drawable.head_men);
		}
	}

	@Override
	protected void treateStopEvent(BaseFeedHolder holder, int position) {

		
	}
}
