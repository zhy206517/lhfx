package com.youa.mobile.jingxuan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.AbstractListView.OnScrollEndListener;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.common.widget.FlowLayout;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.content.WaterfallListView;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.jingxuan.action.RequestTagInfoByClsidAction;
import com.youa.mobile.jingxuan.action.TagClassFeedAction;
import com.youa.mobile.jingxuan.data.CategoryData;
import com.youa.mobile.jingxuan.data.TagInfoData;
import com.youa.mobile.ui.base.BasePopWindow;
import com.youa.mobile.ui.base.BasePopWindow.OnClosePopListener;

public class TagClassFeedPage extends BasePage implements OnScrollEndListener {
	public static String ID_KEY = "id";
	public static String SUB_ID_KEY = "subid";

	public static String TYPE_KEY = "type";

	public static String TYPE_TOPIC = "topic";
	public static String TYPE_TAG = "tag";
	public static String TYPE_CLASSIFY = "classify";

	public static String CURRENT_PAGE = "cur_page";
	public static String TITLE_NAME = "title_name";
	public static String TAG_NAME = "tag_name";

	public String ID_VALUE1 = "";
	public String ID_VALUE2 = "";
	public String TYPE_VALUE = "";
	public String mTitle = "";
	public String mTagName = "";

	public final static int REQUEST_COUNT = 50;
	private WaterfallListView feedList;
	private ListView feedListView;
	private View footer;
	private LinearLayout header;
	private LayoutInflater mInflater;
	private ProgressBar tagProgressBar;
	private ImageButton mback;
	private LinearLayout tagArea;
	private int current_page = 0;

	private BasePopWindow popTagWindow;
	//
	private ScrollView feed_pop_tag;
	private TextView mTagNameTX;
	private ImageView mTagNamePic;
	private List<TagInfoData> mTagInfoList = new ArrayList<TagInfoData>();
	private TextView mPriTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_class_feed);
		getIntentParam(this.getIntent());
		initView();
		loadFeedData(ID_VALUE1, ID_VALUE2, TYPE_VALUE, current_page, true);
		if (!TYPE_TOPIC.equals(TYPE_VALUE)) {
			loadTagData(ID_VALUE1);
		}
	}

	private void getIntentParam(Intent i) {
		if (i != null) {
			TYPE_VALUE = i.getStringExtra(TagClassFeedPage.TYPE_KEY);
			ID_VALUE1 = i.getStringExtra(TagClassFeedPage.ID_KEY);
			ID_VALUE2 = i.getStringExtra(TagClassFeedPage.SUB_ID_KEY);
			mTitle = i.getStringExtra(TagClassFeedPage.TITLE_NAME);
			mTagName = i.getStringExtra(TagClassFeedPage.TAG_NAME);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void initView() {
		mInflater = LayoutInflater.from(this);
		feedListView = (ListView) findViewById(R.id.list);
		header = (LinearLayout) getInflaterLayout(R.layout.feed_header);
		footer = getInflaterLayout(R.layout.feed_footer);
		toTopInit();
		toEndInit();
		feedList = new WaterfallListView(feedListView, header, footer);
		feedList.setOnScrollEndListener(this);
		feedList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeData data = (HomeData) v.getTag();
				if (data == null || data.PublicUser == null) {
					return;
				}
				Intent intent = new Intent();
				if ("0".equals(data.PublicUser.feedType)) {
					intent.setClass(TagClassFeedPage.this,
							ContentOriginActivity.class);
					// 源动态id
					intent.putExtra(ContentOriginActivity.ORIGIN_FEED_ID,
							data.PublicUser.postId);
				} else {
					if (data.originUser == null) {
						return;
					}
					intent.setClass(TagClassFeedPage.this,
							ContentTranspondActivity.class);
					// 转发动态id
					intent.putExtra(ContentTranspondActivity.TRANSPOND_FEED_ID,
							data.originUser.postId);
				}
				startActivity(intent);
			}
		});
		RelativeLayout title = (RelativeLayout) findViewById(R.id.title);
		((TextView) (findViewById(R.id.title_text))).setText(mTitle);
		mback = (ImageButton) title.findViewById(R.id.turnpage);
		mback.setBackgroundResource(R.drawable.common_back_selector);
		tagArea = (LinearLayout) title.findViewById(R.id.tag_bt);
		if (TYPE_TOPIC.equals(TYPE_VALUE)) {
			tagArea.setVisibility(View.GONE);
		} else {
			mTagNameTX = (TextView) findViewById(R.id.tag_name);
			mTagNamePic = (ImageView) findViewById(R.id.tag_name_pic);
			tagProgressBar = (ProgressBar) findViewById(R.id.tag_progressBar);
			if (TYPE_CLASSIFY.equals(TYPE_VALUE)) {
				mTagNameTX.setVisibility(View.GONE);
				mTagNamePic.setVisibility(View.GONE);
				tagProgressBar.setVisibility(View.VISIBLE);
			}
			tagArea.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mTagInfoList != null && mTagInfoList.size() > 0) {
						popTagWindow(v);
					}
				}
			});
		}
		if (TYPE_TAG.equals(TYPE_VALUE)) {
			mTagNameTX.setText(mTagName);
		}
		mback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TagClassFeedPage.this.finish();
			}
		});
		mProcessView = findViewById(R.id.progressBar);
	}

	private void popTagWindow(View v) {
		ApplicationManager manager = ApplicationManager.getInstance();
		int width = manager.getWidth() * 9 / 10;
		int height = manager.getHeight();
		if (height > 0) {
			height = height * 6 / 7;
		}
		if (popTagWindow == null) {
			popTagWindow = new BasePopWindow(this);
			popTagWindow.setBackgroundDrawable(R.drawable.tag_info_bg);
			popTagWindow.setOnClosePopListener(new OnClosePopListener() {
				@Override
				public void OnClose(View v) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mTagNamePic
									.setImageResource(R.drawable.select_tag_arrow_down);
						}
					});

				}
			});
			popTagWindow.buildPopWindow(feed_pop_tag, width, height);
		} else {
			popTagWindow.setHeight(height);
		}
		int offX = v.getMeasuredWidth() / 2 - width / 2;
		popTagWindow.showDropDown(v, offX, 0);
		mTagNamePic.setImageResource(R.drawable.select_tag_arrow_up);
	}

	// private int measureTheme(int height) {
	// if (mTagInfoList == null) {
	// return NullHeight;
	// }
	// int n = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
	// ITEM_HEIGHT, getResources().getDisplayMetrics());
	// n = (int) Math.ceil((double) n / 3);
	// int themeCollectionHeight = mTagInfoList.size() * n;
	// int h = themeCollectionHeight;
	// if (height > 0 && themeCollectionHeight > height) {
	// h = height;
	// }
	// return h;
	// }

	private void initPopTag() {
		feed_pop_tag = new ScrollView(this);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		for (TagInfoData tagInfo : mTagInfoList) {
			if (tagInfo == null || tagInfo.mChildren == null) {
				continue;
			}
			if("热门标签".equals(tagInfo.tagName)){
				continue;
			}
			View rChildItem = mInflater.inflate(R.layout.include_taginfo_item,
					null);
			if (TextUtils.isEmpty(tagInfo.tagName)) {
				LinearLayout lin = (LinearLayout) mInflater.inflate(
						R.layout.view_tag_text, null);
				TextView btn = (TextView) lin.findViewById(R.id.all_title);
				btn.setTag(tagInfo.mChildren.get(0));
				btn.setText(tagInfo.mChildren.get(0).tagName);
				if (TYPE_CLASSIFY.equals(TYPE_VALUE)) {
					btn.setBackgroundResource(R.drawable.tag_item_bg_focus);
					mPriTextView = btn;
				} else {
					btn.setBackgroundResource(R.drawable.tag_item_bg_normal);
				}
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TagInfoData data = (TagInfoData) v.getTag();
						mTagName = data.tagName;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mTagNameTX.setVisibility(View.VISIBLE);
								mTagNameTX.setText(mTagName);
								if (popTagWindow != null) {
									popTagWindow.dismiss();
								}
							}
						});
						if(mPriTextView!=null){
							mPriTextView.setBackgroundResource(R.drawable.tag_item_bg_normal);
						}
						v.setBackgroundResource(R.drawable.tag_item_bg_focus);
						mPriTextView = (TextView) v;
						loadFeedData(ID_VALUE1, data.tagId, TYPE_TAG, 0, true);
					}
				});
				linearLayout.addView(lin);
				continue;
			} else {
				((TextView) rChildItem
						.findViewById(R.id.msubs_right_parent_text))
						.setText(tagInfo.tagName);
			}
			FlowLayout layout = (FlowLayout) rChildItem
					.findViewById(R.id.msubs_flow);
			for (TagInfoData tag : tagInfo.mChildren) {
				TextView btn = (TextView) mInflater.inflate(
						R.layout.view_tag_btn, null);
				btn.setTag(tag);
				btn.setText(tag.tagName);
				if (tag.tagName.equals(mTagName)) {
					btn.setBackgroundResource(R.drawable.tag_item_bg_focus);
					mPriTextView = btn;
				} else {
					btn.setBackgroundResource(R.drawable.tag_item_bg_normal);
				}
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TagInfoData data = (TagInfoData) v.getTag();
						mTagName = data.tagName;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mTagNameTX.setVisibility(View.VISIBLE);
								mTagNameTX.setText(mTagName);
								if (popTagWindow != null) {
									popTagWindow.dismiss();
								}
							}
						});
						if(mPriTextView!=null){
							mPriTextView.setBackgroundResource(R.drawable.tag_item_bg_normal);
						}
						v.setBackgroundResource(R.drawable.tag_item_bg_focus);
						mPriTextView = (TextView) v;
						loadFeedData(ID_VALUE1, data.tagId, TYPE_TAG, 0, true);
					}
				});
				layout.addView(btn);
			}
			linearLayout.addView(rChildItem);
		}
		feed_pop_tag.addView(linearLayout);
	}

	private void updateFeed(final List<HomeData> homeDataList) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				hiddenProgressView();
				feedListView.setVisibility(View.VISIBLE);
				feedList.closeHeaderFooter();
				if (homeDataList == null || homeDataList.size() == 0) {
					feedList.setLockEnd(true);
				} else {
					if (current_page == 1) {
						feedList.setData(homeDataList,
								PageSize.INFO_FEED_PAGESIZE);
					} else {
						feedList.addData(homeDataList,
								PageSize.INFO_FEED_PAGESIZE);
					}
					feedList.setLockEnd(false);
				}
			}
		});
	}

	private void setParams(String id, String subid, String type) {
		ID_VALUE1 = id;
		ID_VALUE2 = subid;
		TYPE_VALUE = type;
	}

	private void loadFeedData(final String id, final String subid,
			final String type, int page, final boolean flag) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TagClassFeedPage.ID_KEY, id);
		params.put(TagClassFeedPage.SUB_ID_KEY, subid);
		params.put(TagClassFeedPage.TYPE_KEY, type);
		if (flag) {
			current_page = 0;
			page = 0;
		}
		params.put(TagClassFeedPage.CURRENT_PAGE, page);
		ActionController.post(TagClassFeedPage.this, TagClassFeedAction.class,
				params, new TagClassFeedAction.ISearchResultListener() {
					@Override
					public void onFail(int resourceID) {
						showToast(TagClassFeedPage.this, resourceID);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// homeProgressBar.setVisibility(View.GONE);
								hiddenProgressView();
								feedList.closeHeaderFooter();
							}
						});
					}

					@Override
					public void onStart() {
						if (flag) {
							showProgressView();
						}
					}

					@Override
					public void onEnd(List<HomeData> homeDataList) {
						current_page++;
						setParams(id, subid, type);
						updateFeed(homeDataList);
					}

				}, true);
	}

	private void loadTagData(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TagClassFeedPage.ID_KEY, id);
		ActionController.post(TagClassFeedPage.this,
				RequestTagInfoByClsidAction.class, params,
				new RequestTagInfoByClsidAction.ITagInfoResultListener() {
					@Override
					public void onFail(int resourceID) {
						showToast(TagClassFeedPage.this, resourceID);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								tagProgressBar.setVisibility(View.GONE);
							}
						});
					}

					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
							}
						});

					}

					@Override
					public void onEnd(final CategoryData data) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (TYPE_CLASSIFY.equals(TYPE_VALUE)) {
									mTagName = "全部" + mTitle;
									mTagNameTX.setText(mTagName);
									mTagNameTX.setVisibility(View.VISIBLE);
									mTagNamePic.setVisibility(View.VISIBLE);
									tagProgressBar.setVisibility(View.GONE);
								}
								if (data.mChildren != null
										&& data.mChildren.size() > 0) {
									TagInfoData infoData = new TagInfoData();
									infoData.mChildren = new ArrayList<TagInfoData>();
									TagInfoData tagData = new TagInfoData();
									tagData.tagName = "全部" + mTitle;
									infoData.mChildren.add(tagData);
									mTagInfoList.clear();
									mTagInfoList.add(infoData);
									mTagInfoList.addAll(data.mChildren);
									initPopTag();
								}
							}
						});

					}

				}, true);
	}

	public View getInflaterLayout(int resource) {
		return mInflater.inflate(resource, null);
	}

	@Override
	public void onScrollEnd() {
		loadFeedData(ID_VALUE1, ID_VALUE2, TYPE_VALUE, current_page, false);
	}

	@Override
	public void onScrollHeader() {
		loadFeedData(ID_VALUE1, ID_VALUE2, TYPE_VALUE, current_page, true);
	}

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private TextView footer_tipsTextview;
	private TextView footer_lastUpdatedTextView;
	private ImageView footer_arrowImageView;
	private ProgressBar footer_progressBar;

	private void toEndInit() {
		footer_arrowImageView = (ImageView) footer
				.findViewById(R.id.head_arrowImageView);
		footer_progressBar = (ProgressBar) footer
				.findViewById(R.id.head_progressBar);
		footer_tipsTextview = (TextView) footer
				.findViewById(R.id.head_tipsTextView);
		footer_lastUpdatedTextView = (TextView) footer
				.findViewById(R.id.head_lastUpdatedTextView);
	}

	private void toTopInit() {
		arrowImageView = (ImageView) header
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) header.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) header.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) header
				.findViewById(R.id.head_lastUpdatedTextView);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}
}
