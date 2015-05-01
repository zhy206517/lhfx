package com.youa.mobile.common.util.picture;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.login.widget.CustomGallery;

//显示图片页面
public class ViewPicturePage extends BasePage implements OnTouchListener, OnClickListener {
	public static final String ACTION = LehuoIntent.ACTION_VIEWPICTURE;
	public static final String EXTRA_IMG_ARRAY = "extra_imgs_array";
	public static final String EXTRA_IMG_INDEX = "extra_imgs_index";
	public static final String EXTRA_IMG_SELECTPOSITION = "extra_imgs_position";
	public static final String EXTRA_IMG_PATH = "extra_imgs_path";
	public static final String EXTRA_ROTATE = "extra_rotate";
	protected static final int ANIM_DISAPPEAR = 0;
	protected static final int ANIM_APPEAR = 1;
	public static final String FROM_KEY = "from";
	public static final int FROM_INPUT = 1;
	public static final int FROM_VIEW = 0;
	private static final int MSG_ROTATE_LEFT = 1;
	private static final int MSG_ROTATE_RIGHT = 2;
	public static final int RESULT_CODE_ROTATE = 10;
	private static final int DIALOG_WAITING = 1;
	// int orentation = 0;

	public final static long DEFAULT_TIME = 200;
	private final static int TITLE_HIGHT = 50;

	private AnimationSet mTopOutAnimation = new AnimationSet(true);
	private AnimationSet mTopInAnimation = new AnimationSet(true);
	private AnimationSet mBottomOutAnimation = new AnimationSet(true);
	private AnimationSet mBottomInAnimation = new AnimationSet(true);
	// private AnimationSet mDescOutAnimation = new AnimationSet(true);
	// private AnimationSet mDescInAnimation = new AnimationSet(true);

	private View mTitleView;
	private View mButtonAreaView;
	private Button mRotateLeftButton;
	private Button mRotateRightButton;
	private CustomGallery mGallery;
	private List<ImageData> mImageList = new ArrayList<ImageData>();
	private TextView mPictureCount;
	private ImageButton back;
	private ImageButton downLoad;
	private ImageButton delBtn;
	private PictureItemAdapter mGalleryAdapter;
	private int selectPosition = -1;
	private boolean mRotate;
	private boolean mHasRotate;
	private int section;
	private PhotoDownLoadHandler photoHandler;

	private Handler mPageHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ROTATE_LEFT: {

				try {
					ImageData imageData = mImageList.get(0);
					Bitmap bitmap = ImageUtil.decode(imageData.path, imageData.angle);
					Bitmap rotateBitmap = rotateBitmap(bitmap, -90);
					bitmap.recycle();
					bitmap = null;

					rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(imageData.path));
					rotateBitmap.recycle();
					rotateBitmap = null;

					mGalleryAdapter.notifyDataSetChanged();
					mHasRotate = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				dismissProgressDialog();
				break;
			}
			case MSG_ROTATE_RIGHT: {
				try {
					ImageData imageData = mImageList.get(0);
					Bitmap bitmap = ImageUtil.decode(imageData.path, imageData.angle);
					Bitmap rotateBitmap = rotateBitmap(bitmap, 90);
					bitmap.recycle();
					bitmap = null;

					rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(imageData.path));
					rotateBitmap.recycle();
					rotateBitmap = null;

					mGalleryAdapter.notifyDataSetChanged();
					mHasRotate = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				dismissProgressDialog();
				break;
			}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (savedInstanceState != null) {
			mRotate = savedInstanceState.getBoolean(EXTRA_ROTATE);
		} else {
			mRotate = getIntent().getBooleanExtra(EXTRA_ROTATE, false);
		}
		photoHandler = new PhotoDownLoadHandler();
		Log.d("seememory", "ViewPicturePage-onCreate:"
				+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGalleryAdapter.destory();
		Log.d("seememory", "ViewPicturePage-onDestroy:"
				+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_ROTATE, mRotate);
	}

	private void showProgressDialog() {
		try {
			showDialog(DIALOG_WAITING);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dismissProgressDialog() {
		try {
			dismissDialog(DIALOG_WAITING);
		} catch (Exception e) {
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle(null);
			dialog.setMessage(getString(R.string.wait));
			dialog.setIndeterminate(false);
			dialog.setCancelable(false);
			return dialog;
		}
		default:
			return super.onCreateDialog(id);
		}
	}

	/*
	 * Animation anim_appear; Animation anim_disappear;
	 */

	private void initAmination() {

		TranslateAnimation tranAnim = new TranslateAnimation(0, 0, 0, -TITLE_HIGHT);
		AlphaAnimation alphAnim = new AlphaAnimation(1, 0);
		tranAnim.setDuration(DEFAULT_TIME);
		alphAnim.setDuration(DEFAULT_TIME);
		mTopOutAnimation.setFillAfter(true);
		mTopOutAnimation.addAnimation(tranAnim);
		mTopOutAnimation.addAnimation(alphAnim);

		tranAnim = new TranslateAnimation(0, 0, -TITLE_HIGHT, 0);
		alphAnim = new AlphaAnimation(0, 1);
		tranAnim.setDuration(DEFAULT_TIME);
		alphAnim.setDuration(DEFAULT_TIME);
		mTopInAnimation.setFillAfter(true);
		mTopInAnimation.addAnimation(tranAnim);
		mTopInAnimation.addAnimation(alphAnim);

		float height = getResources().getDimension(R.dimen.rotate_area_height);
		TranslateAnimation bottomTranAnim = new TranslateAnimation(0, 0, 0, height);
		AlphaAnimation bottomAlphAnim = new AlphaAnimation(1, 0);
		bottomTranAnim.setDuration(DEFAULT_TIME);
		bottomAlphAnim.setDuration(DEFAULT_TIME);
		mBottomOutAnimation.setFillAfter(true);
		mBottomOutAnimation.addAnimation(bottomTranAnim);
		mBottomOutAnimation.addAnimation(bottomAlphAnim);

		bottomTranAnim = new TranslateAnimation(0, 0, height, 0);
		bottomAlphAnim = new AlphaAnimation(0, 1);
		bottomTranAnim.setDuration(DEFAULT_TIME);
		bottomAlphAnim.setDuration(DEFAULT_TIME);
		mBottomInAnimation.setFillAfter(true);
		mBottomInAnimation.addAnimation(bottomTranAnim);
		mBottomInAnimation.addAnimation(bottomAlphAnim);
	}

	@Override
	protected void onStart() {
		mImageList = getImgsDataFromIntent(getIntent());
		initAmination();
		initUI();
		if (selectPosition >= 0 && selectPosition <= mImageList.size()) {
			mGallery.setSelection(selectPosition);
		}
		super.onStart();
	}

	private void initUI() {
		setContentView(R.layout.view_picture);
		mButtonAreaView = findViewById(R.id.button_area);
		mRotateLeftButton = (Button) findViewById(R.id.btn_left);
		mRotateLeftButton.setOnClickListener(this);
		mRotateRightButton = (Button) findViewById(R.id.btn_right);
		mRotateRightButton.setOnClickListener(this);
		if (mRotate) {
			mButtonAreaView.setVisibility(View.VISIBLE);
		} else {
			mButtonAreaView.setVisibility(View.GONE);
		}
		mTitleView = findViewById(R.id.view_picture_title);
		mGallery = (CustomGallery) findViewById(R.id.image_viewPicture);
		mPictureCount = (TextView) findViewById(R.id.picture_count);
		// titleView = (View)findViewById(R.id.view_picture_title);
		// 初始化出现和消失两个动画
		back = (ImageButton) findViewById(R.id.back);
		downLoad = (ImageButton) findViewById(R.id.view_picture_dowload);
		delBtn = (ImageButton) findViewById(R.id.view_picture_del_btn);
		// anim_disappear = AnimationUtils.loadAnimation(ViewPicturePage.this,
		// R.anim.alpha_anim_disappear);
		// anim_disappear.setFillEnabled(true);
		// anim_disappear.setFillAfter(true);
		// anim.setFillBefore(true);
		// anim_appear = AnimationUtils.loadAnimation(ViewPicturePage.this,
		// R.anim.alpha_anim_appear);
		// anim_appear.setFillEnabled(true);
		// anim_appear.setFillAfter(true);
		back.setOnClickListener(this);
		downLoad.setOnClickListener(this);
		delBtn.setOnClickListener(this);
		mGalleryAdapter = new PictureItemAdapter(ViewPicturePage.this, mImageList);
		mGallery.setAdapter(mGalleryAdapter);

		mGallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (mGalleryAdapter.isIn) {
					mTitleView.startAnimation(mTopOutAnimation);
					mGalleryAdapter.isIn = false;
					mButtonAreaView.startAnimation(mBottomOutAnimation);
				} else {
					mTitleView.startAnimation(mTopInAnimation);
					mGalleryAdapter.isIn = true;
					mButtonAreaView.startAnimation(mBottomInAnimation);
				}
				mGalleryAdapter.notifyDataSetChanged();
			}
		});

		mGallery.setOnItemSelectedListener(mImageSelectedListener);
		int mFrom = getIntent().getIntExtra(FROM_KEY, FROM_VIEW);
		if (mFrom == FROM_INPUT) {
			delBtn.setVisibility(View.VISIBLE);
			mPictureCount.setVisibility(View.GONE);
			downLoad.setVisibility(View.GONE);
		} else {
			delBtn.setVisibility(View.GONE);
			mPictureCount.setVisibility(View.VISIBLE);
			downLoad.setVisibility(View.VISIBLE);
		}
	}

	OnItemSelectedListener mImageSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (position < 0) {
				position += mImageList.size();
			}
			position = position % mImageList.size();
			section = position;
			mPictureCount.setText(position + 1 + "/" + mImageList.size());
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	};

	private List<ImageData> getImgsDataFromIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		List<ImageData> imgs = new ArrayList<ImageData>();
		if (null == bundle) {
			showToast(R.string.picture_noresource);
		} else {
			String path = bundle.getString(EXTRA_IMG_PATH);
			if (null != path && !"".equals(path)) {
				ImageData img = new ImageData(null, null, path, 0);
				imgs.add(img);
			} else {
				Parcelable[] imgsObjArray = bundle.getParcelableArray(EXTRA_IMG_ARRAY);
				if (null != imgsObjArray) {
					for (Parcelable imgObj : imgsObjArray) {
						ImageData img = (ImageData) imgObj;
						imgs.add(img);
					}
					selectPosition = bundle.getInt(EXTRA_IMG_SELECTPOSITION);
				} else {
					showToast(R.string.picture_noresource);
				}
			}
		}
		return imgs;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (mHasRotate) {
				setResult(RESULT_CODE_ROTATE);
			}
			finish();
			break;
		case R.id.view_picture_dowload:
			if (downLoadDialog == null) {
				downLoadDialog = ProgressDialog.show(ViewPicturePage.this, "保存图片", "图片正在保存中，请稍等...", true);
			} else {
				downLoadDialog.show();
			}

			new Thread(saveFileRunnable).start();
			break;
		case R.id.view_picture_del_btn:
			new AlertDialog.Builder(ViewPicturePage.this)
					.setMessage(getResources().getString(R.string.picture_del_tip))
					.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							setResult(RESULT_OK);
							dialog.dismiss();
							finish();
						}
					}).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			break;
		case R.id.btn_left:
			if (mImageList.size() > 0) {
				ImageData imageData = mImageList.get(0);
				if (imageData != null) {
					showProgressDialog();
					mPageHandle.sendEmptyMessage(MSG_ROTATE_LEFT);
				}
			}
			break;
		case R.id.btn_right:
			if (mImageList.size() > 0) {
				ImageData imageData = mImageList.get(0);
				if (imageData != null) {
					showProgressDialog();
					mPageHandle.sendEmptyMessage(MSG_ROTATE_RIGHT);
				}
			}
			break;
		}
	}

	// ------------------------------下载图片----------------------------------
	private ProgressDialog downLoadDialog;
	private Runnable saveFileRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				ImageData imageData = mImageList.get(section);
				Bitmap bitmap = mGalleryAdapter.bitmap;
				if (saveFile(bitmap, imageData.id + ".png")) {
					photoHandler.sendEmptyMessage(1);
					// bitmap.recycle();
					bitmap = null;
				}
			} catch (IOException e) {

				photoHandler.sendEmptyMessage(2);
				e.printStackTrace();
			}
			// messageHandler.sendMessage(messageHandler.obtainMessage());
		}

	};

	public boolean saveFile(Bitmap bm, String fileName) throws IOException {
		OutputStream outputStream = openFileOutStream(fileName);
		if (bm==null||outputStream == null) {
			return false;
		}
		BufferedOutputStream bos = new BufferedOutputStream(outputStream);
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
		return true;
	}

	private FileOutputStream openFileOutStream(String fileName) throws FileNotFoundException {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return this.openFileOutput(fileName, Context.MODE_PRIVATE);
		}
		File sdFile = Environment.getExternalStorageDirectory();
		File dirFile = new File(sdFile.getAbsolutePath() + File.separator + "LeHuoStore");
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		dirFile = new File(dirFile, "photo");
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File file = new File(dirFile, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			photoHandler.sendEmptyMessage(3);
			return null;
		}
		return new FileOutputStream(file);
	}

	class PhotoDownLoadHandler extends Handler {
		public void handleMessage(android.os.Message msg) {
			downLoadDialog.dismiss();
			switch (msg.what) {
			case 1:
				Toast.makeText(ViewPicturePage.this, "图片保存成功！", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(ViewPicturePage.this, "图片保存失败！", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(ViewPicturePage.this, "图片已经保存！", Toast.LENGTH_SHORT).show();
				break;
			}

		}
	}

	// ------------------------------下载图片----------------------------------

	@Override
	public void onBackPressed() {
		if (mHasRotate) {
			setResult(RESULT_CODE_ROTATE);
		}
		super.onBackPressed();
	}

	private Bitmap rotateBitmap(Bitmap src, float rotate) {
		Matrix matrix = new Matrix();
		matrix.setRotate(rotate);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "左");
		menu.add(Menu.NONE, 1, 0, "右");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ImageData img = mImageList.get(mGallery.getSelectedItemPosition()%mImageList.size());
		switch (item.getItemId()) {
		case 0:
			img.angle += -90;
			break;
		case 1:
			img.angle += 90;
			break;
		}
		mGalleryAdapter.notifyDataSetChanged();
		return true;
	}

}
