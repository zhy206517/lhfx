//package com.youa.mobile.common.base;
//
//import java.util.List;
//
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.youa.mobile.R;
//import com.youa.mobile.common.base.BaseUserListView.BaseUserHolder;
//import com.youa.mobile.common.data.FeedData;
//import com.youa.mobile.common.data.UserData;
//import com.youa.mobile.common.util.picture.ImageUtil;
//import com.youa.mobile.common.util.picture.UserImageLoader;
//import com.youa.mobile.ui.base.BaseHolder;
//
//public class BaseUserListView extends BaseListView<BaseUserHolder, List<UserData>> {
//	public static final String TAG = "BaseFeedListView";
//	private LayoutInflater mInflater;
//	
////	private List<FeedData> mDataList;
//	public class BaseUserHolder extends BaseHolder {
//		public ImageView headerImageView;
//		public TextView nameView;
//
//	}
//
//	public BaseUserListView(ListView listView, View header, View footer) {
//		super(listView, header, footer);
//		mInflater = LayoutInflater.from(listView.getContext());
//	}
//
//	@Override
//	protected View createTemplateView() {
//		return mInflater.inflate(R.layout.information_attent_list_item, null);
//	}
//
//	@Override
//	protected BaseUserHolder getHolder(View convertView) {
//		BaseUserHolder holder = new BaseUserHolder();
//		holder.headerImageView = (ImageView) convertView.findViewById(R.id.user_head);
//		holder.nameView = (TextView) convertView.findViewById(R.id.user_name);
//		return holder;
//	}
//
//	@Override
//	protected void setDataWithHolder(
//			BaseUserHolder holder, 
//			int position,
//			boolean isMoving) {
//		UserData data = mDataList.get(position);
//		setUserInfo(data, holder, position, isMoving);		
//		// 设置图片
//		ImageUtil.setImageView(
//				getContext(),
//				holder.headerImageView, 
//				data.getHeaderImgid());
////		setImage(holder.headerImageView, ImageUtil.getImageURL(data.getHeaderImgid()));
//	}
//
//	private void setUserInfo(
//			UserData data, 
//			BaseUserHolder holder, 
//			int position,
//			boolean isMoving) {
//		if (data.getUserName() != null) {
//			holder.nameView.setText(data.getUserName());
//			holder.nameView.setVisibility(View.VISIBLE);
//		} else {
//			holder.nameView.setText("" + position);
//		}
//	}
//	
////	private void setImage(
////			final ImageView imageView, 
////			final String url) {
////		imageView.setBackgroundResource(R.drawable.feed_head_bg);
////		if(url == null) {		
////			return;
////		}
////		Log.d(TAG, ">>setImage-url:" + url);
////		UserImageLoader.getInstance().loadDrawable(
////				getContext(), 
////				url, 
////				getContext().getResources().getDrawable(R.drawable.feed_head_bg),
////				new UserImageLoader.OnImageLoadListener(){
////					@Override
////					public void onImageLoaded(
////							Drawable imageDrawable,
////							String imageUrl) {
////						imageView.setImageDrawable(imageDrawable);
////					}					
////				});
////	}
//
//	@Override
//	protected void treateStopEvent(BaseUserHolder holder, int position) {
//		// TODO Auto-generated method stub
//		
//	}
//}
