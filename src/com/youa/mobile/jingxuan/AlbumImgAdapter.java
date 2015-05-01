package com.youa.mobile.jingxuan;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.jingxuan.data.AlbumItemData;

public class AlbumImgAdapter extends BaseAdapter {

	private Context context;
	private int count;
	private List<AlbumItemData> list = new ArrayList<AlbumItemData>();
	private int[] picIndexArr = { R.drawable.album_pic_index1,
			R.drawable.album_pic_index2, R.drawable.album_pic_index3,
			R.drawable.album_pic_index4, R.drawable.album_pic_index5 };

	private final class ViewHolder {
		public ImageView albumItemImage;
		public ImageView albumItemImageIndex0;
		public ImageView albumItemImageIndex1;
		public ImageView albumItemImageIndex2;
		public ImageView albumItemImageIndex3;
		public ImageView albumItemImageIndex4;

	}

	public AlbumImgAdapter(Context context, List<AlbumItemData> data) {
		this.context = context;
		this.list = data;
		count = data.size();
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return list.get(position);
		} else {
			return list.get(position % count);
		}

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (count != 0) {
			position = position % count;
		}
		if (list == null || list.size() == 0) {
			return convertView;
		}
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.album_item_layout, null);
			holder = new ViewHolder();
			holder.albumItemImage = (ImageView) convertView
					.findViewById(R.id.album_img_id);
			holder.albumItemImageIndex0 = (ImageView) convertView
					.findViewById(R.id.album_img_index0);
			holder.albumItemImageIndex1 = (ImageView) convertView
					.findViewById(R.id.album_img_index1);
			holder.albumItemImageIndex2 = (ImageView) convertView
					.findViewById(R.id.album_img_index2);
			holder.albumItemImageIndex3 = (ImageView) convertView
					.findViewById(R.id.album_img_index3);
			holder.albumItemImageIndex4 = (ImageView) convertView
					.findViewById(R.id.album_img_index4);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AlbumItemData data = list.get(position);
		int size = 0;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// 背景86,图片80
			size = ImageUtil.CONTENT_SIZE_BIG;
		} else {// 背景126,图片120
			size = ImageUtil.CONTENT_SIZE_BIG;
		}
		ImageUtil.setImageView(context, holder.albumItemImage, data.image,
				size, size, -1);
		holder.albumItemImageIndex0
				.setImageResource(R.drawable.pic_index_normal);
		holder.albumItemImageIndex1
				.setImageResource(R.drawable.pic_index_normal);
		holder.albumItemImageIndex2
				.setImageResource(R.drawable.pic_index_normal);
		holder.albumItemImageIndex3
				.setImageResource(R.drawable.pic_index_normal);
		holder.albumItemImageIndex4
				.setImageResource(R.drawable.pic_index_normal);
		switch (position) {
		case 0:
			holder.albumItemImageIndex0
					.setImageResource(R.drawable.pic_index_selector);
			break;
		case 1:
			holder.albumItemImageIndex1
					.setImageResource(R.drawable.pic_index_selector);
			break;
		case 2:
			holder.albumItemImageIndex2
					.setImageResource(R.drawable.pic_index_selector);
			break;
		case 3:
			holder.albumItemImageIndex3
					.setImageResource(R.drawable.pic_index_selector);
			break;
		case 4:
			holder.albumItemImageIndex4
					.setImageResource(R.drawable.pic_index_selector);
			break;
		}

		return convertView;
	}
}
