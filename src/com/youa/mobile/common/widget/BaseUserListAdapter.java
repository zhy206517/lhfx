package com.youa.mobile.common.widget;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.common.util.picture.UserImageLoader;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.ui.base.BaseHolder;

public  class BaseUserListAdapter <L extends List<UserData>> extends FlingAdapter<UserData> {
	public class BaseUserHolder extends BaseHolder {
		public ImageView headerImageView;
		public TextView nameView;

	}
	
	public static final String TAG= "BaseUserListAdapter";
	private static final int HOLDER_KEY = 100000001;
	private LayoutInflater mInflater;
	private Context mContext;
	private int mItemResId = -1;
	public BaseUserListAdapter(Context context, int itemResId) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mItemResId = itemResId;
	}
	public BaseUserListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}
	
		@Override
		public int getCount() {
			if (getDataList() == null) {
				return 0;
			}
			return getDataList().size();
		}

		@Override
		public Object getItem(int position) {
			if (getDataList() == null) {
				return null;
			}
			return getDataList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			BaseUserHolder holder = null;
			if (convertView == null) {
				convertView = createTemplateView();
				holder = createHolder(convertView);
				convertView.setTag(HOLDER_KEY, holder);
			}
			holder = (BaseUserHolder) convertView.getTag(HOLDER_KEY);
			UserData data = getDataList().get(position);
			setUserInfo(data, holder, position, false);		
			String sex = data.getSexInt();
			int defaultHeaderRes = R.drawable.head_men;
			if(PersonalInformationData.SEX_WOMAN.equals(sex)) {
				defaultHeaderRes = R.drawable.head_women;
			}
			
			// 设置图片
			ImageUtil.setHeaderImageView(
					mContext, 
					holder.headerImageView, 
					data.getHeaderImgid(),
					defaultHeaderRes);
//			setImage(holder.headerImageView, ImageUtil.getImageURL(data.getHeaderImgid()));
			return convertView;
		}

		protected BaseUserHolder createHolder(View convertView) {
			BaseUserHolder holder = new BaseUserHolder();
			holder.headerImageView = (ImageView) convertView.findViewById(R.id.user_head);
			holder.nameView = (TextView) convertView.findViewById(R.id.user_name);
			return holder;
		}

		
		protected View createTemplateView() {
			if(mItemResId != -1) {
				return mInflater.inflate(mItemResId, null);
			} else {
				return mInflater.inflate(R.layout.information_attent_list_item, null);
			}
			
		}
		
		private void setUserInfo(
				UserData data, 
				BaseUserHolder holder, 
				int position,
				boolean isMoving) {
			if (data.getUserName() != null) {
				holder.nameView.setText(data.getUserName());
				holder.nameView.setVisibility(View.VISIBLE);
			} else {
				holder.nameView.setText("" + position);
			}
		}
		
}
