package com.youa.mobile.jingxuan;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.jingxuan.data.ClassifyTagInfoData;

public class JingxuanGridLayout extends ViewGroup {

	// private static final String TAG = "JingxuanGridLayout";
	// private static final int PADDING = 10;
	private static final int SPACING = 7;
	private static final int TITLE_HEIGHT = 35;

	private Context mContext;
	private float mDpDelta = 0;
	private LayoutInflater mInflater;
	private int childW;

	public JingxuanGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mDpDelta = metrics.densityDpi / 160f;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = getPaddingTop();
		int space = (int) (SPACING * mDpDelta);
		int titleH = (int) (TITLE_HEIGHT * mDpDelta);
		childW = (width - getPaddingLeft() - getPaddingRight() - space) / 2;
		int childWMS = MeasureSpec.makeMeasureSpec(childW, MeasureSpec.EXACTLY);
		int childHMS = MeasureSpec.makeMeasureSpec(childW + titleH, MeasureSpec.EXACTLY);
		int count = getChildCount();
		// LogUtil.d(TAG, "onMeasure. width = " + width + ", child count = " +
		// count + ", child w = " + childW
		// + ", child w m s = " + childWMS);
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				view.measure(childWMS, childHMS);
				if (i % 2 == 0) {
					height += view.getMeasuredHeight() + space;
				}
			}
		}
		setMeasuredDimension(width, height+ space);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		int space = (int) (SPACING * mDpDelta);
		int xPos = getPaddingLeft();
		int yPos = getPaddingTop();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				if (i % 2 == 0) {
					xPos = getPaddingLeft();
					view.layout(xPos, yPos, xPos + view.getMeasuredWidth(), yPos + view.getMeasuredHeight());
					// LogUtil.d(TAG, "onLayout. x = " + xPos + ", y = " + yPos
					// + ", w = " + view.getMeasuredWidth()
					// + ", h = " + view.getMeasuredHeight());
				} else {
					xPos += view.getMeasuredWidth() + space;
					view.layout(xPos, yPos, xPos + view.getMeasuredWidth(), yPos + view.getMeasuredHeight());
					// LogUtil.d(TAG, "onLayout. x = " + xPos + ", y = " + yPos
					// + ", w = " + view.getMeasuredWidth()
					// + ", h = " + view.getMeasuredHeight());
					yPos += view.getMeasuredHeight() + space;
				}
			}
		}
	}

	public void initViews(List<ClassifyTagInfoData> list) {
		removeAllViews();
		for (final ClassifyTagInfoData info : list) {
			View item = mInflater.inflate(R.layout.tag_class_info_layout, null);
			TagPartHolder holder;
			holder = new TagPartHolder();
			holder.mTagItemArea1 = (RelativeLayout) item.findViewById(R.id.tag1);
			holder.mTagItemArea2 = (RelativeLayout) item.findViewById(R.id.tag2);
			holder.mTagItemArea3 = (RelativeLayout) item.findViewById(R.id.tag3);
			holder.mTagItemArea4 = (RelativeLayout) item.findViewById(R.id.tag4);
			item.setTag(holder);

			int size = 0;
			if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
				// 背景86,图片80
				size = ImageUtil.FEED_SIZE_BIG;
			} else {// 背景126,图片120
				size = ImageUtil.FEED_SIZE_BIG;
			}
			holder.mTagItemArea4.setVisibility(View.INVISIBLE);
			holder.mTagItemArea3.setVisibility(View.INVISIBLE);
			holder.mTagItemArea2.setVisibility(View.INVISIBLE);
			holder.mTagItemArea1.setVisibility(View.INVISIBLE);
			if (info != null) {
				((TextView) item.findViewById(R.id.class_name)).setText(String.format(
						mContext.getString(R.string.label_all), info.mName));
				if (info.mFeedData.length > 0) {
					ImageUtil.setImageView(mContext, (ImageView) item.findViewById(R.id.tag1_img),
							info.mFeedData[0].mImageid, size, size, -1);
					((TextView) item.findViewById(R.id.tag1_content)).setText(info.mFeedData[0].mTagName);
					holder.mTagItemArea1.setVisibility(View.VISIBLE);
				}

				if (info.mFeedData.length > 1) {
					ImageUtil.setImageView(mContext, (ImageView) item.findViewById(R.id.tag2_img),
							info.mFeedData[1].mImageid, size, size, -1);
					((TextView) item.findViewById(R.id.tag2_content)).setText(info.mFeedData[1].mTagName);
					holder.mTagItemArea2.setVisibility(View.VISIBLE);
				}
				if (info.mFeedData.length > 2) {
					ImageUtil.setImageView(mContext, (ImageView) item.findViewById(R.id.tag3_img),
							info.mFeedData[2].mImageid, size, size, -1);
					((TextView) item.findViewById(R.id.tag3_content)).setText(info.mFeedData[2].mTagName);
					holder.mTagItemArea3.setVisibility(View.VISIBLE);
				}
				if (info.mFeedData.length > 3) {
					ImageUtil.setImageView(mContext, (ImageView) item.findViewById(R.id.tag4_img),
							info.mFeedData[3].mImageid, size, size, -1);
					((TextView) item.findViewById(R.id.tag4_content)).setText(info.mFeedData[3].mTagName);
					holder.mTagItemArea4.setVisibility(View.VISIBLE);
				}
				item.findViewById(R.id.class_titile).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(mContext, TagClassFeedPage.class);
						i.putExtra(TagClassFeedPage.TYPE_KEY, TagClassFeedPage.TYPE_CLASSIFY);
						i.putExtra(TagClassFeedPage.ID_KEY, info.mClsid);
						i.putExtra(TagClassFeedPage.TITLE_NAME, info.mName);
						mContext.startActivity(i);
					}
				});
				holder.mTagItemArea1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(mContext, TagClassFeedPage.class);
						i.putExtra(TagClassFeedPage.TYPE_KEY, TagClassFeedPage.TYPE_TAG);
						i.putExtra(TagClassFeedPage.ID_KEY, info.mClsid);
						i.putExtra(TagClassFeedPage.SUB_ID_KEY, info.mFeedData[0].mTagId);
						i.putExtra(TagClassFeedPage.TITLE_NAME, info.mName);
						i.putExtra(TagClassFeedPage.TAG_NAME, info.mFeedData[0].mTagName);
						mContext.startActivity(i);

					}
				});
				holder.mTagItemArea2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(mContext, TagClassFeedPage.class);
						i.putExtra(TagClassFeedPage.TYPE_KEY, TagClassFeedPage.TYPE_TAG);
						i.putExtra(TagClassFeedPage.ID_KEY, info.mClsid);
						i.putExtra(TagClassFeedPage.SUB_ID_KEY, info.mFeedData[1].mTagId);
						i.putExtra(TagClassFeedPage.TITLE_NAME, info.mName);
						i.putExtra(TagClassFeedPage.TAG_NAME, info.mFeedData[1].mTagName);
						mContext.startActivity(i);

					}
				});
				holder.mTagItemArea3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(mContext, TagClassFeedPage.class);
						i.putExtra(TagClassFeedPage.TYPE_KEY, TagClassFeedPage.TYPE_TAG);
						i.putExtra(TagClassFeedPage.ID_KEY, info.mClsid);
						i.putExtra(TagClassFeedPage.SUB_ID_KEY, info.mFeedData[2].mTagId);
						i.putExtra(TagClassFeedPage.TITLE_NAME, info.mName);
						i.putExtra(TagClassFeedPage.TAG_NAME, info.mFeedData[2].mTagName);
						mContext.startActivity(i);

					}
				});
				holder.mTagItemArea4.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(mContext, TagClassFeedPage.class);
						i.putExtra(TagClassFeedPage.TYPE_KEY, TagClassFeedPage.TYPE_TAG);
						i.putExtra(TagClassFeedPage.ID_KEY, info.mClsid);
						i.putExtra(TagClassFeedPage.SUB_ID_KEY, info.mFeedData[3].mTagId);
						i.putExtra(TagClassFeedPage.TITLE_NAME, info.mName);
						i.putExtra(TagClassFeedPage.TAG_NAME, info.mFeedData[3].mTagName);
						mContext.startActivity(i);

					}
				});
			}
			addView(item);
		}
	}

	private class TagPartHolder {
		RelativeLayout mTagItemArea1;
		RelativeLayout mTagItemArea2;
		RelativeLayout mTagItemArea3;
		RelativeLayout mTagItemArea4;
	}
}
