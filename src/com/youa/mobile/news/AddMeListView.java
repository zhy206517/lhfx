//package com.youa.mobile.news;
//
//import java.util.List;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.style.ForegroundColorSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import com.youa.mobile.R;
//import com.youa.mobile.common.base.BaseListView;
//import com.youa.mobile.common.util.picture.ImageUtil;
//import com.youa.mobile.news.data.AddMeData;
//import com.youa.mobile.news.util.NewsUtil;
//import com.youa.mobile.ui.base.BaseHolder;
//import com.youa.mobile.utils.Tools;
//import com.youa.mobile.news.AddMeListView.AddMeHolder;
//
//public class AddMeListView extends BaseListView<AddMeHolder, List<AddMeData>> {
//
//	private LayoutInflater mInflater;
//
//	public class AddMeHolder extends BaseHolder {
//
//		private ImageView 	mPublisUserImage;
//		private TextView 	mPublishUserName;
//		private TextView 	mPublishTime;
//		private TextView 	mPublishContent;
//		private ImageView 	mPublishContentImage;
//		private TextView 	mPublishPrice;
//		private TextView 	mPublishPriceYuan;
//		private TextView 	mPublishPlace;
//		private TextView 	mFromWhere;
//		private TextView 	mLikeCount;
//		private TextView 	mCommentCount;
//		private TextView 	mForwardCount;
//		private ImageView 	mPublishPlaceIcon;
//		private ImageView 	mPublishPriceIcon;
//
//		private LinearLayout mSourceLayout;
////		private TextView 	mSourceUserName;
//		private TextView 	mSourceContent;
//		private ImageView 	mSourceContentImage;
//		private TextView 	mSourcePlace;
//		private TextView 	mSourcePrice;
//		private TextView 	mSourcePriceYuan;
//		private ImageView 	mSourcePlaceIcon;
//		private ImageView 	mSourcePriceIcon;
//		
//	}
//
//	
//	public AddMeListView(ListView listView, View header, View footer) {
//		super(listView, header, footer);
//		mInflater = LayoutInflater.from(listView.getContext());
//	}
//
//	@Override
//	protected View createTemplateView() {
//		return  mInflater.inflate(R.layout.news_add_me_item, null);
//	}
//
//	@Override
//	protected AddMeHolder getHolder(View convertView) {
//
//		AddMeHolder holder = new AddMeHolder();
//		holder.mPublisUserImage 	= (ImageView) convertView.findViewById(R.id.user_head);
//		holder.mPublishUserName 	= (TextView) convertView.findViewById(R.id.user_name);
//		holder.mPublishTime 		= (TextView) convertView.findViewById(R.id.time);
//		holder.mPublishContent 		= (TextView) convertView.findViewById(R.id.publish_content);
//		holder.mPublishContentImage = (ImageView) convertView.findViewById(R.id.publish_image);
//		holder.mPublishPlace 		= (TextView) convertView.findViewById(R.id.publish_place);
//		holder.mPublishPrice 		= (TextView) convertView.findViewById(R.id.publish_price);
//		holder.mPublishPriceYuan 	= (TextView) convertView.findViewById(R.id.publish_price_yuan);
//		holder.mPublishPlaceIcon 	= (ImageView) convertView.findViewById(R.id.publish_place_image);
//		holder.mPublishPriceIcon 	= (ImageView) convertView.findViewById(R.id.publish_price_image);
//		holder.mLikeCount 			= (TextView) convertView.findViewById(R.id.like);
//		holder.mCommentCount 		= (TextView) convertView.findViewById(R.id.comment);
//		holder.mForwardCount 		= (TextView) convertView.findViewById(R.id.forward);
//		holder.mFromWhere 			= (TextView) convertView.findViewById(R.id.from_where);
//		holder.mSourceLayout 		= (LinearLayout) convertView.findViewById(R.id.source);
//		holder.mSourceContent 		= (TextView) convertView.findViewById(R.id.source_content);
//		holder.mSourceContentImage 	= (ImageView) convertView.findViewById(R.id.source_image);
//		holder.mSourcePlaceIcon 	= (ImageView) convertView.findViewById(R.id.source_place_image);
//		holder.mSourcePriceIcon 	= (ImageView) convertView.findViewById(R.id.source_price_image);
//		holder.mSourcePlace 		= (TextView) convertView.findViewById(R.id.source_place);
//		holder.mSourcePrice 		= (TextView) convertView.findViewById(R.id.source_price);
//		holder.mSourcePriceYuan 	= (TextView) convertView.findViewById(R.id.source_price_yuan);
//		return holder;
//	}
//
//	@Override
//	protected void setDataWithHolder(
//			AddMeHolder holder,
//			int position,
//			boolean isMoving) {
//		AddMeData data = mDataList.get(position);
//		boolean isOriginal = AddMeData.TYPE_ORIGINAL.equals(data.getPublishType());
//		setUserInfo(data, holder, position, isMoving);
//		holder.mPublishTime.setText(Tools.translateToString(Long.valueOf(data.getPublishTime()) * 1000));
//		holder.mPublishTime.setVisibility(View.VISIBLE);
//		if (!NewsUtil.isEmpty(data.getPublishContent())) {
//			holder.mPublishContent.setVisibility(View.VISIBLE);
//			holder.mPublishContent.setText(data.getPublishContent());
//			if (isOriginal) {
//				Tools.setLimitText(holder.mPublishContent, holder.mPublishContent.getMeasuredWidth(), 3, 0);
//			}
//		} else {
//			holder.mPublishContent.setVisibility(View.GONE);
//		}
//		if (!NewsUtil.isEmpty(data.getPublishContentImageIds())) {
//			holder.mPublishContentImage.setVisibility(View.VISIBLE);
//			boolean isArr = data.getPublishContentImageIds().length > 1;
//			holder.mPublishContentImage.setBackgroundResource(isArr ? R.drawable.feed_img_array_bg
//							: R.drawable.feed_img_bg);
//			if (isArr) {
//				holder.mPublishContentImage.setPadding(3, 3, 9, 9);
//			} else {
//				holder.mPublishContentImage.setPadding(3, 3, 3, 3);
//			}
//			ImageUtil.setImageView(
//					getContext(), 
//					holder.mPublishContentImage, 
//					data.getPublishContentImageIds()[0],
//					79,
//					79,
//					isArr ? R.drawable.feed_img_array_bg
//							: R.drawable.feed_img_bg);
//		} else {
//			holder.mPublishContentImage.setVisibility(View.GONE);
//		}
//
//		if (!NewsUtil.isEmpty(data.getPublishConsumePlace())) {
//			holder.mPublishPlace.setVisibility(View.VISIBLE);
//			holder.mPublishPlaceIcon.setVisibility(View.VISIBLE);
//			holder.mPublishPlace.setText(data.getPublishConsumePlace());
//		} else {
//			holder.mPublishPlace.setVisibility(View.GONE);
//			holder.mPublishPlaceIcon.setVisibility(View.GONE);
//		}
//
//		if (!NewsUtil.isEmpty(data.getPublishConsumePrice())) {
//			holder.mPublishPrice.setVisibility(View.VISIBLE);
//			holder.mPublishPriceIcon.setVisibility(View.VISIBLE);
//			holder.mPublishPriceYuan.setVisibility(View.VISIBLE);
//			holder.mPublishPrice.setText(data.getPublishConsumePrice());
//		} else {
//			holder.mPublishPrice.setVisibility(View.GONE);
//			holder.mPublishPriceIcon.setVisibility(View.GONE);
//			holder.mPublishPriceYuan.setVisibility(View.GONE);
//		}
//
//		if (isOriginal) {
//			holder.mSourceLayout.setVisibility(View.GONE);
//		} else {
//			holder.mSourceLayout.setVisibility(View.VISIBLE);
//			boolean hasSourceContent = !NewsUtil.isEmpty(data.getSourceContent());
//			boolean hasSourceContentImage = !NewsUtil.isEmpty(data.getSourceContentImageIds());
//			boolean hasSourceConsumePlace = !NewsUtil.isEmpty(data.getSourceConsumePlace());
//			if (hasSourceContentImage) {
//				holder.mSourceContentImage.setVisibility(View.VISIBLE);
//				boolean isArr = data.getSourceContentImageIds().length > 1;
//				holder.mSourceContentImage.setBackgroundResource(
//						isArr ? R.drawable.feed_img_array_bg : R.drawable.feed_img_bg);
//				if (isArr) {
//					holder.mSourceContentImage.setPadding(3, 3, 9, 9);
//				} else {
//					holder.mSourceContentImage.setPadding(3, 3, 3, 3);
//				}
//				ImageUtil.setImageView(
//						getContext(), 
//						holder.mSourceContentImage, 
//						data.getSourceContentImageIds()[0],
//						79,
//						79,
//						isArr ? R.drawable.feed_img_array_bg
//								: R.drawable.feed_img_bg);
//			} else {
//				holder.mSourceContentImage.setVisibility(View.GONE);
//			}
//			if (hasSourceConsumePlace) {
//				holder.mSourcePlace.setVisibility(View.VISIBLE);
//				holder.mSourcePlaceIcon.setVisibility(View.VISIBLE);
//				holder.mSourcePlace.setText(data.getSourceConsumePlace());
//			} else {
//				holder.mSourcePlace.setVisibility(View.GONE);
//				holder.mSourcePlaceIcon.setVisibility(View.GONE);
//			}
//			if (!NewsUtil.isEmpty(data.getSourceConsumePrice())) {
//				holder.mSourcePrice.setVisibility(View.VISIBLE);
//				holder.mSourcePriceIcon.setVisibility(View.VISIBLE);
//				holder.mSourcePrice.setText(data.getSourceConsumePrice());
//				holder.mSourcePriceYuan.setVisibility(View.VISIBLE);
//			} else {
//				holder.mSourcePrice.setVisibility(View.GONE);
//				holder.mSourcePriceIcon.setVisibility(View.GONE);
//				holder.mSourcePriceYuan.setVisibility(View.GONE);
//			}
//			if (hasSourceContent) {
//				holder.mSourceContent.setVisibility(View.VISIBLE);
//				SpannableString ss = new SpannableString(data.getSourceUserName() + "  " + data.getSourceContent());
//				ss.setSpan(new ForegroundColorSpan(0XFF5F911B), 0, data.getSourceUserName().length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//				holder.mSourceContent.setText(ss);
//			} else {
//				if (!hasSourceContentImage && !hasSourceConsumePlace) {
//					holder.mSourceContent.setText("该帖已经被删除");
//				} else {
//					holder.mSourceContent.setVisibility(View.GONE);
//				}
//			}
//			Tools.setLimitText(holder.mSourceContent, holder.mSourceContent.getMeasuredWidth(), 3, 0);
//		}
//		holder.mLikeCount.setText(data.getPublishLikeCount());
//		holder.mCommentCount.setText(data.getPublishCommentCount());
//		holder.mForwardCount.setText(data.getPublishForwardCount());
//		String from = data.getPublishFrom();
//		int id = R.string.news_from_web;
//		if (AddMeData.FROM_ADNROID.equals(from)) {
//			id = R.string.news_from_android;
//		} else if (AddMeData.FROM_APPLE.equals(from)) {
//			id = R.string.news_from_apple;
//		}
//		holder.mFromWhere.setText(getContext().getResources().getString(id));
//	}
//
//	private void setUserInfo(
//			AddMeData data,
//			AddMeHolder holder,
//			int position,
//			boolean isMoving) {
//		if (!NewsUtil.isEmpty(data.getPublishUserName())) {
//			holder.mPublishUserName.setText(data.getPublishUserName());
//			holder.mPublishUserName.setVisibility(View.VISIBLE);
//		} else {
//			holder.mPublishUserName.setText("" + position);
//		}
//		ImageUtil.setImageView(
//				getContext(), 
//				holder.mPublisUserImage, 
//				data.getPublishUserImage(),
//				ImageUtil.HEADER_SIZE_BIG,
//				ImageUtil.HEADER_SIZE_BIG,
//				R.drawable.head_men);
//	}
//
//	@Override
//	protected void treateStopEvent(AddMeHolder holder, int position) {
//	}
//}
