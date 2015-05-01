package com.youa.mobile.content;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.content.HomeCommentListView.ContentHolder;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.friend.TextStyle;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.input.CommentPage;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.theme.TopicFeedPage;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.Tools;

public class HomeCommentListView extends
		BaseListView<ContentHolder, List<FeedContentCommentData>> implements
		TextStyle.OnClickListener {

	public class ContentHolder extends BaseHolder {
		private ImageView headView;
		private TextView nameView;
		private TextView timeView;
		private TextView publicContentView;
		private ImageView replyView;
		private boolean isHeadImgGet;
	}

	private LayoutInflater mInflater;
	private Activity mActivity;
	private TextStyle textStyle;

	public HomeCommentListView(ListView listView, View header, View footer,
			Activity activity) {
		super(listView, header, footer);
		mActivity = activity;
		mInflater = LayoutInflater.from(mActivity);
		textStyle = new TextStyle(mActivity);
		textStyle.setOnClickListener(this);
	}

	@Override
	protected View createTemplateView(int pos) {
		return mInflater.inflate(R.layout.feed_content_comment_item, null);
	}

	@Override
	protected ContentHolder getHolder(View convertView, int pos) {
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
		final FeedContentCommentData data = mDataList.get(position);
		if (data == null) {
			return;
		}
		LayoutParams lp = holder.headView.getLayoutParams();
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {
			lp.width = 40;
			lp.height = 40;
		} else {
			lp.width = 60;
			lp.height = 60;
		}
		if (data.sex == User.MEN) {
			holder.headView.setBackgroundResource(R.drawable.head_men);
			holder.headView.setImageBitmap(null);
		} else {
			holder.headView.setBackgroundResource(R.drawable.head_women);
			holder.headView.setImageBitmap(null);
		}
		if (data.public_name != null) {
			holder.nameView.setText(data.public_name);
			holder.nameView.setVisibility(View.VISIBLE);
		} else {
			holder.nameView.setVisibility(View.GONE);
			holder.nameView.setText(null);
		}
		setTime(data, holder);
		if (data.replyName != null) {
			// holder.publicContentView.append("回复");
			// holder.publicContentView.append(Html.fromHtml("<a href=\"\" >"
			// + "@" + data.replyName + "</a>"));
			// holder.publicContentView.append(" :");

			// textStyle.setTextStyle(holder.publicContentView, Color.GREEN,
			// Color.YELLOW, null);
		}
		if (data.contents != null) {
			// holder.publicContentView.append(data.public_content);
			combineContent(holder.publicContentView, data.contents);
			holder.publicContentView.setVisibility(View.VISIBLE);
		} else {
			holder.publicContentView.setVisibility(View.GONE);
			holder.publicContentView.setText(null);
		}
		if (data.public_name != null) {
			holder.replyView.setTag(new String[] { data.commentId,
					data.public_name });
		}

		holder.headView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (!ApplicationManager.getInstance().isLogin()) {
				// Intent i = new Intent(getContext(), LoginPage.class);
				// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// getContext().startActivity(i);
				// return;
				// }
				startUserInfoActivity(data.publicId, data.public_name);
			}
		});
		holder.nameView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (!ApplicationManager.getInstance().isLogin()) {
				// Intent i = new Intent(getContext(), LoginPage.class);
				// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// getContext().startActivity(i);
				// return;
				// }
				startUserInfoActivity(data.publicId, data.public_name);
			}
		});
		setContentImg(data, holder);
		// if (!isMoving) {
		getHeaderImg(holder, position);
		// }
	}

	public void startUserInfoActivity(String uid, String uname) {
		Intent intent = new Intent(getContext(), PersonnalInforPage.class);
		intent.putExtra(PersonnalInforPage.KEY_USER_ID, uid);
		intent.putExtra(PersonnalInforPage.KEY_USER_NAME, uname);
		getContext().startActivity(intent);
	}

	private void setTime(FeedContentCommentData data, ContentHolder holder) {
		if (data.public_time == null) {
			holder.timeView.setVisibility(View.GONE);
			holder.timeView.setText(null);
			return;
		}
		long t = 0;
		try {
			t = Long.valueOf(data.public_time) * 1000;
		} catch (Exception e) {
			holder.timeView.setVisibility(View.GONE);
			return;
		}
		holder.timeView.setText(Tools.translateToString(t));
		holder.timeView.setVisibility(View.VISIBLE);
	}

	protected void combineContent(TextView contentView, ContentData[] contents) {
		contentView.setText(null);
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].type == ContentData.TYPE_AT) {
				contentView
						.append(Html.fromHtml("<a href=\"" + contents[i].href
								+ "\" >" + contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_TOPIC) {
				contentView
						.append(Html.fromHtml("<a href=\"" + contents[i].href
								+ "\" >" + contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_LINK) {
				contentView
						.append(Html.fromHtml("<a href=\"" + contents[i].href
								+ "\" >" + contents[i].str + "</a>"));
			} else if (contents[i].type == ContentData.TYPE_EMOTION) {
				// Spanned span = EmotionHelper.parseToImageText(mActivity,
				// contents[i].str, 20);
				// contentView.append(span);
				contentView.append(Html.fromHtml("<a href=\"img\" >"
						+ contents[i].str + "</a>"));
			} else {
				contentView.append(contents[i].str);
			}
		}
		textStyle.setTextStyle(contentView, 0XFF5F911B, Color.YELLOW, null);
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

		ImageUtil.setHeaderImageView(mActivity, holder.headView,
				data.public_img_head_id, -1);
	}

	// ------------------------------------------------------------------------
	private String mFeedId;

	public void setFeedId(String feedId) {
		mFeedId = feedId;
	}

	private class ReplyOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Bundle bundle = new Bundle();
				bundle.putString(CommentPage.KEY_SOURCE, mFeedId);
				String[] obj = (String[]) v.getTag();
				StringBuffer sb = new StringBuffer();
				sb.append(mActivity.getResources().getString(
						R.string.feed_reply_string));
				sb.append(obj[1]);
				sb.append(":");
				String content = sb.toString();
				sb = null;
				bundle.putString(CommentPage.KEY_ORG_ID, (String) obj[0]);
				bundle.putString(CommentPage.KEY_DEFAULT_CONTENT, content);
				bundle.putBoolean(CommentPage.KEY_TYPE, false);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(mActivity, CommentPage.class);
				mActivity.startActivityForResult(intent,
						ContentActivity.commentRequest);
				break;
			}
			return true;
		}
	}

	@Override
	public void onAtClick(Object object) {
		String[] obj = (String[]) object;
		Bundle bundle = new Bundle();
		bundle.putString(PersonnalInforPage.KEY_USER_ID, obj[0]);
		bundle.putString(PersonnalInforPage.KEY_USER_NAME, obj[1]);
		Intent intent = new Intent();
		intent.setClass(mActivity, PersonnalInforPage.class);
		intent.putExtras(bundle);
		mActivity.startActivity(intent);
	}

	@Override
	public void onTopicClick(Object objet) {
		String[] obj = (String[]) objet;
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(TopicFeedPage.KEYWORD, obj[0]);// mStr
		// int type = 0;
		// try {
		// type = Integer.parseInt(obj[1]);
		// } catch (Exception e) {
		// type = 0;
		// }
		// bundle.putInt(FriendFeedActivity.FEED_TYPE, type);//
		// FeedActivity.TYPE_THEME
		intent.setClass(mActivity, TopicFeedPage.class);
		intent.putExtras(bundle);
		mActivity.startActivity(intent);
	}

	@Override
	public void onCheckClick(Object objet) {

	}
}
