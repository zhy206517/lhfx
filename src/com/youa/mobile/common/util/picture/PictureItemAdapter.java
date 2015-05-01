package com.youa.mobile.common.util.picture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.parser.ParserContent;

public class PictureItemAdapter extends BaseAdapter {
	private static final String TAG = "PictureItemAdapter";
	private Context context;
	private List<ImageData> list = new ArrayList<ImageData>();
	public boolean isIn = true;
	public Bitmap bitmap;
	private static Map<Integer, Bitmap> imageMap = new HashMap<Integer, Bitmap>();

	class ViewHolder {
		public View loadingView;
		public ImageView mImageView;
		public TextView mImgDesc;
	}

	public PictureItemAdapter(Context context, List<ImageData> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size() <= 1 ? list.size() : Integer.MAX_VALUE;// list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.view_picture_gallery_item,
					null);
			holder.loadingView = convertView
					.findViewById(R.id.img_load_progressBar);
			holder.mImageView = (ImageView) convertView
					.findViewById(R.id.view_picture_item_img);
			holder.mImgDesc = (TextView) convertView
					.findViewById(R.id.img_desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position < 0) {
			position += list.size();
		}
		ImageData img = list.get(position % list.size());
		if (null != img) {

			if (null != img.id && !"".equals(img.id)) {

				// 注释部分为服务器支持按图片尺寸获取图片后的代码，现在暂时用注释后面的代码
				int width = 0, heigth = 0;
				int sreenDpi = ApplicationManager.getInstance().getDensityDpi();
				if (sreenDpi <= 160) {//
					width = 320;
					heigth = 1600;
				} else if (sreenDpi > 160 && sreenDpi <= 240) {// 背景126,图片120
					width = 480;
					heigth = 1600;
				} else if (sreenDpi > 240) {
					width = 600;
					heigth = 1600;
				}
				ImageUtil.setImageView(context, holder.mImageView, img.id,
						width, heigth, img.angle, holder.loadingView);
				BitmapDrawable bitmapDra = (BitmapDrawable) holder.mImageView
						.getDrawable();
				if (bitmapDra != null) {
					this.bitmap = bitmapDra.getBitmap();
					imageMap.put(position, this.bitmap);
				}
				// ImageUtil.setImageViewOriginal(context, mImageView, img.id,
				// loadingView);
			} else if (null != img.path && !"".equals(img.path)) {
				try {
					Bitmap bitmap = ImageUtil.decode(img.path, img.angle);
					imageMap.put(position, bitmap);
					holder.mImageView.setImageBitmap(bitmap);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					showToast(context
							.getString(R.string.viewpic_invalited_picture));
				}
			} else {
				showToast(context.getString(R.string.viewpic_invalited_picture));
			}
			if (null != img.desc && !"".equals(img.desc)) {
				ContentData[] contents = ParserContent.getParser().parser(
						img.desc.toCharArray());
				StringBuffer sb = new StringBuffer();
				for (ContentData d : contents) {
					sb.append(d.str);
				}
				holder.mImgDesc.setText(sb.toString());
				holder.mImgDesc.setVisibility(View.VISIBLE);
				setMImageAnimation(holder.mImgDesc);
			} else {
				holder.mImgDesc.setVisibility(View.GONE);
			}
		} else {
			showToast(context.getString(R.string.viewpic_null_picture,
					position + 1));
		}
		return convertView;
	}

	protected void showToast(String res) {
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}

	protected void setMImageAnimation(View v) {
		TranslateAnimation tranAnim;
		// float height = v.getHeight();
		if (!isIn) {
			AnimationSet mDescOutAnimation = new AnimationSet(true);
			tranAnim = new TranslateAnimation(0, 0, 0, 0);
			AlphaAnimation alphAnim = new AlphaAnimation(1, 0);
			tranAnim.setDuration(ViewPicturePage.DEFAULT_TIME);
			alphAnim.setDuration(ViewPicturePage.DEFAULT_TIME);
			mDescOutAnimation.setFillAfter(true);
			mDescOutAnimation.addAnimation(tranAnim);
			mDescOutAnimation.addAnimation(alphAnim);
			v.startAnimation(mDescOutAnimation);
		} else {
			AnimationSet mDescInAnimation = new AnimationSet(true);
			tranAnim = new TranslateAnimation(0, 0, 0, 0);
			AlphaAnimation alphAnim = new AlphaAnimation(0, 1);
			tranAnim.setDuration(ViewPicturePage.DEFAULT_TIME);
			alphAnim.setDuration(ViewPicturePage.DEFAULT_TIME);
			mDescInAnimation.setFillAfter(true);
			mDescInAnimation.addAnimation(tranAnim);
			mDescInAnimation.addAnimation(alphAnim);
			v.startAnimation(mDescInAnimation);
		}
	}

	public void destory() {
		Log.d(TAG, "destory");
		for (Map.Entry<Integer, Bitmap> entry : imageMap.entrySet()) {
			Bitmap bitmap = entry.getValue();
			bitmap.recycle();
		}
		imageMap.clear();
	}
}
