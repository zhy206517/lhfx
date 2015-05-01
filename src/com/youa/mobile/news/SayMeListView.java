package com.youa.mobile.news;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.news.SayMeListView.SayMeHolder;
import com.youa.mobile.news.data.SayMeData;
import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.Tools;

public class SayMeListView extends BaseListView<SayMeHolder, List<SayMeData>> {

	private LayoutInflater mInflater;

	public class SayMeHolder extends BaseHolder {
		private ImageView mPublisUserImage;
		private TextView mPublishUserName;
		private TextView mPublishTime;
		private TextView mPublishContent;
		private TextView mSourceContent;
		private ImageView mSourceImage;

	}

	public SayMeListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		mInflater = LayoutInflater.from(listView.getContext());
	}

	@Override
	protected View createTemplateView(int pos) {
		return mInflater.inflate(R.layout.news_say_me_item, null);
	}

	@Override
	protected SayMeHolder getHolder(View convertView, int pos) {
		SayMeHolder holder = new SayMeHolder();
		holder.mPublisUserImage = (ImageView) convertView
				.findViewById(R.id.user_head);
		holder.mPublishUserName = (TextView) convertView
				.findViewById(R.id.user_name);
		holder.mPublishTime = (TextView) convertView.findViewById(R.id.time);
		holder.mPublishContent = (TextView) convertView
				.findViewById(R.id.publish_content);
		holder.mSourceContent = (TextView) convertView
				.findViewById(R.id.source_content);
		holder.mSourceImage = (ImageView) convertView
				.findViewById(R.id.source_image);
		return holder;
	}

	@Override
	protected void setDataWithHolder(SayMeHolder holder, int position,
			boolean isMoving) {
		final SayMeData data = mDataList.get(position);
		setUserInfo(data, holder, position, isMoving);
		holder.mPublishTime.setText(Tools.translateToString(Long.valueOf(data
				.getPublishTime()) * 1000));
		holder.mPublishTime.setVisibility(View.VISIBLE);
		if (!NewsUtil.isEmpty(data.getPublishContent())) {
			holder.mPublishContent.setVisibility(View.VISIBLE);
			holder.mPublishContent.setText(data.getPublishContent());
		} else {
			holder.mPublishContent.setVisibility(View.GONE);
		}
		int wi = ApplicationManager.getInstance().getWidth()
				- holder.mPublisUserImage.getLayoutParams().width - 20;
		Tools.setLimitText(holder.mPublishContent, wi, 3, 0);
		int size = 0;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// 背景86,图片80
			size = ImageUtil.FEED_SIZE_SMALL;
		} else {// 背景126,图片120
			size = ImageUtil.FEED_SIZE_BIG;
		}
		holder.mSourceContent.setText("");
		if (TextUtils.isEmpty(data.sourceImage)) {
			holder.mSourceContent.setVisibility(View.VISIBLE);
			Html.ImageGetter imageGetter = new Html.ImageGetter() {
				@Override
				public Drawable getDrawable(String source) {
					int id = Integer.parseInt(source);
					Drawable drawable = getContext().getResources()
							.getDrawable(id);
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					return drawable;
				}
			};
			if (!TextUtils.isEmpty(data.getSourceContent())) {
				holder.mSourceContent.setText(Html.fromHtml("<img src='"
						+ R.drawable.feed_beliked_icon + "'/>", imageGetter,
						null));

				holder.mSourceContent.append(data.getSourceContent());
				holder.mSourceImage.setVisibility(View.GONE);
			} else {
				holder.mSourceContent.setText(Html.fromHtml(
						"<img src='"
								+ R.drawable.feed_beliked_icon
								+ "'/>"
								+ "<br>"
								+ getContext().getString(
										R.string.feed_like_origin_deleted),
						imageGetter, null));
				holder.mSourceImage.setVisibility(View.GONE);
			}

		} else {
			holder.mSourceContent.setVisibility(View.GONE);
			holder.mSourceImage.setVisibility(View.VISIBLE);
			ImageUtil.setImageView(getContext(), holder.mSourceImage,
					data.sourceImage, size, size, -1);
		}

		Tools.setLimitText(holder.mSourceContent, wi, 3, 0);
		holder.mSourceContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartOriginContentActivity(data);
			}
		});
		holder.mSourceImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartOriginContentActivity(data);
			}
		});
		holder.mPublisUserImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startUserInfoActivity(data.getPublishUserId(),
						data.getPublishUserName());
			}
		});
	}

	private void StartOriginContentActivity(final SayMeData data) {
		if(TextUtils.isEmpty(data.getSourceContent())&&TextUtils.isEmpty(data.sourceImage)){
			Toast.makeText(this.getContext(), this.getContext().getResources().getString(R.string.feed_deleted), 3).show();
			return;
		}
		Bundle bundle = new Bundle();
		Class c = null;
		c = ContentOriginActivity.class;
		bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
				data.getSourceFeedId());// 源动态id
		// --------------------
		Intent intent = new Intent();
		intent.putExtras(bundle);
		intent.setClass(getContext(), c);
		getContext().startActivity(intent);
	}

	private void setUserInfo(SayMeData data, SayMeHolder holder, int position,
			boolean isMoving) {
		String username = data.getPublishUserName();
		if (username != null) {
			holder.mPublishUserName.setText(username);
			holder.mPublishUserName.setVisibility(View.VISIBLE);
		}
		// else {
		// holder.mPublishUserName.setText("" + position);
		// }

		ImageUtil.setHeaderImageView(getContext(), holder.mPublisUserImage,
				data.getPublishUserImage(), PersonalInformationData.SEX_MAN
						.equals(data.getSex()) ? R.drawable.head_men
						: R.drawable.head_women);
	}

	@Override
	protected void treateStopEvent(SayMeHolder holder, int position) {
	}
}
