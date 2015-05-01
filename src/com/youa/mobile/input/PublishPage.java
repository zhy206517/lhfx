package com.youa.mobile.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youa.mobile.LehoApp;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseSyncPage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.NetworkStatus;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.common.util.picture.ViewPicturePage;
import com.youa.mobile.common.widget.TakePicturePage;
import com.youa.mobile.input.action.SaveDraftAction;
import com.youa.mobile.input.action.SearchDraftAction;
import com.youa.mobile.input.data.ImageData;
import com.youa.mobile.input.data.PublishData;
import com.youa.mobile.input.manager.DraftManager;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.location.MapPage;
import com.youa.mobile.login.widget.CustomGallery;

public class PublishPage extends BaseSyncPage {

	private final static String TAG = "PublishPage";

	public final static int REQUEST_NEW_IMAGE = 2;
	public final static int REQUEST_NEW_CONSUME = 4;
	public final static int REQUEST_SHOW_CONSUME = 5;
	public final static int REQUEST_SHOW_IMAGE = 6;
	public final static int FACE_DEFAULT_SIZE = 22;

	private final static int REQUEST_LOCATION = 10;
	private final static int REQUEST_SAVE_DRAFT = 0;
	private final static int REQUEST_INPUT_TEXTCONTENT = 11;
	private final static long DEFAULT_SAVE_TIME = 10000;

	public final static String CONSUME_PRICE = "price";
	public final static String CONSUME_PEOPLE_NUM = "people_num";
	public final static String CONSUME_AV_PRICE = "av_price";
	public final static String CONSUME_MANY_PEOPLE = "many_people";
	public final static String IS_DELETE = "delete";
	public final static String KEY_PARAMS_ID = "uid";
	public final static String KEY_PARAMS_CONTENT = "content";
	public final static String KEY_PARAMS_MANY_PEOPLE = "many_people";

	public final static String KEY_PARAMS_PLID = "pid";
	public final static String KEY_PARAMS_PTYPE = "type";
	public final static String KEY_PARAMS_PLACE = "place";
	public final static String KEY_PARAMS_IMAGE = "image";
	public final static String KEY_PARAMS_PRICE = "price";
	public final static String KEY_PARAMS_LATITUDE = "latitude";
	public final static String KEY_PARAMS_LONGITUDE = "longitude";

	public final static String KEY_PARAMS_SYNC_SITE = "sync_data";
	public final static String KEY_PARAMS_SHARED = "share";
	public final static String KEY_FROM_TOPIC = "from_topic";
	public final static String KEY_FROM_OUTSIDE_SHARE = "from_outside_share";
	private String address;
	private ImageButton mBackButton;
	private Button mSendButton;

	private CustomGallery mImageGallery;
	private ImageAdapter mImageAdapter;
	private List<ImageData> mImageList = new ArrayList<ImageData>();
	private ImageView addImageView;
	// private ImageView mHasImageButton;
	private View mInsertImageButton;
	private View mHasImageArea;
	private TextView mTitleTextView;
	private View mInsertImageView;

	private ImageView mInsertLocIcon;
	private ImageView mInsertPriceIcon;
	private TextView mLocInfo;
	private TextView mPriceInfo;
	private ProgressBar loadLocProgressBar;
	private RelativeLayout mRelLocInfo;
	private RelativeLayout mRelPriceInfo;
	private LinearLayout mPicIndexView;
	private ArrayList<ImageView> imageViewList;
	// private CheckBox mSyncSinaBox;
	// private CheckBox mSyncQQBox;
	// private CheckBox mSyncRenRenBox;
	// private ProgressBar loadProgressBar;

	private boolean isSaveThreadRunning;
	private boolean needSaveWhenPause;
	private PublishData mPublicData;
	private int leftWords;
	private String delPath;
	private BitmapCanInsertEditText mContentEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_publish);
		initViews();
		initInfoFromDraft();
	}

	private void initViews() {
		mTitleTextView = (TextView) findViewById(R.id.title);
		mBackButton = (ImageButton) findViewById(R.id.back);
		mSendButton = (Button) findViewById(R.id.send);

		mInsertImageButton = findViewById(R.id.insert_image);
		mInsertImageView = findViewById(R.id.insert_image_area);

		mImageGallery = (CustomGallery) findViewById(R.id.has_image);
		mImageAdapter = new ImageAdapter(this, mImageList);
		mImageGallery.setAdapter(mImageAdapter);
		mContentEdit = (BitmapCanInsertEditText) findViewById(R.id.edit);
		mImageGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long arg3) {
				String path = mImageList.get(pos).imagePath;
				startShowImagePage(path);
				delPath = path;
			}
		});
		mImageGallery.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if(imageViewList==null){
					return;
				}
				for (int i = 0; i < imageViewList.size(); i++) {
					ImageView imageview = imageViewList.get(i);
					if (i == position) {
						imageview.setImageResource(R.drawable.pic_index_selector);
					}else{
						imageview.setImageResource(R.drawable.pic_index_normal);
					}
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
		addImageView = (ImageView) this.findViewById(R.id.add_img_id);
		// consumeInfo
		mRelLocInfo = (RelativeLayout) findViewById(R.id.position_info_id);
		mRelLocInfo.setOnClickListener(onClickListener);
		mInsertLocIcon = (ImageView) findViewById(R.id.insert_position);
		mInsertLocIcon.setOnClickListener(onClickListener);
		mLocInfo = (TextView) findViewById(R.id.position_info);
		loadLocProgressBar = (ProgressBar) findViewById(R.id.progressBar_loc);
		mPicIndexView = (LinearLayout) findViewById(R.id.pic_index);

		mRelPriceInfo = (RelativeLayout) findViewById(R.id.price_info_id);
		mRelPriceInfo.setOnClickListener(onClickListener);
		mInsertPriceIcon = (ImageView) findViewById(R.id.insert_consume);
		mInsertPriceIcon.setOnClickListener(onClickListener);
		mPriceInfo = (TextView) findViewById(R.id.price_info);

		mHasImageArea = findViewById(R.id.has_image_area);
		mBackButton.setOnClickListener(onClickListener);
		mSendButton.setOnClickListener(onClickListener);
		// mHasImageButton.setOnClickListener(onClickListener);
		mInsertImageButton.setOnClickListener(onClickListener);
		mTitleTextView.setText(R.string.publish_title);
		mContentEdit.setOnClickListener(onClickListener);
		addImageView.setOnClickListener(onClickListener);

		mBindSinaBox = (CheckBox) findViewById(R.id.sync_account_sina);
		mBindQQBox = (CheckBox) findViewById(R.id.sync_account_qq);
		mBindRenrenBox = (CheckBox) findViewById(R.id.sync_account_renren);
		isSyncPage = true;//放在updateAllSyncBoxStatus前
		updateAllSyncBoxStatus();
		initSyncBoxCheckListener(onClickListener);
		loadProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		setViewDisable(mSendButton);
	}

	protected void goBack() {
		if (!InputUtil.isEmpty(mPublicData.getContent())
				|| mPublicData.getContentImage().size() > 0
				|| (!InputUtil.isEmpty(mPublicData.getConsumePlace()) && !mPublicData
						.getConsumePlace().equals(address))
				|| !InputUtil.isEmpty(mPublicData.getConsumePrice())) {
			hideSoft();
			startSaveDialogPage();
		} else {
			needSaveWhenPause = false;
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void startSaveDialogPage() {
		Intent intent = new Intent(this, SaveDraftDialogPage.class);
		startActivityForResult(intent, REQUEST_SAVE_DRAFT);
	}

	private void initInfoFromDraft() {
		ActionController.post(PublishPage.this, SearchDraftAction.class, null,
				new SearchDraftAction.IGetDraftResultListener() {
					public void onFinish(final PublishData data) {
						mPublicData = new PublishData();
						SharedPreferences sp = PublishPage.this
								.getSharedPreferences(
										SystemConfig.XML_FILE_LOCATION_GUIDE,
										Context.MODE_WORLD_READABLE);
						address = sp.getString(SystemConfig.KEY_LOCATION_NAME,
								"");
						if (TextUtils.isEmpty(data.getConsumePlace())
								&& TextUtils.isEmpty(data.getConsumePrice())
								&& TextUtils.isEmpty(data.getContent())
								&& (data.getContentImage() == null || data
										.getContentImage().size() == 0)) {
							Bundle bundle = getIntent().getExtras();
							if (bundle != null) {
								String content = bundle
										.getString(KEY_FROM_TOPIC);
								if (!InputUtil.isEmpty(content)) {
									mPublicData
											.setContent("#" + content + "# ");

								}
								String contentImage = bundle
										.getString(KEY_FROM_OUTSIDE_SHARE);
								ImageData imageData = new ImageData();
								if (!InputUtil.isEmpty(contentImage)) {
									imageData.imagePath = contentImage;
									mPublicData.addContentImage(imageData);
								}
								mHandler.post(new Runnable() {
									public void run() {
										onDateChanged(mPublicData);
									}
								});
							}
							if (!TextUtils.isEmpty(address)) {
								mHandler.post(new Runnable() {
									public void run() {
										setSelectorLocation(address);
										mPublicData.setConsumePlace(address);
									}
								});

							}
							return;
						}
						mPublicData = data;
						// mPublicData.setTokens(tokens);
						mHandler.post(new Runnable() {
							public void run() {
								onDateChanged(data);
							}
						});
					}
				}, true);
	}

	private void onDateChanged(PublishData data) {
		String content = data.getContent();
		ArrayList<ImageData> contentImage = data.getContentImage();
		String consumePlace = data.getConsumePlace();
		String consumePrice = data.getConsumePrice();

		InputUtil.LOGD(TAG, " onDateChanged <content> " + content);
		InputUtil.LOGD(TAG, " onDateChanged <image> " + contentImage);
		InputUtil.LOGD(TAG, " onDateChanged <place> " + consumePlace);
		InputUtil.LOGD(TAG, " onDateChanged <price> " + consumePrice);
		if (!InputUtil.isEmpty(content)) {
			SpannableString ss = new SpannableString(content);
			mContentEdit.setText("");
			Editable editable = mContentEdit.getText();
			// 解析表情，循环解析每一个表情
			for (String emoChar : EmotionHelper.emoChars) {
				int start = 0;
				int index;
				// 查找是否含有该表情字符串
				while ((index = content.indexOf(emoChar, start)) != -1) {
					// int index = content.indexOf(emoChar, start);
					// 如果有则换成对应表情
					Drawable drawable = getResources().getDrawable(
							EmotionHelper.getEmoImgByChar(emoChar));
					float density = ApplicationManager.getInstance()
							.getDensity();
					drawable.setBounds(0, 0,
							(int) (FACE_DEFAULT_SIZE * density),
							0 + (int) (FACE_DEFAULT_SIZE * density));
					// 得到imageSpan，插入SpannableString
					ImageSpan span = new ImageSpan(drawable,
							ImageSpan.ALIGN_BASELINE);
					ss.setSpan(span, index, index + emoChar.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					start = index + emoChar.length();
				}
			}
			editable.append(ss);
		} else {
			mContentEdit.setText("");
		}
		if (contentImage.size() > 0) {
			// TODO
			if (imageViewList == null) {
				imageViewList = new ArrayList<ImageView>();
			}
			imageViewList.clear();
			mPicIndexView.removeAllViews();
			setViewVisiable(mHasImageArea);
			hideView(mInsertImageView);
			mImageList.clear();
			ImageView imageview = null;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8,
					8);
			params.setMargins(0, 0, 4, 0);
			for (int i = 0; i < contentImage.size(); i++) {
				imageview = new ImageView(this);
				imageview.setLayoutParams(params);
				imageview.setImageResource(R.drawable.pic_index_normal);
				imageview.setScaleType(ScaleType.FIT_XY);
				mImageList.add((ImageData) contentImage.get(i));
				mPicIndexView.addView(imageview);
				imageViewList.add(imageview);
			}
			mImageAdapter.notifyDataSetChanged();
			mImageGallery.setSelection(mImageList.size() - 1);
			imageview = imageViewList.get(imageViewList.size() - 1);
			imageview.setImageResource(R.drawable.pic_index_selector);
			if (contentImage.size() >= 8) {
				addImageView.setVisibility(View.INVISIBLE);
			} else {
				addImageView.setVisibility(View.VISIBLE);
			}
		} else {
			setViewVisiable(mInsertImageView);
			hideView(mHasImageArea);
		}
		if (!TextUtils.isEmpty(consumePrice)) {
			mInsertPriceIcon.setImageResource(R.drawable.input_priced_sec);
			mPriceInfo.setText(getString(R.string.per_people_consume_price,
					consumePrice));
			mPriceInfo.setTextColor(Color.argb(255, 73, 68, 75));
		} else {
			mInsertPriceIcon.setImageResource(R.drawable.input_price_sec);
			mPriceInfo.setText(R.string.add_price_info);
			mPriceInfo.setTextColor(Color.argb(255, 182, 180, 179));
		}
		if (!TextUtils.isEmpty(consumePlace)) {
			setSelectorLocation(consumePlace);
		} else {
			setDisablePlace();
		}
		if (needSendButtonVisiable()) {
			setViewVisiable(mSendButton);
		} else {
			setViewDisable(mSendButton);
		}
	}

	public void setSelectorLocation(String postion) {
		mInsertLocIcon.setImageResource(R.drawable.input_position_bg_selector);
		mLocInfo.setText(postion);
		mLocInfo.setTextColor(Color.argb(255, 73, 68, 75));
		loadLocProgressBar.setVisibility(View.GONE);
	}

	protected boolean needSendButtonVisiable() {
		return (leftWords >= 0)
				&& (InputUtil.strip(mContentEdit.getText().toString()).length() > 0 || (mPublicData != null && mPublicData
						.getContentImage().size() > 0));
	}

	OnClickListener onClickListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				goBack();
				break;
			case R.id.add_img_id:
				startImageSearchPage();
				break;
			case R.id.send:
				send(mPublicData);
				break;
			case R.id.insert_image:
				startImageSearchPage();
				break;
			case R.id.edit:
				startInputTextPage();
				break;
			case R.id.position_info_id:
				startMapPage();
				break;
			case R.id.insert_position:
				if (!TextUtils.isEmpty(mPublicData.getConsumePlace())) {
					setDisablePlace();
				}else{
					startMapPage();
				}
				break;
			case R.id.price_info_id:
				startNewConsumePage();
				break;
			case R.id.insert_consume:
				if (!TextUtils.isEmpty(mPublicData.getConsumePrice())) {
					mPublicData.setConsumePrice("");
					onDateChanged(mPublicData);
				}else{
					startNewConsumePage();
				}
				break;
			case R.id.sync_account_sina:
				openThirdAuthPageOrUnbind(R.id.sync_account_sina, false);
				break;
			case R.id.sync_account_qq:
				openThirdAuthPageOrUnbind(R.id.sync_account_qq, false);
				break;
			case R.id.sync_account_renren:
				openThirdAuthPageOrUnbind(R.id.sync_account_renren, false);
				break;
			}
		}
	};
	private void setDisablePlace() {
		mInsertLocIcon.setImageResource(R.drawable.input_position_add_selector);
		mLocInfo.setText(R.string.add_location_info);
		mLocInfo.setTextColor(Color.argb(255, 182, 180, 179));
		mPublicData.setConsumePlace(null);
		mPublicData.setPlid(null);
		mPublicData.setLatitude(0);
		mPublicData.setLongitude(0);
	}
	private void send(PublishData data) {
		if (!NetworkStatus.isNetworkAvailable(this)) {
			showToast(R.string.common_network_not_available);
			return;
		}
		setViewDisable(mSendButton);
		data.setTokens(tokens);
		ApplicationManager.getInstance().send(data);
		showToast(R.string.publish_sending_lable);
		saveData(false, false);
		finish();
	}

	private synchronized void saveData(final boolean isSave,
			final boolean needShowToast) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (mPublicData == null) {
			mPublicData = new PublishData();
		}
		if (!isSave) {
			mPublicData = new PublishData();
			needSaveWhenPause = false;
		}
		params.put(DraftManager.KEY_CONTENT, mPublicData.getContent());
		params.put(DraftManager.KEY_CONTENT_IMAGE,
				mPublicData.getContentImage());
		params.put(DraftManager.KEY_CONSUME_AV_PRICE,
				mPublicData.getConsumePrice());
		params.put(DraftManager.KEY_CONSUME_PLACE,
				mPublicData.getConsumePlace());
		params.put(DraftManager.KEY_POS_LATITUDE, mPublicData.getLatitude());
		params.put(DraftManager.KEY_POS_LONGITUDE, mPublicData.getLongitude());
		params.put(DraftManager.KEY_MANY_PEOPLE, mPublicData.isManyPeople());
		params.put(DraftManager.KEY_CONSUME_PEOPLE_NUM, mPublicData.mPeopleNum);
		params.put(DraftManager.KEY_CONSUME_PRICE, mPublicData.mConsumePrice);
		params.put(DraftManager.KEY_POS_ID, mPublicData.getPlid());
		ActionController.post(PublishPage.this, SaveDraftAction.class, params,
				new SaveDraftAction.ISaveDraftResultListenter() {
					@Override
					public void onFinish() {
						if (isSave && needShowToast)
							showToast(PublishPage.this,
									R.string.publish_save_succ);
					}
				});
	}

	private void startNewConsumePage() {
		Intent intent = new Intent(this, ConsumerExperiencePage.class);
		intent.putExtra(CONSUME_PRICE, mPublicData.mConsumePrice);
		intent.putExtra(CONSUME_PEOPLE_NUM, mPublicData.mPeopleNum);
		startActivityForResult(intent, REQUEST_NEW_CONSUME);
	}

	private void startImageSearchPage() {
		Intent intent = new Intent(this, TakePicturePage.class);
		intent.putExtra(TakePicturePage.KEY_OPERATE_TYPE,
				TakePicturePage.OPERATE_TYPE_ADD);
		startActivityForResult(intent, REQUEST_NEW_IMAGE);
	}

	private void startInputTextPage() {
		Intent intent = new Intent(this, InputTextContentPage.class);
		intent.putExtra(KEY_PARAMS_CONTENT, mPublicData.getContent());
		startActivityForResult(intent, REQUEST_INPUT_TEXTCONTENT);
	}

	private void startMapPage() {
		Intent intent = new Intent(this, MapPage.class);
		intent.putExtra(MapPage.KEY_PlACE_NAME, mPublicData.getConsumePlace());
		intent.putExtra(MapPage.KEY_PID, mPublicData.getPlid());
		startActivityForResult(intent, REQUEST_LOCATION);
	}

	private void startShowImagePage(String path) {
		Intent intent = new Intent(this, ViewPicturePage.class);
		intent.putExtra(ViewPicturePage.EXTRA_IMG_PATH, path);
		intent.putExtra(ViewPicturePage.FROM_KEY, ViewPicturePage.FROM_INPUT);
		intent.putExtra(ViewPicturePage.EXTRA_ROTATE, true);
		InputUtil.LOGD(TAG,
				" enter startShowImagePage with data <CONTENT IMAGE PATH> : "
						+ mPublicData.getContentImage());
		startActivityForResult(intent, REQUEST_SHOW_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		InputUtil.LOGD(TAG, " enter onActivityResult <requestCode> : "
				+ requestCode);
		InputUtil.LOGD(TAG, " enter onActivityResult <resultCode> : "
				+ resultCode);
		InputUtil.LOGD(TAG, " enter onActivityResult <data> : " + data);
		switch (requestCode) {
		case REQUEST_SHOW_IMAGE:
			if (resultCode == RESULT_OK) {
				if (!TextUtils.isEmpty(delPath)) {
					mPublicData.delContentImage(delPath);
					onDateChanged(mPublicData);
				}
			} else if (resultCode == ViewPicturePage.RESULT_CODE_ROTATE) {
				onDateChanged(mPublicData);
			}
			break;
		case REQUEST_NEW_IMAGE:
			if (data != null) {
				String path = data.getExtras().getString(
						TakePicturePage.RESULT_PATH);
				if (!TextUtils.isEmpty(path)) {
					InputUtil.LOGD(TAG,
							" onActivityResult from REQUEST_IMAGE <path> : "
									+ path);
					showToast(R.string.publish_image_succ);
					ImageData imageData = new ImageData();
					imageData.imagePath = path;
					mPublicData.addContentImage(imageData);
					onDateChanged(mPublicData);
				} else {
					showToast(R.string.publish_image_fail);
				}
			}
			break;
		case REQUEST_NEW_CONSUME:
			if (data != null) {
				showToast(R.string.consume_succ);
				String averagePrice = (String) data.getExtras().get(
						CONSUME_AV_PRICE);
				if (!InputUtil.isEmpty(averagePrice)) {
					mPublicData.setConsumePrice(averagePrice);
					mPublicData.mPeopleNum = (String) data.getExtras().get(
							CONSUME_PEOPLE_NUM);
					mPublicData.mConsumePrice = (String) data.getExtras().get(
							CONSUME_PRICE);
					onDateChanged(mPublicData);
				}
			}
			break;
		case REQUEST_SAVE_DRAFT:
			if (resultCode == RESULT_OK) {
				saveData(true, true);
				needSaveWhenPause = false;
				finish();
			} else if (resultCode == SaveDraftDialogPage.RESULT_NOT_OK) {
				saveData(false, false);
				needSaveWhenPause = false;
				finish();
			}
			break;
		case REQUEST_LOCATION:
			if (data != null &&resultCode == RESULT_OK) {
				String address = data.getStringExtra(MapPage.KEY_PlACE_NAME);
				int latitude = data.getIntExtra(MapPage.KEY_LAT, 0);
				int longitude = data.getIntExtra(MapPage.KEY_LON, 0);
				String plid = data.getStringExtra(MapPage.KEY_PID);
				mPublicData.setConsumePlace(address);
				mPublicData.setLatitude(latitude);
				mPublicData.setLongitude(longitude);
				mPublicData.setPlid(plid);
				onDateChanged(mPublicData);
			}

			break;
		case REQUEST_INPUT_TEXTCONTENT:
			if (data != null && resultCode == RESULT_OK) {
				String content = (String) data.getExtras().get(
						KEY_PARAMS_CONTENT);
				if (!TextUtils.isEmpty(content)) {
					mPublicData.setContent(content);
					onDateChanged(mPublicData);
				} else {
					mPublicData.setContent("");
					onDateChanged(mPublicData);
				}

			}
			break;

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		((LehoApp) PublishPage.this.getApplication()).requestStopLocation();
		isSaveThreadRunning = false;
		if (needSaveWhenPause) {
			saveData(true, false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		((LehoApp) PublishPage.this.getApplication()).requestLocation();
		isSaveThreadRunning = true;
		needSaveWhenPause = true;
		Thread saveDraft = new Thread() {
			public void run() {
				int i = 0;
				while (isSaveThreadRunning) {
					try {
						// 第一次进入输入页，先等待默认时间
						if (i == 0) {
							Thread.sleep(DEFAULT_SAVE_TIME);
							i++;
							continue;
						}
						if (mContentEdit.getText().length() > 0) {
							saveData(true, false);
						}
						Thread.sleep(DEFAULT_SAVE_TIME);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
//		saveDraft.start();
	}

	public Bitmap cropBitmap(Bitmap src, int width) {
		int imageWidth = src.getWidth();
		int imageHeight = src.getHeight();

		if (imageWidth < imageHeight) {
			if (width > imageWidth) {
				width = imageWidth;
			}
		} else {
			if (width > imageHeight) {
				width = imageHeight;
			}
		}

		int srcX = (imageWidth - width) / 2;
		int srcY = (imageHeight - width) / 2;

		Bitmap croppedImage;
		croppedImage = Bitmap.createBitmap(width, width,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(croppedImage);

		Rect srcRect = new Rect(srcX, srcY, width + srcX, width + srcY);
		Rect dstRect = new Rect(0, 0, width, width);

		canvas.drawBitmap(src, srcRect, dstRect, null);
		src.recycle();

		return croppedImage;
	}

	// private List<SyncThirdData> datas = new ArrayList<SyncThirdData>(0);
	// private SyncSettingResultListener syncSettingListener = new
	// SyncSettingResultListener() {
	// @Override
	// public void onStart() {
	// hideOrShowLoadingView(View.VISIBLE);
	// }
	//
	// @Override
	// public void onFinish(int resourceID, SupportSite site,
	// RequestType requestType, String thirdUid) {
	// hideOrShowLoadingView(View.GONE);
	// updataUiBySite(site, requestType, thirdUid);
	// }
	//
	// @Override
	// public void onFail(int resourceID) {
	// hideOrShowLoadingView(View.GONE);
	// showToast(resourceID);
	// }
	//
	// @Override
	// public void onGetSyncListFinish(List<SyncThirdData> datas) {
	// hideOrShowLoadingView(View.GONE);
	// if (datas == null) {
	// return;
	// }
	// PublishPage.this.datas = datas;
	// for (SyncThirdData data : datas) {
	// data.setFlag("1");
	// updateUI(data);
	// }
	// }
	//
	// @Override
	// public void onFail(int resourceID, SupportSite site) {
	// onFail(resourceID);
	// updataUiBySite(site, null, null);
	// }
	// };

	// private void updataUiBySite(SupportSite site, RequestType requestType,
	// String thirdUid) {
	// SyncThirdData data;
	// switch (site) {
	// case SINA:
	// data = (SyncThirdData) mSyncSinaBox.getTag();
	// break;
	// case QQ:
	// data = (SyncThirdData) mSyncQQBox.getTag();
	// break;
	// case RENREN:
	// data = (SyncThirdData) mSyncRenRenBox.getTag();
	// break;
	// default:
	// return;
	// }
	// if (requestType != null) {
	// switch (requestType) {
	// case UNBINDING_THIRD_ACCOUNT:
	// data.setThirdUid("");
	// break;
	// case BINDING_THIRD_ACCOUNT:
	// data.setThirdUid(thirdUid);
	// break;
	// default:
	// break;
	// }
	// }
	// updateUI(data);
	// }

	private void hideOrShowLoadingView(final int showOrHidn) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				loadProgressBar
						.setVisibility(showOrHidn == View.VISIBLE ? View.VISIBLE
								: View.GONE);
			}
		});
	}

	// @Override
	// public void onAuthResult(BaseToken token) {
	// System.out.println("---------------------------------------");
	// // super.onAuthResult(tokenData);
	// if (token == null) {
	// if (datas != null) {
	// for (SyncThirdData data : datas) {
	// updateUI(data);
	// }
	// }
	// return;
	// }
	// Log.d(TAG, "token.site.getSiteTag:" + token.site.getSiteTag() + "\t"
	// + "token.token:" + token.token + "\t" + "token.userid"
	// + token.userid);
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put(SyncSettingAction.REQUEST_TYPE,
	// RequestType.BINDING_THIRD_ACCOUNT);
	// params.put(SyncSettingAction.THIRD_UID, token.userid);
	// params.put(SyncSettingAction.SUPPORT_SITE, token.site);
	// params.put(SyncSettingAction.ACCESS_TOKEN, token.token);
	// params.put(SyncSettingAction.REFRESH_TOKEN, token.reFreshToken);
	// params.put(SyncSettingAction.EXP_TIME, token.expTime);
	// bindOrUnbindThirdAccont(params);
	// }

	// private void loadThirdAccountList() {
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put(SyncSettingAction.REQUEST_TYPE,
	// RequestType.THIRD_SYNC_LIST);
	// ActionController.post(PublishPage.this,
	// SyncSettingAction.class, params, syncSettingListener,
	// true);
	// }
	// });
	// }

	// private void openThirdAuthPageOrUnbind(int btnId) {
	// View v = findViewById(btnId);
	// SyncThirdData data = (SyncThirdData) v.getTag();
	// if (data == null)
	// return;
	// SupportSite site = data.getSite();
	// String thirdUid = data.getThirdUid();
	// if (thirdUid == null || "".equals(thirdUid)) {
	// LoginUtil.openThirdAuthPage(this, site, true);
	// } else {
	// String flag = data.getFlag();
	// data.setFlag("1".equals(flag) ? "0" : "1");
	// updateUI(data);
	// }
	// }

	// private void bindOrUnbindThirdAccont(final Map<String, Object> params) {
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// ActionController.post(PublishPage.this,
	// SyncSettingAction.class, params, syncSettingListener,
	// true);
	// }
	// });
	// }

	// private void updateUI(final SyncThirdData data) {
	// if (data == null)
	// return;
	// String thirdUid = data.getThirdUid();
	// final boolean isEnable = (thirdUid == null || "".equals(thirdUid)) ?
	// false
	// : true;
	// final boolean isChecked = "1".equals(data.getFlag());
	// for (SyncThirdData d : datas) {
	// if (d.getSite() == data.getSite()) {
	// // Collections.replaceAll(datas, d, data);
	// d = data;
	// }
	// }
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// switch (data.getSite()) {
	// case SINA:
	// if (isChecked) {
	// setCheckBoxEnableAndTag(mSyncSinaBox, true, isChecked,
	// data);
	// setCheckBoxEnableAndTag(mSyncQQBox, false, false, data);
	// setCheckBoxEnableAndTag(mSyncRenRenBox, false, false,
	// data);
	// } else {
	// setCheckBoxEnableAndTag(mSyncSinaBox, true, isChecked,
	// data);
	// setCheckBoxEnableAndTag(mSyncQQBox, true, isChecked,
	// data);
	// setCheckBoxEnableAndTag(mSyncRenRenBox, true,
	// isChecked, data);
	// }
	// break;
	// case RENREN:
	// if (isChecked) {
	// setCheckBoxEnableAndTag(mSyncRenRenBox, true,
	// isChecked, data);
	// setCheckBoxEnableAndTag(mSyncSinaBox, false, false,
	// data);
	// setCheckBoxEnableAndTag(mSyncQQBox, false, false, data);
	// } else {
	// setCheckBoxEnableAndTag(mSyncRenRenBox, true,
	// isChecked, data);
	// setCheckBoxEnableAndTag(mSyncSinaBox, true, isChecked,
	// data);
	// setCheckBoxEnableAndTag(mSyncQQBox, true, isChecked,
	// data);
	// }
	// break;
	// case QQ:
	// if (isChecked) {
	// setCheckBoxEnableAndTag(mSyncQQBox, true, isChecked,
	// data);
	// setCheckBoxEnableAndTag(mSyncRenRenBox, false, false,
	// data);
	// setCheckBoxEnableAndTag(mSyncSinaBox, false, false,
	// data);
	// } else {
	// setCheckBoxEnableAndTag(mSyncQQBox, true, isChecked,
	// data);
	// setCheckBoxEnableAndTag(mSyncRenRenBox, true,
	// isChecked, data);
	// setCheckBoxEnableAndTag(mSyncSinaBox, true, isChecked,
	// data);
	// }
	// break;
	// default:
	// break;
	// }
	// }
	// });
	// }

	// private void setCheckBoxEnableAndTag(CheckBox box, boolean enable,
	// boolean checked, SyncThirdData tag) {
	// box.setEnabled(true);
	// if (enable) {
	// box.setClickable(enable);
	// box.setChecked(checked);
	// } else {
	// box.setVisibility(View.GONE);
	// box.setChecked(checked);
	// box.setClickable(enable);
	// box.setEnabled(enable);
	// }
	// SyncThirdData otherTag = (SyncThirdData) box.getTag();
	// if (enable && (otherTag != null && otherTag.getSite() == tag.getSite()))
	// {
	// box.setTag(tag);
	// } else {
	// ((SyncThirdData) box.getTag()).setThirdUid("");
	// }
	// }

	protected void setViewDisable(View view) {
		view.setClickable(false);
		view.setEnabled(false);
	}

	protected void setViewVisiable(View view) {
		view.setVisibility(View.VISIBLE);
		view.setClickable(true);
		view.setEnabled(true);
	}

	protected void hideView(View view) {
		view.setVisibility(View.GONE);
	}

	protected void hideSoft() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mContentEdit.getWindowToken(), 0);
	}

	public class ImageAdapter extends BaseAdapter {

		private Context context;
		private int count;
		private List<ImageData> list = new ArrayList<ImageData>();

		private final class ViewHolder {
			public ImageView albumItemImage;
		}

		public ImageAdapter(Context context, List<ImageData> data) {
			this.context = context;
			this.list = data;
			count = data.size();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (position != 0) {
				return list.get(position);
			} else {
				return null;
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (list == null || list.size() == 0 || (position >= list.size())) {
				return convertView;
			}
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.input_image_item_layout, null);
				holder = new ViewHolder();
				holder.albumItemImage = (ImageView) convertView
						.findViewById(R.id.album_img_id);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ImageData data = list.get(position);
			Bitmap bitmap = BitmapFactory.decodeFile(data.imagePath);
			if (bitmap != null) {
				int width = mInsertImageView.getWidth();
				if (width == 0) {
					if (width == 0) {
						float padding = getResources().getDimension(
								R.dimen.publish_input_image_padding);
						width = (int) (ApplicationManager.getInstance()
								.getWidth() - padding * 2);
					}
				}
				if (width > 0) {
					bitmap = cropBitmap(bitmap, width);
				}
//				bitmap = ImageEffect.createCornorBitmap(bitmap, 2);
				holder.albumItemImage.setImageBitmap(bitmap);
			}
			return convertView;
		}
	}
}
