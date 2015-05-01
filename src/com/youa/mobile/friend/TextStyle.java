package com.youa.mobile.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Browser;
import android.text.Html.ImageGetter;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.parser.ContentData;

public class TextStyle {
	private Context mContext;

	public TextStyle(Context context) {
		mContext = context;
	}

	public void setTextStyle(TextView tv, int textColor,
			final int textFocusColor, String addStr) {
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		CharSequence text = tv.getText();
		System.out.println("text:"+text);
		if (!(text instanceof Spannable)) {
			return;
		}
		int end = text.length();
		final Spannable sp = (Spannable) tv.getText();
		final URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
		if (urls == null || urls.length < 1) {
			return;
		}
		final SpannableStringBuilder style = new SpannableStringBuilder(text);
		style.clearSpans(); // should clear old spans
		int iStart = 0, iEnd = 0;
		for (int i = 0; i < urls.length; i++) {
			iStart = sp.getSpanStart(urls[i]);
			iEnd = sp.getSpanEnd(urls[i]);
			TextURLSpan myURLSpan = new TextURLSpan(urls[i].getURL(), style,
					iStart, iEnd, sp);
			style.setSpan(myURLSpan, iStart, iEnd,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			Drawable drawable = null;
			ImageSpan span = null;
			if (addStr != null && i == urls.length - 1
					&& addStr.equals(myURLSpan.getStr())) {
				myURLSpan.setType(TextURLSpan.TYPE_CHECK);
				// style.setSpan(new ForegroundColorSpan(Color.WHITE), iStart,
				// iEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// style.setSpan(new BackgroundColorSpan(0XFF74a345), iStart,
				// iEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				drawable = mContext.getResources().getDrawable(
						R.drawable.feed_content_button);// feed_title_middle_middle
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
				style.setSpan(span, iStart, iEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if ("img".equals(urls[i].getURL())) {
				String str = sp.toString().substring(iStart, iEnd);
				if (str != null) {
					int id = EmotionHelper.getEmoImgByChar(str);
					if (id > 0) {
						drawable = mContext.getResources().getDrawable(id);// feed_title_middle_middle
						float density = ApplicationManager.getInstance()
								.getDensity();
						drawable.setBounds(0, 0, (int) (20 * density),
								0 + (int) (20 * density));
						span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
						style.setSpan(span, iStart, iEnd,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			} else {
				style.setSpan(new ForegroundColorSpan(textColor), iStart, iEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		tv.setText(style);
	}

	public interface OnClickListener {
		void onAtClick(Object objet);

		void onTopicClick(Object objet);

		void onCheckClick(Object objet);
	}

	private OnClickListener mOnClickListener;

	public void setOnClickListener(OnClickListener onClickListener) {
		mOnClickListener = onClickListener;
	}

	public OnClickListener getlid() {
		return mOnClickListener;
	}

	private class TextURLSpan extends URLSpan {
		final public static int TYPE_TEXT = 0, TYPE_AT = 1, TYPE_TOPIC = 2,
				TYPE_CHECK = 3, TYEP_EMOTION = 4, TYPE_LIKN = 5;
		private SpannableStringBuilder mStyle;
		private int mType;
		private int mStart, mEnd;
		private String mStr;

		public TextURLSpan(String url, SpannableStringBuilder style) {
			super(url);
			mStyle = style;
		}

		TextURLSpan(String url, SpannableStringBuilder style, int start,
				int end, Spannable sp) {
			super(url);
			mStyle = style;
			mStart = start;
			mEnd = end;
			checkType(sp, mStart, mEnd);
		}

		private String getStr() {
			return mStr;
		}

		private void setType(int type) {
			mType = type;
		}

		private void checkType(Spannable sp, int start, int end) {
			String str = sp.toString().substring(start, end);
			if (str.startsWith("#")) {
				mStr = str.substring(1, str.length()-1);
				mType = TextURLSpan.TYPE_TOPIC;
			} else if (str.startsWith("@")) {
				mStr = str.substring(1, str.length());
				mType = TextURLSpan.TYPE_AT;
			} else if (!str.startsWith("[")) {
				mStr = str.substring(0, str.length());
				mType = TYPE_LIKN;
			}
		}

		@Override
		public void onClick(View widget) {
			// mStyle.setSpan(new BackgroundColorSpan(Color.RED), mStart, mEnd,
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			if (mOnClickListener == null) {
				return;
			}
			String[] obj = null;
			switch (mType) {
			case TYPE_AT:
				String url=getURL();
				if(!isUrlValid(url)){
					Toast.makeText(mContext, "此人不存在", Toast.LENGTH_SHORT).show();
					return;
				}
				obj = new String[] { url, mStr };// WXShareMainPage.KEY_USER_ID,WXShareMainPage.KEY_USER_NAME
				mOnClickListener.onAtClick(obj);
				break;
			case TYPE_TOPIC:
				obj = new String[] { mStr};//, "" + FriendFeedActivity.TYPE_THEME 
				mOnClickListener.onTopicClick(obj);
				break;
			case TYPE_CHECK:
				mOnClickListener.onCheckClick(widget);
				break;
			case TYPE_LIKN:
				url = getURL();
				if (url == null) {
					break;
				}
				Context context = widget.getContext();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//				intent.setClassName("com.android.browser",
//						"com.android.browser.BrowserActivity");
				context.startActivity(intent);
				break;
			// default:
			// super.onClick(widget);
			// break;
			}
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(false); // 去掉下划线
		}
	}
	
	private boolean  isUrlValid(String url){
		if(url==null||url.trim().length()<1){
			return false;
		}
		int len = url.length();
		for (int i = 0; i < len; i++) {
			if(!isValid(url.charAt(i))){	
				return false;
			}
			continue;
		}
		return true;
	}
	
	private boolean isValid(char c){
		if(c>=48&&c<=57||c>=65&&c<=70||c>=97&&c<=102){
			return true;
		}
		return false;
	}

	protected ImageGetter getImage() {
		ImageGetter imageGetter = new ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				int id = Integer.parseInt(source);
				// 根据id从资源文件中获取图片对象
				Drawable d = mContext.getResources().getDrawable(id);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};
		return imageGetter;
	}
}
