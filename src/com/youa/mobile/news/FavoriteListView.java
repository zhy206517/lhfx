package com.youa.mobile.news;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.news.FavoriteListView.FavoriteHolder;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.news.data.FavoriteData;
import com.youa.mobile.news.util.NewsUtil;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.Tools;

public class FavoriteListView extends
		BaseListView<FavoriteHolder, List<FavoriteData>> {

	private LayoutInflater mInflater;

	public class FavoriteHolder extends BaseHolder {
		private ImageView mUserHead;
		private TextView mUserName;
		private TextView mTimeView;
		private TextView mSourceContent;
		private ImageView mSourceImage;

	}

	public FavoriteListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		mInflater = LayoutInflater.from(listView.getContext());
	}

	@Override
	protected View createTemplateView(int pos) {
		return mInflater.inflate(R.layout.news_favorite_item, null);
	}

	@Override
	protected FavoriteHolder getHolder(View convertView,int pos) {
		FavoriteHolder holder = new FavoriteHolder();
		holder.mUserHead = (ImageView) convertView.findViewById(R.id.user_head);
		holder.mUserName = (TextView) convertView.findViewById(R.id.user_name);
		holder.mTimeView = (TextView) convertView.findViewById(R.id.time);
		holder.mSourceContent = (TextView) convertView
				.findViewById(R.id.source_content);
		holder.mSourceImage = (ImageView) convertView
				.findViewById(R.id.source_image);
		return holder;
	}

	@Override
	protected void setDataWithHolder(FavoriteHolder holder, int position,
			boolean isMoving) {
		final FavoriteData data = mDataList.get(position);
		ImageUtil.setHeaderImageView(getContext(), holder.mUserHead,
				data.headImgId, PersonalInformationData.SEX_MAN
						.equals(data.sex) ? R.drawable.head_men
						: R.drawable.head_women);
		setUserInfo(data, holder, position, isMoving);
		holder.mTimeView.setText(Tools.translateToString(Long
				.valueOf(data.likeTime) * 1000));
		int size = 0;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// 背景86,图片80
			size = ImageUtil.FEED_SIZE_SMALL;
		} else {// 背景126,图片120
			size = ImageUtil.FEED_SIZE_BIG;
		}
		// data.sourceImage="52c490c024b0c8cd57c5a4e1";
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
			if (!TextUtils.isEmpty(data.sourceContent)) {
				holder.mSourceContent.setText(Html.fromHtml("<img src='"
						+ R.drawable.feed_beliked_icon + "'/>", imageGetter,
						null));

				holder.mSourceContent.append(data.sourceContent);
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
		int wi = ApplicationManager.getInstance().getWidth()
				- (ApplicationManager.getInstance().getDensityDpi() < 240 ? 48
						: 72);
		// Tools.setLimitText(holder.mSourceContent, wi, 2, 0);
		// ----------------
		holder.mUserHead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ApplicationManager.getInstance().isLogin()) {
					Intent i = new Intent(getContext(), LoginPage.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					getContext().startActivity(i);
					return;
				}
				Intent intent = new Intent(getContext(),
						PersonnalInforPage.class);
				intent.putExtra(PersonnalInforPage.KEY_USER_ID, data.likeUserid);
				getContext().startActivity(intent);
			}
		});
		holder.mSourceContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startOriginFeedActivity(data);
			}
		});
		holder.mSourceImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startOriginFeedActivity(data);
			}

		});
	}

	private void startOriginFeedActivity(final FavoriteData data) {
		Bundle bundle = new Bundle();
		Class c = null;
		c = ContentOriginActivity.class;
		bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
				data.sourceFeedId);// 源动态id
		// --------------------
		Intent intent = new Intent();
		intent.putExtras(bundle);
		intent.setClass(getContext(), c);
		getContext().startActivity(intent);
	}

	private void setUserInfo(FavoriteData data, FavoriteHolder holder,
			int position, boolean isMoving) {
		// if(data.getLikeCount()!=null){
		// holder.mlikedCount.setText(data.getLikeCount());
		// holder.mlikedCount.setVisibilityd(View.VISIBLE);
		// }else{
		// holder.mlikedCount.setText("0");
		// }
		// if (data.getLikeUserName() != null) {
		holder.mUserName.setText(data.userName);
		// } else {
		// holder.mUserNameView.setText("0");
		// }
	}

	@Override
	protected void treateStopEvent(FavoriteHolder holder, int position) {
	}
}
