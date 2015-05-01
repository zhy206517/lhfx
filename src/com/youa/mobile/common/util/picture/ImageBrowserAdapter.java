package com.youa.mobile.common.util.picture;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;

public class ImageBrowserAdapter extends BaseAdapter {

	private Context mContext;
	private List<ImageData> mDataList;

	public ImageBrowserAdapter(Context context, List<ImageData> list) {
		mContext = context;
		mDataList = list;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String id = mDataList.get(position).id;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.view_image_browser_item, null);
		}
		int size = 0;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// 背景86,图片80
			size = ImageUtil.CONTENT_SIZE_SMALL;
		} else {// 背景126,图片120
			size = ImageUtil.CONTENT_SIZE_BIG;
		}
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		ImageUtil.setImageView(
				mContext,
				image,
				id,
				size,
				size,
				-1);
		return convertView;
	}

}
