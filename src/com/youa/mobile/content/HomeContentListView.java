package com.youa.mobile.content;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.util.picture.UserImageLoader;
import com.youa.mobile.common.util.picture.UserImageLoader.OnImageLoadListener;
import com.youa.mobile.content.HomeContentListView.ContentHolder;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.input.CommentPage;
import com.youa.mobile.ui.base.BaseHolder;

public class HomeContentListView extends
		BaseListView<ContentHolder, List<FeedContentCommentData>> {

	public class ContentHolder extends BaseHolder {
		private ImageView headView;
		private TextView nameView;
		private TextView timeView;
		private TextView publicContentView;
		private ImageView replyView;

		private boolean isHeadImgGet;

	}

	private Handler mHander = new Handler();
	private LayoutInflater mInflater;
	private Context context;

	public HomeContentListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		context = listView.getContext();
		mInflater = LayoutInflater.from(context);
	}

	@Override
	protected View createTemplateView(int pos) {
		return mInflater.inflate(R.layout.feed_content_comment_item, null);
	}

	@Override
	protected ContentHolder getHolder(View convertView,int pos) {
		ContentHolder holder = new ContentHolder();
		holder.headView = (ImageView) convertView.findViewById(R.id.user_head);
		holder.nameView = (TextView) convertView.findViewById(R.id.user_name);
		holder.timeView = (TextView) convertView.findViewById(R.id.time);
		holder.publicContentView = (TextView) convertView
				.findViewById(R.id.content);
		holder.replyView = (ImageView) convertView.findViewById(R.id.reply);
		holder.replyView.setOnTouchListener(new ReplyOnTouchListener());
		return holder;
	}

	@Override
	protected void setDataWithHolder(ContentHolder holder, int position,
			boolean isMoving) {
		FeedContentCommentData data = mDataList.get(position);
		if (data == null) {
			return;
		}
		if (data.public_name != null) {
			holder.nameView.setText(data.public_name);
			holder.nameView.setVisibility(View.VISIBLE);
		} else {
			holder.nameView.setVisibility(View.GONE);
		}
		if (data.public_time != null) {
			holder.timeView.setText(data.public_time);
			holder.timeView.setVisibility(View.VISIBLE);
		} else {
			holder.timeView.setVisibility(View.GONE);
		}
		if (data.public_content != null) {
			holder.publicContentView.setText(data.public_content);
			holder.publicContentView.setVisibility(View.VISIBLE);
		} else {
			holder.publicContentView.setVisibility(View.GONE);
		}
		setContentImg(data, holder);
		if (!isMoving) {
			getHeaderImg(holder, position);
		}
	}

	// 这块设上后，则不会走stopevent事件
	private void setContentImg(FeedContentCommentData data, ContentHolder holder) {
		// 从缓存去，有则设上;没有则，去取
		holder.setNeedTreateScroolStopEvent(true);
		data.isHeadImgNeedGet = true;
		holder.isHeadImgGet = false;
	}

	@Override
	protected void treateStopEvent(ContentHolder holder, int position) {
		getHeaderImg(holder, position);
	}

	private void getHeaderImg(ContentHolder holder, int position) {
		List<FeedContentCommentData> list = getData();
		FeedContentCommentData data = list.get(position);
		if (data == null || holder == null || holder.isHeadImgGet
				|| !data.isHeadImgNeedGet || data.public_img_head_id == null
				|| "".equals(data.public_img_head_id)) {
			return;
		}
		holder.isHeadImgGet = true;
		data.isHeadImgNeedGet = false;
		getPhoto(holder, data.public_img_head_id, 40);
	}

	private void getPhoto(final ContentHolder holder, String imgId, int size) {
		if (imgId == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("http://img.youa.com/src_new/");
		sb.append(imgId);
		sb.append("-");
		sb.append(size);
		sb.append("-");
		sb.append(size);
		sb.append("-1");
		UserImageLoader.getInstance().loadDrawable(context, sb.toString(),
				new OnImageLoadListener() {
					@Override
					public void onImageLoaded(Drawable imageDrawable,
							String imageUrl) {
						updateHead(holder, imageDrawable);
					}

					@Override
					public void onImageLoaded(Bitmap bitmap) {
						// TODO Auto-generated method stub
						
					}
				});
	}

	private void updateHead(ContentHolder holder, final Drawable imageDrawable) {
		final ImageView headView = holder.headView;
		mHander.post(new Runnable() {
			@Override
			public void run() {
				headView.setImageDrawable(imageDrawable);
			}
		});
	}

	// ------------------------------------------------------------------------
	private class ReplyOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Bundle bundle = new Bundle();
				bundle.putString(ContentOriginActivity.ORIGIN_UESER_ID, "");
				bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID, "");
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(context, CommentPage.class);
				context.startActivity(intent);
				break;
			}
			return true;
		}
	}
}
