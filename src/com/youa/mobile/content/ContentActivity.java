package com.youa.mobile.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youa.mobile.ExitPage;
import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageData;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.common.util.picture.ViewPicturePage;
import com.youa.mobile.content.action.DeleteFeedAction;
import com.youa.mobile.content.action.LikeAction;
import com.youa.mobile.content.action.LikeCancelAction;
import com.youa.mobile.content.data.FeedContentCommentData;
import com.youa.mobile.content.manager.HistoryFeedManager;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.friend.TextStyle;
import com.youa.mobile.friend.data.ContentImg;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.input.CommentPage;
import com.youa.mobile.input.ForwardPage;
import com.youa.mobile.location.ShopMapPage;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.theme.TopicFeedPage;
import com.youa.mobile.utils.LogUtil;
import com.youa.mobile.utils.Tools;
import com.youa.mobile.webshop.WebShopPage;

public abstract class ContentActivity extends BasePage implements
		BaseListView.OnScrollEndListener, TextStyle.OnClickListener {
	final public static int commentRequest = 10, commentOk = 1,
			commentBack = 2;
	protected Handler mHandler = new Handler();
	protected HomeCommentListView contentListView;
	protected ListView listView;
	protected ProgressBar progressBar;
	private ContentTouchListener touchListener;
	// -------------------title-------------------
	private ImageView backView;
	private ImageView homeView;
	// --------------------bottom-------------------
	private LinearLayout l_LikeView;
	private LinearLayout l_TranspondView;
	private LinearLayout l_CommentView;
	private LinearLayout l_DelView;
	private LinearLayout contentArea;
	private View l_ShareView;
	private TextView contentTimeView;
	private ImageView likeView;
	private ImageView delView;
	private ImageView transpondView;
	private ImageView commentView;
	private TextView commentNumTextView;
	private ImageView shareView;
	private int cancleFlag = 2;
	private int initStat = 0;
	private ArrayList<RelativeLayout> mContentImageItemList = new ArrayList<RelativeLayout>();
	private ArrayList<String> mImageIdList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_content_main);
		touchListener = new ContentTouchListener();
		textStyle = new TextStyle(this);
		textStyle.setOnClickListener(this);
		// mHistoryData = new HistoryData();
		initView();
		loadFriendContentData();
		loadCommentData(true);
		// -------------------------------------------------------
		backView = (ImageView) findViewById(R.id.back);
		homeView = (ImageView) findViewById(R.id.go_home);
		backView.setImageResource(R.drawable.common_button_back_normal);
		l_LikeView = (LinearLayout) findViewById(R.id.l_like);
		l_TranspondView = (LinearLayout) findViewById(R.id.l_transpond);
		l_CommentView = (LinearLayout) findViewById(R.id.l_comment);
		l_DelView= (LinearLayout) findViewById(R.id.l_del);
		l_ShareView = findViewById(R.id.l_share);
		
		delView= (ImageView) findViewById(R.id.del);
		likeView = (ImageView) findViewById(R.id.like);
		transpondView = (ImageView) findViewById(R.id.transpond);
		commentView = (ImageView) findViewById(R.id.comment);
		shareView = (ImageView) findViewById(R.id.share);
		contentImgView.setOnTouchListener(touchListener);
		backView.setOnTouchListener(touchListener);
		homeView.setOnTouchListener(touchListener);
		l_LikeView.setOnTouchListener(touchListener);
		l_TranspondView.setOnTouchListener(touchListener);
		l_CommentView.setOnTouchListener(touchListener);
		l_DelView.setOnTouchListener(touchListener);
		l_ShareView.setOnTouchListener(touchListener);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (mData == null || mData.PublicUser == null) {
			return;
		}
		setSel(l_LikeView, false);
	}

	@Override
	protected void onDestroy() {
		if (mNeedToStore) {
			new HistoryFeedManager().addToHistory(mData);
		}
		super.onDestroy();
	}

//	final private int MENU_BACK = 1, MENU_EXIT = 2;

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuItem m = null;
//		m = menu.add(Menu.NONE, MENU_BACK, 0,
//				getResources().getString(R.string.feed_back_home));
//		m.setIcon(R.drawable.menum_home);
//		m = menu.add(Menu.NONE, MENU_EXIT, 0,
//				getResources().getString(R.string.feed_exit));
//		m.setIcon(R.drawable.menum_exit);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case MENU_BACK:
//			Intent intent = new Intent();
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.setClass(this, LehoTabActivity.class);
//			startActivity(intent);
//			break;
//		case MENU_EXIT:
//			intent = new Intent();
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.setClass(this, ExitPage.class);
//			startActivity(intent);
//			break;
//		}
//		return true;
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != commentRequest) {
			return;
		}
		switch (resultCode) {
		case commentOk:
			loadCommentData(true);
			break;
		}
	}

	private View mFooterView;

	private void initView() {
		// --------------------------------
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		listView = (ListView) findViewById(R.id.pop_listView);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout linear = (LinearLayout) inflater.inflate(
				R.layout.feed_content_head, null);
		listView.addHeaderView(linear);
		initHead(linear);
		mFooterView = LayoutInflater.from(this).inflate(R.layout.feed_footer,
				null);
		contentListView = new HomeCommentListView(listView, null, mFooterView,
				this);
		contentListView.setOnScrollEndListener(this);
	}

	private void initHead(LinearLayout linear) {
		contentUserCase = (RelativeLayout) linear
				.findViewById(R.id.content_user);
		contentUserCase.setOnTouchListener(touchListener);
		headImgView = (ImageView) linear.findViewById(R.id.content_user_head);
		transpondLable = (ImageView) linear.findViewById(R.id.feed_type);
		contentTimeView = (TextView) linear
				.findViewById(R.id.content_public_time);
		nameView = (TextView) linear.findViewById(R.id.content_user_name);
		orgName = (TextView) linear.findViewById(R.id.org_name);
		publicContentView = (TextView) linear
				.findViewById(R.id.content_public_content);
		transpondCase = (LinearLayout) linear
				.findViewById(R.id.content_transpond);
		transpondCase.setOnTouchListener(touchListener);
		contentView = (TextView) linear.findViewById(R.id.content_content);
		commerialCase = (LinearLayout) linear.findViewById(R.id.commerial);
		placeView = (TextView) linear.findViewById(R.id.place);
		placeView.setOnTouchListener(touchListener);
		priceView = (TextView) linear.findViewById(R.id.price);
		contentArea = (LinearLayout) linear.findViewById(R.id.content_area);
		// mContentGallery =
		// (CustomGallery)linear.findViewById(R.id.content_gallery);
		// mContentImgAdapter = new ContentImgAdapter(this);
		// mContentGallery.setAdapter(mContentImgAdapter);
		// mContentGallery.setOnItemSelectedListener(new
		// OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int position, long id) {
		// mContentImgAdapter.setCurrentPoint(position);
		// mContentImgAdapter.notifyDataSetChanged();
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		//
		// }
		// });

		contentImgView = (LinearLayout) linear.findViewById(R.id.content_img);

		fromWhereView = (TextView) linear.findViewById(R.id.form_where);

		likeImgView = (ImageView) linear.findViewById(R.id.like_img);
		commentImgView = (ImageView) linear.findViewById(R.id.comment_img);
		transportImgView = (ImageView) linear.findViewById(R.id.transport_img);
		like = (TextView) linear.findViewById(R.id.like);
		comment = (TextView) linear.findViewById(R.id.comment);
		commentNumTextView = (TextView) linear.findViewById(R.id.comment_num);
		transport = (TextView) linear.findViewById(R.id.transport);
	}

	protected void combineContent(TextView contentView, ContentData[] contents) {
		// mHistoryData.contents = contents;
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
				// Spanned span = EmotionHelper.parseToImageText(this,
				// contents[i].str, 16);
				// contentView.append(span);
				contentView.append(Html.fromHtml("<a href=\"img\" >"
						+ contents[i].str + "</a>"));
			} else {
				contentView.append(contents[i].str);
			}
		}
		// int exWidth = 41;
		// if (ApplicationManager.getInstance().getDensityDpi() >= 160) {
		// exWidth = 64;
		// }
		// exWidth = (int)
		// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		// exWidth, getResources().getDisplayMetrics());
		// Tools.setLimitText(contentView, ApplicationManager.getInstance()
		// .getWidth(), 5, exWidth);// 根据不同分辨率写不同的大小
		// CharSequence charSequence = (CharSequence) contentView.getTag();
		// if (contentView.getText().length() < charSequence.length()) {
		// contentView.append(Html.fromHtml("<a href=\"\" >" + "全文" + "</a>"));
		// }
		textStyle.setTextStyle(contentView, 0XFF5F911B, Color.YELLOW, null);
		// textStyle.setTextStyle(contentView, 0XFF5F911B, Color.YELLOW, "全文");
	}

	@Override
	public void onAtClick(Object object) {
		String[] obj = (String[]) object;
		Bundle bundle = new Bundle();
		bundle.putString(PersonnalInforPage.KEY_USER_ID, obj[0]);
		bundle.putString(PersonnalInforPage.KEY_USER_NAME, obj[1]);
		Intent intent = new Intent();
		intent.setClass(ContentActivity.this, PersonnalInforPage.class);
		intent.putExtras(bundle);
		ContentActivity.this.startActivity(intent);
	}

	@Override
	public void onTopicClick(Object objet) {
		// if (!ApplicationManager.getInstance().isLogin()) {
		// startLoginActivity();
		// return;
		// }
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
		intent.setClass(ContentActivity.this, TopicFeedPage.class);
		intent.putExtras(bundle);
		ContentActivity.this.startActivity(intent);
	}

	@Override
	public void onCheckClick(Object objet) {
		View widget = (View) objet;
		CharSequence charSquence = (CharSequence) widget.getTag();
		((TextView) widget).setText(charSquence);
	}

	protected String getMinId() {
		List<FeedContentCommentData> datas = contentListView.getData();
		if (datas == null) {
			return "-1";
		}
		return datas.get(datas.size() - 1).commentId;
	}

	protected abstract void loadFriendContentData();

	protected abstract void loadCommentData(boolean isRefresh);

	@Override
	public void onScrollHeader() {

	}

	@Override
	public void onScrollEnd() {
		loadCommentData(false);
	}

	// ----------------------------------------正文内容处------------------------------
	// ----------------
	// --------------------head-------------------
	protected RelativeLayout contentUserCase;// 用户点击区
	protected TextView nameView;
	protected TextView publicContentView;// 发布者
	protected LinearLayout transpondCase;
	protected ImageView headImgView;
	protected LinearLayout contentImgView;
	protected TextView orgName;
	protected ImageView transpondLable;
	// private CustomGallery mContentGallery;
	// private ContentImgAdapter mContentImgAdapter;
	protected TextView contentView;
	protected LinearLayout commerialCase;
	protected TextView placeView;
	protected TextView priceView;
	protected TextView fromWhereView;
	private ImageView likeImgView;
	protected TextView like;
	private ImageView commentImgView;
	protected TextView comment;
	private ImageView transportImgView;
	protected TextView transport;
	public TextStyle textStyle;

	protected boolean isHeadImgGet;
	protected boolean isContentGet;
	protected HomeData mData;
	// private HistoryData mHistoryData;
	private boolean mNeedToStore = true;

	protected void updateContent(HomeData data) {
		mData = data;
		// ------------------------------
		// int size=0;
		// if (ApplicationManager.getInstance().getDensityDpi() < 240) {
		// size = ImageUtil.CONTENT_SIZE_SMALL;
		// } else {
		// size = ImageUtil.CONTENT_SIZE_BIG;
		// }
		// // LayoutParams glp = mContentGallery.getLayoutParams();
		// // glp.width=size;
		// // glp.height=size;
		// List<ImageData> list = new ArrayList<ImageData>();
		// for (int i = 0; i < mData.PublicUser.contentImg.length; i++) {
		// ContentImg img = mData.PublicUser.contentImg[i];
		// if (null != img) {
		// ImageData imgData = new ImageData(
		// img.img_content_id, img.img_desc, null, 0);
		// list.add(imgData);
		// }
		// }
		// mContentImgAdapter.setData(list);
		// ------------------------------
		// mHistoryData.uId = mData.PublicUser.uId;
		// mHistoryData.postId = mData.PublicUser.postId;
		if (TextUtils.equals(ApplicationManager.getInstance().getUserId(),
				mData.PublicUser.uId)) {
			l_DelView.setVisibility(View.VISIBLE);
		}
		if (data.PublicUser != null)
			LogUtil.d(TAG, "updateContent. public post id = "
					+ data.PublicUser.postId);
		if (data.originUser != null)
			LogUtil.d(TAG, "updateContent. origin post id = "
					+ data.originUser.postId);
		setSel(l_LikeView, false);
		if (data.PublicUser.isLiked) {
			initStat = 1;
		} else {
			initStat = 0;
		}

		// LayoutParams lp = headImgView.getLayoutParams();
		// if (ApplicationManager.getInstance().getDensityDpi() < 240) {
		// lp.width = 40;
		// lp.height = 40;
		// } else {
		// lp.width = 60;
		// lp.height = 60;
		// }
		if (data.PublicUser.sex == User.MEN) {
			headImgView.setBackgroundResource(R.drawable.head_men);
			headImgView.setImageBitmap(null);
		} else {
			headImgView.setBackgroundResource(R.drawable.head_women);
			headImgView.setImageBitmap(null);
		}
		if (data.PublicUser.name != null) {
			nameView.setText(data.PublicUser.name);
			nameView.setTextColor(0XFF5F911B);
			nameView.setVisibility(View.VISIBLE);
			// mHistoryData.name = data.PublicUser.name;
		} else {
			nameView.setVisibility(View.GONE);
			nameView.setText(null);
		}
		if (data.PublicUser.time != null) {
			contentTimeView.setText(data.PublicUser.name);
			nameView.setVisibility(View.VISIBLE);
			long t = 0;
			try {
				t = Long.valueOf(data.PublicUser.time) * 1000;
			} catch (Exception e) {
				contentTimeView.setVisibility(View.GONE);
				return;
			}
			contentTimeView.setText(Tools.translateToString(t));
			contentTimeView.setVisibility(View.VISIBLE);
		} else {
			nameView.setVisibility(View.GONE);
			nameView.setText(null);
		}
		setTranspondContent(data);
		setContent(data);

		if (data.PublicUser.fromWhere != null) {
			fromWhereView.setVisibility(View.VISIBLE);
			fromWhereView.setText(data.PublicUser.fromWhere);
		} else {
			fromWhereView.setVisibility(View.GONE);
		}
		if (data.PublicUser.like_num != null) {
			like.setText(data.PublicUser.like_num);
			likeImgView.setVisibility(View.VISIBLE);
			like.setVisibility(View.VISIBLE);
			// mHistoryData.like_num = data.PublicUser.like_num;
		} else {
			likeImgView.setVisibility(View.GONE);
			like.setVisibility(View.GONE);
		}
		if (data.PublicUser.comment_num != null) {
			comment.setText(data.PublicUser.comment_num);
			if (Integer.parseInt(data.PublicUser.comment_num) > 0) {
				commentNumTextView
						.setText(getString(R.string.comment_num_lable,
								data.PublicUser.comment_num));
				commentNumTextView.setVisibility(View.VISIBLE);
			}

			commentImgView.setVisibility(View.VISIBLE);
			comment.setVisibility(View.VISIBLE);
		} else {
			commentImgView.setVisibility(View.GONE);
			comment.setVisibility(View.GONE);
			commentNumTextView.setVisibility(View.GONE);
		}
		if (data.PublicUser.transpond_num != null) {
			transport.setText(data.PublicUser.transpond_num);
			transportImgView.setVisibility(View.VISIBLE);
			transport.setVisibility(View.VISIBLE);
		} else {
			transportImgView.setVisibility(View.GONE);
			transport.setVisibility(View.GONE);
		}
		setHeaderImg(data);
		setContentImg(data);
		getHeaderImg();
		getContentImg();
	}

	private void setTranspondContent(HomeData data) {
		if ("0".equals(data.PublicUser.feedType)) {
			transpondCase.setPadding(0, 0, 0, 0);
			transpondCase.setBackgroundDrawable(null);
			transpondLable.setImageResource(R.drawable.feed_say);
			publicContentView.setVisibility(View.GONE);
			publicContentView.setText(null);
			return;
		}
		if ("2".equals(data.PublicUser.feedType)) {// 喜欢
			transpondLable.setImageResource(R.drawable.feed_like);
			publicContentView.setVisibility(View.VISIBLE);
			// transpondCase.setPadding(10, 0, 0, 0);
			transpondCase.setBackgroundResource(R.drawable.feed_reply_area_bg);
			transpondCase.setPadding(18, 0, 0, 0);
			publicContentView.setText("");
			publicContentView.append(Html.fromHtml("<img src='"
					+ R.drawable.content_heart + "'/>", getImage(), null));
			publicContentView.append(getResources().getString(
					R.string.feed_content_like));
		}
		if ("1".equals(data.PublicUser.feedType)) {
			transpondLable.setImageResource(R.drawable.feed_transport);
			transpondCase.setBackgroundResource(R.drawable.feed_transpond_bg);
			publicContentView.setVisibility(View.VISIBLE);
			// transpondCase.setPadding(10, 10, 10, 10);
			if (data.PublicUser.contents != null) {
				setTranpondC(false);
			} else {
				publicContentView.setText(R.string.forward_share_lable);
			}

		}
	}

	protected abstract void setTranpondC(boolean isOrigrin);

	private ImageGetter getImage() {
		ImageGetter imageGetter = new ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				int id = Integer.parseInt(source);
				// 根据id从资源文件中获取图片对象
				Drawable d = getResources().getDrawable(id);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};
		return imageGetter;
	}

	private void setContent(HomeData data) {
		User user = null;
		if (!"0".equals(data.PublicUser.feedType)) {
			user = data.originUser;
		} else {
			user = data.PublicUser;
		}
		// mHistoryData.feedType = data.PublicUser.feedType;
		if (user == null && !"0".equals(data.PublicUser.feedType)) {
			mNeedToStore = false;
			// transpondCase.setBackgroundDrawable(null);
			// transpondCase.setVisibility(View.GONE);
			// if (!"0".equals(data.PublicUser.feedType)) {
			contentView
					.setText(getResources().getString(R.string.feed_deleted));
			contentView.setVisibility(View.VISIBLE);
			// }
			// contentView.setText(null);
			// contentView.setVisibility(View.GONE);
			if(orgName!=null){
				orgName.setVisibility(View.GONE);
				orgName.setText(null);	
			}
			// contentImgView.setBackgroundDrawable(null);
			// contentImgView.setImageDrawable(null);
			contentImgView.setVisibility(View.GONE);
			// mContentGallery.setBackgroundDrawable(null);
			// mContentGallery.setVisibility(View.GONE);
			commerialCase.setVisibility(View.GONE);
			placeView.setText(null);
			priceView.setText(null);
			return;
		}
		if ((!"0".equals(data.PublicUser.feedType))
				&& user.charSequence != null) {
			contentView.setVisibility(View.VISIBLE);
			if (user.nameCharSequence != null) {
				contentView.append(user.nameCharSequence);
				ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
						0XFF5F911B);
				String str = ": ";
				SpannableStringBuilder style = new SpannableStringBuilder(str);
				style.setSpan(foregroundColorSpan, 0, str.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				TextView t = new TextView(this);
				t.setText(style);
				contentView.append(t.getText());
			}
			contentView.append(user.charSequence);
		} else if ("0".equals(data.PublicUser.feedType)) {
			setTranpondC(true);
		} else if (user.charSequence != null) {
			contentView.setVisibility(View.VISIBLE);
			contentView.setText(user.charSequence);
		} else {
			contentView.setVisibility(View.GONE);
			contentView.setText(null);
		}

		// ----------------------------------------------------------
		if (!"0".equals(data.PublicUser.feedType)) {
			// int width = ApplicationManager.getInstance().getWidth()
			// - Tools.dip2px(this, 24) - transpondCase.getPaddingLeft()
			// - transpondCase.getPaddingRight();
			// Tools.setLimitText(contentView, width, 3, 0);
		}
		// -----------------------------------------------------------
		if (user.contentImg != null && user.contentImg.length > 0) {// 针对不同分辨率，
																	// 进行处理
			contentArea
					.setBackgroundResource(R.drawable.feed_content_haspic_bg_selector); // mContentGallery.setVisibility(View.VISIBLE);
			LayoutInflater mInflater = LayoutInflater.from(this);
			RelativeLayout feedContentImageItem = null;
			for (int i = 0; i < user.contentImg.length; i++) {
				feedContentImageItem = (RelativeLayout) mInflater.inflate(
						R.layout.feed_content_img_item, null);
				// feedContentImageItem.setTag(i);
				// feedContentImageItem
				// .setOnClickListener(new android.view.View.OnClickListener() {
				// @Override
				// public void onClick(View view) {
				// String index = view.getTag().toString();
				// if ("2".equals(mData.PublicUser.feedType)) {
				// clickTranspond(transpondCase);
				// return;
				// }
				// ContentImg[] imgs = null;
				// if ("0".equals(mData.PublicUser.feedType)) {
				// imgs = mData.PublicUser.contentImg;
				// } else if (null != mData.originUser) {
				// imgs = mData.originUser.contentImg;
				// }
				// if ((null == imgs) || !isDown) {
				// return;
				// }
				// Bundle bundle = new Bundle();
				// ImageData[] imgDataArray = new ImageData[imgs.length];
				// for (int i = 0; i < imgs.length; i++) {
				// ContentImg img = imgs[i];
				// if (null != img) {
				// ImageData imgData = new ImageData(
				// img.img_content_id,
				// img.img_desc, null, 0);
				// imgDataArray[i] = imgData;
				// }
				// }
				// bundle.putParcelableArray(
				// ViewPicturePage.EXTRA_IMG_ARRAY,
				// imgDataArray);
				// bundle.putString(
				// ViewPicturePage.EXTRA_IMG_INDEX, index);
				// Intent intent = new Intent();
				// intent.putExtras(bundle);
				// intent.setClass(ContentActivity.this,
				// ViewPicturePage.class);
				// ContentActivity.this.startActivity(intent);
				// }
				// });
				contentImgView.setOrientation(LinearLayout.VERTICAL);
				if (user.contentImg.length > 1) {
					if (i == 0) {
						FrameLayout fl = (FrameLayout) feedContentImageItem
								.findViewById(R.id.feed_content_img_area);
						fl.setBackgroundResource(R.drawable.feed_content_du_img_bg);
						TextView tx = (TextView) feedContentImageItem
								.findViewById(R.id.content_img_num);
						tx.setText(String.valueOf(user.contentImg.length) + "张");
						tx.setVisibility(View.VISIBLE);
						tx.setOnClickListener(new android.view.View.OnClickListener() {
							@Override
							public void onClick(View v) {
								v.setVisibility(View.GONE);
								RelativeLayout feedImageItem = mContentImageItemList
										.get(0);
								FrameLayout fl = (FrameLayout) feedImageItem
										.findViewById(R.id.feed_content_img_area);
								fl.setBackgroundResource(R.drawable.feed_img_content_bg);
								for (int i = 0; i < mContentImageItemList
										.size(); i++) {
									RelativeLayout feedContentImageItem = mContentImageItemList
											.get(i);
									if (i == (mContentImageItemList.size() - 1)) {
										TextView tx = (TextView) feedContentImageItem
												.findViewById(R.id.content_img_num);
										tx.setText(String
												.valueOf(mContentImageItemList
														.size())
												+ "张");
										tx.setBackgroundResource(R.drawable.feed_content_img_up_bg);
										tx.setVisibility(View.VISIBLE);
										tx.setOnClickListener(new android.view.View.OnClickListener() {
											@Override
											public void onClick(View v) {
												RelativeLayout feedImageItem = mContentImageItemList
														.get(0);
												FrameLayout fl = (FrameLayout) feedImageItem
														.findViewById(R.id.feed_content_img_area);
												fl.setBackgroundResource(R.drawable.feed_content_du_img_bg);
												for (int i = mContentImageItemList
														.size() - 1; i >= 0; i--) {
													RelativeLayout feedContentImageItem = mContentImageItemList
															.get(i);
													if (i != 0) {
														feedContentImageItem
																.setVisibility(View.GONE);
													} else {
														TextView tx = (TextView) feedContentImageItem
																.findViewById(R.id.content_img_num);
														ImageView img = (ImageView) feedContentImageItem
																.findViewById(R.id.content_img);
														img.setBackgroundResource(R.drawable.feed_content_du_img_bg);
														tx.setVisibility(View.VISIBLE);
													}

												}
											}
										});
									}
									int size = 0;
									if (ApplicationManager.getInstance()
											.getDensityDpi() < 240) {//
										// 背景86,图片80
										size = ImageUtil.CONTENT_SIZE_LITTLE;
									} else {// 背景126,图片120
										size = ImageUtil.CONTENT_SIZE_LARGE;
									}
									feedContentImageItem
											.setVisibility(View.VISIBLE);
									ImageUtil
											.setImageView(
													ContentActivity.this,
													(ImageView) feedContentImageItem
															.findViewById(R.id.content_img),
													mImageIdList.get(i), size,
													size, -1);
								}
							}
						});
						feedContentImageItem.setVisibility(View.VISIBLE);
					}
				}

				mImageIdList.add(user.contentImg[i].img_content_id);
				mContentImageItemList.add(feedContentImageItem);
				contentImgView.addView(feedContentImageItem);
			}
			contentImgView.setVisibility(View.VISIBLE);
			// contentImgView
			// .setBackgroundResource(user.contentImg.length > 1 ?
			// R.drawable.feed_img_array_bg
			// : R.drawable.feed_img_bg);
			// if (user.contentImg.length > 1) {
			// contentImgView.setPadding(3, 3, 9, 9);
			// } else {
			// contentImgView.setPadding(3, 3, 3, 3);
			// }
			// contentImgView.setImageBitmap(null);
			// LayoutParams lp = contentImgView.getLayoutParams();
			// int padding = user.contentImg.length > 1 ? 12 : 6;
			// if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// // 背景86,图片80
			// lp.width = ImageUtil.CONTENT_SIZE_SMALL + padding;
			// lp.height = ImageUtil.CONTENT_SIZE_SMALL + padding;
			// } else {// 背景126,图片120
			// lp.width = ImageUtil.CONTENT_SIZE_BIG + padding;
			// lp.height = ImageUtil.CONTENT_SIZE_BIG + padding;
			// }
			// mHistoryData.contentImg = user.contentImg;
		} else {
			contentArea
					.setBackgroundResource(R.drawable.feed_content_nopic_bg_selector);
			contentImgView.setVisibility(View.GONE);
			// contentImgView.setBackgroundDrawable(null);
			// contentImgView.setImageBitmap(null);
			// mContentGallery.setVisibility(View.GONE);
			// mContentGallery.setBackgroundDrawable(null);
		}
		if (TextUtils.isEmpty(user.place) && TextUtils.isEmpty(user.price)) {
			commerialCase.setVisibility(View.GONE);
		} else {
			commerialCase.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(user.place)) {
				placeView.setVisibility(View.VISIBLE);
				placeView.setText(" " + user.place.trim());
			} else {
				placeView.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(user.price)) {
				priceView.setVisibility(View.VISIBLE);
				priceView.setText(" " + user.price.trim());
			} else {
				priceView.setVisibility(View.GONE);
			}
		}
		// if (user.place == null || user.price == null
		// || "".equals(user.place.trim()) || "".equals(user.price.trim())) {
		// commerialCase.setVisibility(View.GONE);
		// placeView.setText(null);
		// priceView.setText(null);
		// } else {
		// commerialCase.setVisibility(View.VISIBLE);
		// placeView.setText(user.place);
		// // int ww = (int) (width - priceView.getPaint()
		// // .measureText("22222222")) - Tools.dip2px(this, 40);
		// // placeView.getLayoutParams().width = ww;
		// priceView.setText(user.price);
		// }
	}

	// --------------------------图片处理-------------------------------------

	private void setContentImg(HomeData data) {
		// 从缓存去，如果有则世界设上，没有则，去取
		User user = null;
		if (!"0".equals(data.PublicUser.feedType)) {
			user = data.originUser;
		} else {
			user = data.PublicUser;
		}
		if (user == null) {
			return;
		}
		user.isContentImgNeedGet = true;
		isContentGet = false;
	}

	private void setHeaderImg(HomeData data) {
		// 从缓存去，有则设上;没有则，去取
		data.PublicUser.isHeadNeedGet = true;
		isHeadImgGet = false;
		// mHistoryData.img_head_id = data.PublicUser.img_head_id;
	}

	private void getHeaderImg() {
		HomeData data = mData;
		if (data == null || isHeadImgGet || !data.PublicUser.isHeadNeedGet
				|| data.PublicUser.img_head_id == null
				|| "".equals(data.PublicUser.img_head_id)) {
			return;
		}
		isHeadImgGet = true;
		data.PublicUser.isHeadNeedGet = false;
		int size = 0;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// 背景60,
			size = ImageUtil.HEADER_SIZE_BIG;
		} else {// 背景40,
			size = ImageUtil.HEADER_SIZE_BIG;
		}
		// ImageUtil.setImageViewOriginal(ContentActivity.this, headImgView,
		// data.PublicUser.img_head_id, -1);
		ImageUtil.setHeaderImageView(ContentActivity.this, headImgView,
				data.PublicUser.img_head_id, -1);
	}

	private void getContentImg() {
		HomeData data = mData;
		if (data == null) {
			return;
		}
		User user = null;
		if (!"0".equals(data.PublicUser.feedType)) {
			user = data.originUser;
		} else {
			user = data.PublicUser;
		}
		if (user == null || isContentGet || !user.isContentImgNeedGet
				|| user.contentImg == null || user.contentImg[0] == null
				|| user.contentImg[0].img_content_id == null
				|| "".equals(user.contentImg[0].img_content_id)) {
			return;
		}
		isContentGet = true;
		user.isContentImgNeedGet = false;
		int size = 0;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			// 背景86,图片80
			size = ImageUtil.CONTENT_SIZE_LITTLE;
		} else {// 背景126,图片120
			size = ImageUtil.CONTENT_SIZE_LARGE;
		}
		if (mContentImageItemList != null && mContentImageItemList.size() > 0) {
			RelativeLayout feedContentImageItem = mContentImageItemList.get(0);
			feedContentImageItem.setVisibility(View.VISIBLE);
			ImageView img = (ImageView) feedContentImageItem
					.findViewById(R.id.content_img);
			ImageUtil.setImageView(ContentActivity.this, img,
					user.contentImg[0].img_content_id, size, size, -1);
		}
	}

	// ---------------------------------title-bottom事件处理--------------------------
	// ----------------

	private void cancelLike() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LikeAction.PARAM_POST_ID, mData.PublicUser.postId);
		ActionController.post(this, LikeCancelAction.class, params,
				new LikeCancelAction.ISearchResultListener() {

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(ContentActivity.this, resourceID);
								mData.PublicUser.isLiked = true;
								setSel(l_LikeView, false);
							}
						});
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onEnd(final int likeType) {
						if (likeType == LikeCancelAction.DELIKE_OK) {
							cancleFlag = 0;
							mData.PublicUser.isLiked = false;

						}
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								setSel(l_LikeView, false);
								showToast(ContentActivity.this,
										R.string.feed_cancel_like);
							}
						});
					}
				}, true);

	}

	private void deleteFeed() {
		if (mData == null || mData.PublicUser == null) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(DeleteFeedAction.PARAM_POST_ID, mData.PublicUser.postId);
		ActionController.post(this, DeleteFeedAction.class, params,
				new DeleteFeedAction.ISearchResultListener() {
					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								progressBar.setVisibility(View.GONE);
								showToast(ContentActivity.this, resourceID);
							}
						});
					}

					@Override
					public void onStart() {
						progressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onEnd(final int flag) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								progressBar.setVisibility(View.GONE);
								backResult();
							}
						});
					}
				}, true);
	}

	private void backResult() {
		Intent i = new Intent(LehuoIntent.ACTION_OWN_FEED_DELETE);
		i.putExtra("postId", mData.PublicUser.postId);
		sendBroadcast(i);
		setResult(FriendFeedActivity.result_delete_code, i);
		finish();
	}

	private void loadLike() {
		if (mData == null || mData.PublicUser == null) {
			setSel(l_LikeView, false);
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LikeAction.PARAM_POST_ID, mData.PublicUser.postId);
		ActionController.post(this, LikeAction.class, params,
				new LikeAction.ISearchResultListener() {
					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(ContentActivity.this, resourceID);
								if (resourceID == R.string.feed_like_ed) {
									setSel(l_LikeView, false);
								} else {
									setSel(l_LikeView, false);
									showToast(ContentActivity.this,
											R.string.feed_like_fail);
								}
							}
						});
					}

					@Override
					public void onStart() {

					}

					@Override
					public void onEnd(final int likeType) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (likeType == LikeAction.LIKE_OK) {
									showToast(ContentActivity.this,
											R.string.feed_like_sucess);
									mData.PublicUser.isLiked = true;
									cancleFlag = 1;
									setSel(l_LikeView, false);
								} else if (likeType == LikeAction.LIKE_ORIGIN_DELETED) {
									showToast(ContentActivity.this,
											R.string.feed_like_origin_deleted);
									mData.PublicUser.isLiked = false;
									setSel(l_LikeView, false);
								} else if (likeType == LikeAction.LIKE_ED) {
									showToast(ContentActivity.this,
											R.string.feed_like_ed);
									setSel(l_LikeView, false);
								} else if (mData.PublicUser.isLiked) {
									showToast(ContentActivity.this,
											R.string.feed_like_ed);
									setSel(l_LikeView, false);
								} else {
									showToast(ContentActivity.this,
											R.string.feed_like_fail);
									setSel(l_LikeView, false);
								}
							}
						});
					}
				}, true);
	}

	private boolean isDown;

	private class ContentTouchListener implements OnTouchListener {
		private float startX, startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				clearSel(v);
				setSel(v, true);
				setHeadSel(v, true);
				isDown = true;
				switch (v.getId()) {
				case R.id.back:
					backView.setImageResource(R.drawable.common_button_back_focus);
					break;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (Math.abs(event.getX() - startX) < 10
						&& Math.abs(event.getY() - startY) < 10) {
					return false;
				}
				// if (event.getRawX() > 0 && event.getRawY() > 0
				// && event.getRawX() < v.getRight()
				// && event.getRawY() < v.getBottom()) {
				// return false;
				// }
				setHeadSel(v, false);
				setSel(v, false);
				isDown = false;
				break;
			case MotionEvent.ACTION_UP:
				headEvent(v);
				Intent intent = null;
				Bundle bundle = null;
				switch (v.getId()) {
				case R.id.back:
					backView.setImageResource(R.drawable.common_button_back_normal);
					finish();
					break;
				case R.id.go_home:
					Intent homeIntent = new Intent();
					homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					homeIntent.setClass(ContentActivity.this, LehoTabActivity.class);
					startActivity(homeIntent);
					finish();
					break;
				case R.id.content_img:
					if ("2".equals(mData.PublicUser.feedType)) {
						clickTranspond(transpondCase);
						return true;
					}
					ContentImg[] imgs = null;
					if ("0".equals(mData.PublicUser.feedType)) {
						imgs = mData.PublicUser.contentImg;
					} else if (null != mData.originUser) {
						imgs = mData.originUser.contentImg;
					}
					if ((null == imgs) || !isDown) {
						return false;
					}
					bundle = new Bundle();
					ImageData[] imgDataArray = new ImageData[imgs.length];
					for (int i = 0; i < imgs.length; i++) {
						ContentImg img = imgs[i];
						if (null != img) {
							ImageData imgData = new ImageData(
									img.img_content_id, img.img_desc, null, 0);
							imgDataArray[i] = imgData;
						}
					}
					bundle.putParcelableArray(ViewPicturePage.EXTRA_IMG_ARRAY,
							imgDataArray);
					bundle.putInt(ViewPicturePage.EXTRA_IMG_INDEX, 0);
					intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(ContentActivity.this, ViewPicturePage.class);
					ContentActivity.this.startActivity(intent);
					break;
				case R.id.l_like:
					if (!isDown) {
						return false;
					}
					if (likeState()) {
						cancelLike();
					} else {
						loadLike();
					}
					break;
				case R.id.l_del:
					// TODO
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ContentActivity.this);
					builder.setMessage(getResources().getString(
							R.string.feed_delete_confirm));
					builder.setPositiveButton(
							getResources().getString(R.string.yes),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									deleteFeed();
								}
							});
					builder.setNegativeButton(
							getResources().getString(R.string.no),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					builder.show();
					break;
				case R.id.l_transpond:// 转发
					setSel(v, false);
					if (mData == null || !isDown) {
						return false;
					}
					bundle = new Bundle();
					bundle.putString(ForwardPage.KEY_SOURCE_ID,
							mData.PublicUser.postId);
					bundle.putString(ForwardPage.KEY_SOURCE_USERNAME,
							mData.PublicUser.name);
					StringBuffer sb = new StringBuffer();
					sb.append("//@");
					sb.append(mData.PublicUser.name);
					if (!"0".equals(mData.PublicUser.feedType)
							&& mData.PublicUser.charSequence != null) {
						sb.append(" ");
						sb.append(mData.PublicUser.charSequence);
					}
					bundle.putString(ForwardPage.KEY_PARAMS_FORWARD_CONTENT,
							sb.toString());
					intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(ContentActivity.this, ForwardPage.class);
					ContentActivity.this.startActivityForResult(intent,
							ContentActivity.commentRequest);
					break;
				case R.id.l_comment:
					setSel(v, false);
					if (mData == null || !isDown) {
						return false;
					}
					bundle = new Bundle();
					bundle.putString(CommentPage.KEY_SOURCE,
							mData.PublicUser.postId);
					bundle.putBoolean(CommentPage.KEY_TYPE, true);
					intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(ContentActivity.this, CommentPage.class);
					ContentActivity.this.startActivityForResult(intent,
							ContentActivity.commentRequest);
					break;
				case R.id.l_share:
					setSel(v, false);
					if (mData == null || !isDown) {
						return false;
					}
					intent = new Intent();
					bundle = new Bundle();
					bundle.putSerializable(ShareFeedPage.CONTEXT_OBJ, mData);
					intent.putExtras(bundle);
					intent.setClass(ContentActivity.this, ShareFeedPage.class);
					ContentActivity.this.startActivity(intent);
					break;
				case R.id.place:
					intent = new Intent();
					bundle = new Bundle();
					if(mData.PublicUser == null)
						break;
					int lat = 0, lon = 0;
					if (TextUtils.isEmpty(mData.PublicUser.lat)
							|| TextUtils.isEmpty(mData.PublicUser.lon)
							|| "0".equals(mData.PublicUser.lat)
							|| "0".equals(mData.PublicUser.lon)) {
						if(!TextUtils.isEmpty(mData.PublicUser.place) && URLUtil.isNetworkUrl(mData.PublicUser.place)){
							Intent i = new Intent();
							bundle.putString(WebShopPage.URL, mData.PublicUser.place);
							i.putExtras(bundle);
							i.setClass(ContentActivity.this, WebShopPage.class);
							startActivity(i);
						}else {
							showToast(R.string.no_support_place_info);
						}
						break;
					}
					
					try {
						lat = Integer.parseInt(mData.PublicUser.lat);
						lon = Integer.parseInt(mData.PublicUser.lon);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bundle.putInt(ShopMapPage.KEY_LAT, lat);
					bundle.putInt(ShopMapPage.KEY_LON, lon);
					bundle.putString(ShopMapPage.KEY_REFID, mData.PublicUser.refId);
					bundle.putString(ShopMapPage.KEY_PNAME, mData.PublicUser.place);
					bundle.putInt(ShopMapPage.KEY_SHOP_TYPE, mData.PublicUser.placeType);
					intent.putExtras(bundle);
					intent.setClass(ContentActivity.this, ShopMapPage.class);
					ContentActivity.this.startActivity(intent);
				}
				break;
			}
			return true;
		}
	}

	// private boolean getSel(View view, boolean isSel, boolean isOrigin) {
	// if (view == null || view != l_LikeView) {
	// return isSel;
	// }
	// boolean sel = likeState();
	// return isOrigin ? sel : !sel;
	// }

	private boolean likeState() {
		if (mData == null || mData.PublicUser == null) {
			return false;
		}
		return mData.PublicUser.isLiked;
	}

	private void setHeadSel(View v, boolean isSel) {
		if (v == null || v.getVisibility() == View.GONE) {
			return;
		}
		Resources resource = getResources();
		Drawable sel = null;
		switch (v.getId()) {
		case R.id.content_user:
			sel = resource.getDrawable(R.drawable.feed_item_color_sel);
			v.setBackgroundDrawable(isSel ? sel : null);
			break;
		case R.id.content_img:
			if (!"2".equals(mData.PublicUser.feedType)) {
				break;
			}
			v = transpondCase;
			// case R.id.content_transpond:// contant-transpond
			// if ((publicContentView != null &&
			// publicContentView.getVisibility() == View.GONE)
			// || "0".equals(mData.PublicUser.feedType)) {
			// return;
			// }
			// sel = resource.getDrawable(R.drawable.feed_transpond_bg_sel);
			// Drawable unSel =
			// resource.getDrawable(R.drawable.feed_transpond_bg);
			// v.setBackgroundDrawable(isSel ? sel : unSel);
			// break;
		}
	}

	private void headEvent(View v) {
		if (v.getVisibility() == View.GONE || !isDown) {
			return;
		}
		Intent intent = null;
		Bundle bundle = null;
		switch (v.getId()) {
		case R.id.content_user:
			// if (!ApplicationManager.getInstance().isLogin()) {
			// startLoginActivity();
			// return;
			// }
			setHeadSel(v, false);
			bundle = new Bundle();
			bundle.putString(PersonnalInforPage.KEY_USER_ID,
					mData.PublicUser.uId);
			bundle.putString(PersonnalInforPage.KEY_USER_NAME,
					mData.PublicUser.name);
			intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(ContentActivity.this, PersonnalInforPage.class);
			ContentActivity.this.startActivity(intent);
			break;
		case R.id.content_transpond:
			clickTranspond(v);
			break;
		}
	}

	private void clickTranspond(View v) {
		setHeadSel(v, false);
		if (publicContentView.getVisibility() == View.GONE
				|| mData.originUser == null) {
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
				mData.originUser.postId);
		Intent intent = new Intent();
		intent.putExtras(bundle);
		intent.setClass(ContentActivity.this, ContentOriginActivity.class);
		ContentActivity.this.startActivity(intent);
	}

	private void clearSel(View v) {
		if (v == null || v.getId() == R.id.back) {
			return;
		}
		// setSel(likeView, false);
		setSel(l_TranspondView, false);
		setSel(l_CommentView, false);
		setSel(l_ShareView, false);
	}

	private void setSel(View v, boolean isSel) {
		if (v == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.l_like:
			// if (!isSel && mData != null && mData.PublicUser != null
			// && mData.PublicUser.isLiked) {
			// return;
			// }
			if (mData != null && mData.PublicUser != null
					&& mData.PublicUser.isLiked) {
				likeView.setImageResource(isSel ? R.drawable.ic_liked_focus
						: R.drawable.ic_liked);
			} else {
				likeView.setImageResource(isSel ? R.drawable.ic_like_no_focus
						: R.drawable.ic_like_no);
			}
			break;
		case R.id.l_transpond:
			transpondView
					.setImageResource(isSel ? R.drawable.ic_transpond_focus
							: R.drawable.ic_transpond);
			break;
		case R.id.l_comment:
			commentView.setImageResource(isSel ? R.drawable.ic_comment_focus
					: R.drawable.ic_comment);
			break;
		case R.id.l_share:
			shareView.setImageResource(isSel ? R.drawable.ic_share_focus
					: R.drawable.ic_share);
			break;
		}
	}

	@Override
	public void finish() {
		if (mData != null && mData.PublicUser != null
				&& !mData.PublicUser.isLiked && initStat == 1
				&& cancleFlag == 0) {
			Intent i = new Intent(LehuoIntent.ACTION_OWN_FEED_DELETE);
			i.putExtra("postId", mData.PublicUser.postId);
			sendBroadcast(i);
			setResult(FriendFeedActivity.result_delete_code, i);
		}
		super.finish();
	}
}
