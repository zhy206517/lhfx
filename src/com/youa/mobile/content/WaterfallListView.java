package com.youa.mobile.content;

import java.util.List;

import android.content.Context;
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
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.utils.LogUtil;

public class WaterfallListView extends AbstractListView<WaterfallHolder, List<HomeData>> {

	private static final int ITEM_CHILDREN_COUNT = 7;
	private static final int[] IMG_WIDTH = { 450, 220, 220, 220, 220, 220, 220 };
	private static final int[] IMG_HEIGHT = { 450, 180, 180, 360, 180, 360, 180 };
	private static int[] IMG_REAL_WIDTH;// = new int[7];
	private static int[] IMG_REAL_HEIGHT;// = new int[7];

	private LayoutInflater mInflater;
	private Context mContext;

	public WaterfallListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		mContext = listView.getContext();
		mInflater = LayoutInflater.from(mContext);
		if (SystemConfig.SCREEN_WIDTH > 480) {
			DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
			float dpDelta = metrics.densityDpi / 160f;
			float dp = SystemConfig.SCREEN_WIDTH / dpDelta;
			LogUtil.d(TAG, "WaterfallListView. delta = " + dpDelta + ", dp = " + dp);
			float borderDp = 10;
			float clearanceDp = 6;
			int dw = (int) ((dp - borderDp * 2) * dpDelta);
			int w = (int) ((dw - clearanceDp * dpDelta) / 2);
			int dh = dw * 339 / 450;
			int h = (int) (w * 202.5 / 220);
			LogUtil.d(TAG, "WaterfallListView. dw = " + dw + ", w = " + w + ", dh = " + dh + ", h = " + h);
			IMG_REAL_WIDTH = new int[] { dw, w, w, w, w, w, w };
			IMG_REAL_HEIGHT = new int[] { dh, h, h, dh, h, dh, h };
		}
	}

	@Override
	protected View createTemplateView() {
		return mInflater.inflate(R.layout.feed_waterfall_item, null);
	}

	@Override
	protected WaterfallHolder getHolder(View convertView) {
		WaterfallHolder holder = new WaterfallHolder();
		holder.mPostId = new String[ITEM_CHILDREN_COUNT];
		holder.isContentImgGet = new boolean[ITEM_CHILDREN_COUNT];
		holder.mItemLayout = new View[] { convertView.findViewById(R.id.waterfall_item_0),
				convertView.findViewById(R.id.waterfall_item_1), convertView.findViewById(R.id.waterfall_item_2),
				convertView.findViewById(R.id.waterfall_item_3), convertView.findViewById(R.id.waterfall_item_4),
				convertView.findViewById(R.id.waterfall_item_5), convertView.findViewById(R.id.waterfall_item_6) };
		holder.mImg = new ImageView[] { (ImageView) convertView.findViewById(R.id.item_0_img),
				(ImageView) convertView.findViewById(R.id.item_1_img),
				(ImageView) convertView.findViewById(R.id.item_2_img),
				(ImageView) convertView.findViewById(R.id.item_3_img),
				(ImageView) convertView.findViewById(R.id.item_4_img),
				(ImageView) convertView.findViewById(R.id.item_5_img),
				(ImageView) convertView.findViewById(R.id.item_6_img) };
		holder.mLikeView = new TextView[] { (TextView) convertView.findViewById(R.id.item_0_like_num),
				(TextView) convertView.findViewById(R.id.item_1_like_num),
				(TextView) convertView.findViewById(R.id.item_2_like_num),
				(TextView) convertView.findViewById(R.id.item_3_like_num),
				(TextView) convertView.findViewById(R.id.item_4_like_num),
				(TextView) convertView.findViewById(R.id.item_5_like_num),
				(TextView) convertView.findViewById(R.id.item_6_like_num) };
		holder.mContent = new TextView[] { (TextView) convertView.findViewById(R.id.item_0_content),
				(TextView) convertView.findViewById(R.id.item_1_content),
				(TextView) convertView.findViewById(R.id.item_2_content),
				(TextView) convertView.findViewById(R.id.item_3_content),
				(TextView) convertView.findViewById(R.id.item_4_content),
				(TextView) convertView.findViewById(R.id.item_5_content),
				(TextView) convertView.findViewById(R.id.item_6_content) };
		return holder;
	}

	@Override
	protected void setDataWithHolder(WaterfallHolder holder, int position, boolean isMoving) {
		// LogUtil.d(TAG, "setDataWithHolder. position = " + position +
		// ", isMoving = " + isMoving);
		for (int i = 0; i < ITEM_CHILDREN_COUNT; i++) {
			int currIndex = position * ITEM_CHILDREN_COUNT + i;
			// LogUtil.d(TAG, "setDataWithHolder. currIndex = " + currIndex);
			if (currIndex >= mDataList.size()) {
				// LogUtil.d(TAG, "setDataWithHolder. currIndex >= size");
				// holder.mImg[i].setVisibility(View.GONE);
				// holder.mContent[i].setVisibility(View.GONE);
				// holder.mLikeView[i].setVisibility(View.GONE);
				holder.mItemLayout[i].setVisibility(View.GONE);
			} else {
				// holder.mImg[i].setVisibility(View.VISIBLE);
				// holder.mContent[i].setVisibility(View.VISIBLE);
				// holder.mLikeView[i].setVisibility(View.VISIBLE);
				holder.mItemLayout[i].setVisibility(View.VISIBLE);

				if (SystemConfig.SCREEN_WIDTH > 480) {
					LayoutParams params = holder.mImg[i].getLayoutParams();
					params.width = IMG_REAL_WIDTH[i];
					params.height = IMG_REAL_HEIGHT[i];
					holder.mImg[i].setLayoutParams(params);
					LayoutParams params2 = holder.mContent[i].getLayoutParams();
					params2.width = IMG_REAL_WIDTH[i];
					holder.mContent[i].setLayoutParams(params2);
				}

				HomeData data = mDataList.get(currIndex);
				User user = null;
				if (data == null) {
					return;
				}
				if (data.originUser != null && !"0".equals(data.PublicUser.feedType)) {
					user = data.originUser;
				} else if (data.PublicUser != null && "0".equals(data.PublicUser.feedType)) {
					user = data.PublicUser;
				}
				if (user == null) {
					return;
				}
				// LogUtil.d(TAG, "getView. user. postId = " + user.postId +
				// ", like num = " + user.like_num);

				holder.mPostId[i] = user.postId;
				holder.mPostId[i] = user.postId;
				ContentData[] datas = user.contents;
				StringBuffer str = new StringBuffer();
				if (datas != null) {
					for (ContentData d : datas) {
						str.append(d.type == ContentData.TYPE_TEXT ? d.str : "");
					}
				}
				holder.mContent[i].setText(str.toString().replaceAll("\n", ""));
				// LogUtil.d(TAG, "setDataWithHolder. position = " + position +
				// ", i = " + i + ", text = "
				// + holder.mContent[i].getText());
				if (!TextUtils.isEmpty(user.like_num)) {
					holder.mLikeView[i].setVisibility(View.VISIBLE);
					holder.mLikeView[i].setText(user.like_num);
				} else {
					holder.mLikeView[i].setVisibility(View.GONE);
				}
				holder.mImg[i].setImageBitmap(null);
				if (user.contentImg != null && user.contentImg.length > 0) {
					if (!TextUtils.isEmpty(user.contentImg[0].img_content_id)) {
						ImageUtil.setImageView(mContext, holder.mImg[i], user.contentImg[0].img_content_id,
								IMG_WIDTH[i], IMG_HEIGHT[i], -1);
						user.contentImgWidth = IMG_WIDTH[i];
						user.contentImgHeight = IMG_HEIGHT[i];
					}
				} else {
					holder.mImg[i].setImageDrawable(null);
					// LogUtil.d(TAG, "setDataWithHolder. image is null");
				}
				holder.mItemLayout[i].setTag(data);
				holder.mItemLayout[i].setOnClickListener(listener);
			}
		}
	}

	private OnClickListener listener;

	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}
}
