package com.youa.mobile.circum;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.AbstractListView;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.location.util.LocationUtil;
import com.youa.mobile.location.util.LocationUtil.EarthPoint;
import com.youa.mobile.utils.LogUtil;

public class CircumListView extends
		AbstractListView<CircumHolder, List<HomeData>> {

	private static final int ITEM_CHILDREN_COUNT = 7;
	private static final int[] IMG_WIDTH = { 450, 220, 220, 220, 220, 220, 220 };
	private static final int[] IMG_HEIGHT = { 450, 180, 180, 360, 180, 360, 180 };
	private static int[] IMG_REAL_WIDTH;
	private static int[] IMG_REAL_HEIGHT;

	private LayoutInflater mInflater;
	private Context mContext;
	public String locationName;

	public CircumListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		mContext = listView.getContext();
		mInflater = LayoutInflater.from(mContext);
		if (SystemConfig.SCREEN_WIDTH > 480) {
			DisplayMetrics metrics = mContext.getResources()
					.getDisplayMetrics();
			float dpDelta = metrics.densityDpi / 160f;
			float dp = SystemConfig.SCREEN_WIDTH / dpDelta;
			LogUtil.d(TAG, "CircumListView. delta = " + dpDelta + ", dp = "
					+ dp);
			float borderDp = 10;
			float clearanceDp = 6;
			int dw = (int) ((dp - borderDp * 2) * dpDelta);
			int w = (int) ((dw - clearanceDp * dpDelta) / 2);
			int dh = dw * 339 / 450;
			int h = w * 169 / 220;
			LogUtil.d(TAG, "CircumListView. dw = " + dw + ", w = " + w
					+ ", dh = " + dh + ", h = " + h);
			IMG_REAL_WIDTH = new int[] { dw, w, w, w, w, w, w };
			IMG_REAL_HEIGHT = new int[] { dh, h, h, dh, h, dh, h };
		}
	}

	@Override
	protected View createTemplateView() {
		return mInflater.inflate(R.layout.circum_feed_item, null);
	}

	@Override
	protected CircumHolder getHolder(View convertView) {
		CircumHolder holder = new CircumHolder();
		holder.mPostId = new String[ITEM_CHILDREN_COUNT];
		holder.isContentImgGet = new boolean[ITEM_CHILDREN_COUNT];
		holder.subCells = new View[] {
				convertView.findViewById(R.id.circum_item_0),
				convertView.findViewById(R.id.circum_item_1),
				convertView.findViewById(R.id.circum_item_2),
				convertView.findViewById(R.id.circum_item_3),
				convertView.findViewById(R.id.circum_item_4),
				convertView.findViewById(R.id.circum_item_5),
				convertView.findViewById(R.id.circum_item_6) };
		holder.mHeaderLayout = new View[] {
				convertView.findViewById(R.id.item_0_layout),
				convertView.findViewById(R.id.item_1_layout),
				convertView.findViewById(R.id.item_2_layout),
				convertView.findViewById(R.id.item_3_layout),
				convertView.findViewById(R.id.item_4_layout),
				convertView.findViewById(R.id.item_5_layout),
				convertView.findViewById(R.id.item_6_layout) };
		
		holder.mImgs = new ImageView[] {
				(ImageView) convertView.findViewById(R.id.item_0_img),
				(ImageView) convertView.findViewById(R.id.item_1_img),
				(ImageView) convertView.findViewById(R.id.item_2_img),
				(ImageView) convertView.findViewById(R.id.item_3_img),
				(ImageView) convertView.findViewById(R.id.item_4_img),
				(ImageView) convertView.findViewById(R.id.item_5_img),
				(ImageView) convertView.findViewById(R.id.item_6_img) };
//		holder.mShadow = new ImageView[] {
//				(ImageView) convertView.findViewById(R.id.item_0_shadow),
//				(ImageView) convertView.findViewById(R.id.item_1_shadow),
//				(ImageView) convertView.findViewById(R.id.item_2_shadow),
//				(ImageView) convertView.findViewById(R.id.item_3_shadow),
//				(ImageView) convertView.findViewById(R.id.item_4_shadow),
//				(ImageView) convertView.findViewById(R.id.item_5_shadow),
//				(ImageView) convertView.findViewById(R.id.item_6_shadow) };
		holder.userHeader = new ImageView[] {
				(ImageView) convertView.findViewById(R.id.item_0_user_header),
				(ImageView) convertView.findViewById(R.id.item_1_user_header),
				(ImageView) convertView.findViewById(R.id.item_2_user_header),
				(ImageView) convertView.findViewById(R.id.item_3_user_header),
				(ImageView) convertView.findViewById(R.id.item_4_user_header),
				(ImageView) convertView.findViewById(R.id.item_5_user_header),
				(ImageView) convertView.findViewById(R.id.item_6_user_header) };
		
		holder.shopStoreAddr = new TextView[] {
				(TextView) convertView.findViewById(R.id.item_0_shopstore_place),
				(TextView) convertView.findViewById(R.id.item_1_shopstore_place),
				(TextView) convertView.findViewById(R.id.item_2_shopstore_place),
				(TextView) convertView.findViewById(R.id.item_3_shopstore_place),
				(TextView) convertView.findViewById(R.id.item_4_shopstore_place),
				(TextView) convertView.findViewById(R.id.item_5_shopstore_place),
				(TextView) convertView.findViewById(R.id.item_6_shopstore_place) };
		
		holder.shopStoreFromHere = new TextView[] {
				(TextView) convertView.findViewById(R.id.item_0_shopstore_from_here),
				(TextView) convertView.findViewById(R.id.item_1_shopstore_from_here),
				(TextView) convertView.findViewById(R.id.item_2_shopstore_from_here),
				(TextView) convertView.findViewById(R.id.item_3_shopstore_from_here),
				(TextView) convertView.findViewById(R.id.item_4_shopstore_from_here),
				(TextView) convertView.findViewById(R.id.item_5_shopstore_from_here),
				(TextView) convertView.findViewById(R.id.item_6_shopstore_from_here) };
		holder.locationView = (TextView) convertView.findViewById(R.id.location_lable);
		return holder;
	}

	public void setLocation(String locationName){
//		this.locationName = locationName;
		this.getAdapter().notifyDataSetChanged();
	}
	
	@Override
	protected void setDataWithHolder(CircumHolder holder, int position,
			boolean isMoving) {
		if(position == 0){
			for (int i = 0; i < ITEM_CHILDREN_COUNT; i++) {
				if(holder.subCells[i]!= null){
					holder.subCells[i].setVisibility(View.GONE);
				}
			}
//			TextView locaNameView = (TextView)holder.locationView.findViewById(R.id.location_lable);
			holder.locationView.setTextColor(Color.rgb(101, 96, 93));
			if(locationName != null){
				holder.locationView.setText(locationName);
			}else{
				holder.locationView.setText(mContext.getString(R.string.locationning));
			}
			holder.locationView.setVisibility(View.VISIBLE);
			
			return ;
		}
		holder.locationView.setVisibility(View.GONE);
			LocationUtil lu = new LocationUtil();
			for (int i = 0; i < ITEM_CHILDREN_COUNT; i++) {
				int currIndex = position * ITEM_CHILDREN_COUNT + i;
				// LogUtil.d(TAG, "setDataWithHolder. currIndex = " + currIndex);
				if (currIndex >= mDataList.size()) {
					// LogUtil.d(TAG, "setDataWithHolder. currIndex >= size");
					holder.mImgs[i].setVisibility(View.GONE);
//					holder.mShadow[i].setVisibility(View.GONE);
					holder.userHeader[i].setVisibility(View.GONE);
					holder.shopStoreAddr[i].setVisibility(View.GONE);
					holder.shopStoreFromHere[i].setVisibility(View.GONE);
					holder.subCells[i].setVisibility(View.GONE);
//					holder.subCells[i].setEnabled(false);
//					holder.subCells[i].setOnClickListener(null);
//					holder.mShadow[i].setOnClickListener(null);
//					holder.mShadow[i].setClickable(false);
//					LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//					convertView = inflater.inflate(R.layout.album_item_layout, null);
//					View view = inflater.inflate(resource, root)(R.layout.hello,null);
					//break;
				} else {
					holder.subCells[i].setVisibility(View.VISIBLE);
//					holder.subCells[i].setEnabled(true);
//					holder.mShadow[i].setClickable(false);
//					holder.mShadow[i].setOnClickListener(listener);
					holder.subCells[i].setOnClickListener(listener);
					holder.mImgs[i].setVisibility(View.VISIBLE);
//					holder.mShadow[i].setVisibility(View.VISIBLE);
					holder.userHeader[i].setVisibility(View.VISIBLE);
					holder.shopStoreAddr[i].setVisibility(View.VISIBLE);
					holder.shopStoreFromHere[i].setVisibility(View.VISIBLE);

					if (SystemConfig.SCREEN_WIDTH > 480) {
						LayoutParams params = holder.mImgs[i].getLayoutParams();
						params.width = IMG_REAL_WIDTH[i];
						params.height = IMG_REAL_HEIGHT[i];
						holder.mImgs[i].setLayoutParams(params);
						LayoutParams params2 = holder.mHeaderLayout[i].getLayoutParams();
						params2.width = IMG_REAL_WIDTH[i];
						holder.mHeaderLayout[i].setLayoutParams(params2);
//						holder.mShadow[i].setLayoutParams(params);
					}

					HomeData data = mDataList.get(currIndex);
					User user = null;
					if (data == null) {
						return;
					}
					if (data.originUser != null
							&& !"0".equals(data.PublicUser.feedType)) {
						user = data.originUser;
					} else if (data.PublicUser != null
							&& "0".equals(data.PublicUser.feedType)) {
						user = data.PublicUser;
					}
					if (user == null) {
						return;
					}
					// LogUtil.d(TAG, "getView. user. postId = " + user.postId +
					// ", like num = " + user.like_num);

					holder.mPostId[i] = user.postId;
					holder.userHeader[i].setImageBitmap(null);
					
					int sex = user.sex;
					int defaultHeaderRes = R.drawable.head_men;
					if (User.WOMEN  == sex) {
						defaultHeaderRes = R.drawable.head_women;
					}
					ImageUtil.setImageView(mContext, holder.userHeader[i],
							user.img_head_id,
							ImageUtil.HEADER_SIZE_BIG, ImageUtil.HEADER_SIZE_BIG,
							defaultHeaderRes, 16);
//					if(!TextUtils.isEmpty(user.img_head_id)){
//						
//					}else{
//						holder.userHeader[i].setImageDrawable(null);
//					}
					
					if(!TextUtils.isEmpty(user.place)){
						holder.shopStoreAddr[i].setText("在 "+user.place);
					}else{
						holder.shopStoreAddr[i].setVisibility(View.GONE);
					}
					
					if(!TextUtils.isEmpty(user.lat) && !TextUtils.isEmpty(user.lon)){
						try {
							double la = Double.parseDouble(user.lat);
							double lo = Double.parseDouble(user.lon);
							EarthPoint ep = lu.new EarthPoint(la, lo);
							int far = ((int)lu.getCurFarByEarthPoint(mContext, ep)/10)*10;
							holder.shopStoreFromHere[i].setText(mContext.getString(R.string.far_unit, far));
						} catch (NumberFormatException e) {
							holder.shopStoreFromHere[i].setVisibility(View.GONE);
							e.printStackTrace();
						}
					}
					
					
					// holder.mImg[i].setImageResource(R.drawable.feed_img_bg);
					holder.mImgs[i].setImageBitmap(null);
					if (user.contentImg != null && user.contentImg.length > 0) {
						if (!TextUtils.isEmpty(user.contentImg[0].img_content_id)) {
							ImageUtil.setImageView(mContext, holder.mImgs[i],
									user.contentImg[0].img_content_id,
									IMG_WIDTH[i], IMG_HEIGHT[i], -1);
							user.contentImgWidth = IMG_WIDTH[i];
							user.contentImgHeight = IMG_HEIGHT[i];
						}
					} else {
						holder.mImgs[i].setImageDrawable(null);
						// LogUtil.d(TAG, "setDataWithHolder. image is null");
					}
					// if (isToShare) {
//					holder.mShadow[i].setTag(data);
					holder.subCells[i].setTag(data);
					// } else {
					// holder.mShadow[i].setTag(new String[] { user.postId,
					// user.feedType });
					// }
					
				}
			}
	}

	private OnClickListener listener;

	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}
}
