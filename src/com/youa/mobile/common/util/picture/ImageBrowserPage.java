package com.youa.mobile.common.util.picture;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;

public class ImageBrowserPage extends BasePage implements OnItemClickListener, OnClickListener {

	public static final String KEY_IMAGES = "K";
	private ArrayList<ImageData> mImageList = new ArrayList<ImageData>();
	private ImageBrowserGallery mGallery;
	private TextView mPictureCount;
	private Button mButton;
	private int mCurrentIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mImageList = bundle.getParcelableArrayList(KEY_IMAGES);
		}
		if (mImageList == null || mImageList.size() < 1) {
			finish();
		}
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
		setContentView(R.layout.view_image_browser);
		LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.dimAmount = 0.8f;
		layoutParams.x = 0;
		layoutParams.y = 0;
		layoutParams.width = -1;
		layoutParams.height = -1;
		getWindow().setAttributes(layoutParams);
		initView();
	}
	private void initView() {
		mPictureCount = (TextView) findViewById(R.id.picture_count);
		if (mImageList.size() <= 1) {
			mPictureCount.setVisibility(View.GONE);
		}
		mButton = (Button) findViewById(R.id.go_to_picture);
		mButton.setOnClickListener(this);
		mGallery = (ImageBrowserGallery) findViewById(R.id.images);
		mGallery.setOnItemClickListener(this);
		mGallery.setOnItemSelectedListener(mImageSelectedListener);
		mGallery.setAdapter(new ImageBrowserAdapter(this, mImageList));
	}

	OnItemSelectedListener mImageSelectedListener = new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mCurrentIndex = position;
			mPictureCount.setText(position + 1 +"/"+mImageList.size());
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		finish();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, ViewPicturePage.class);
		Bundle bundle = new Bundle();
		bundle.putParcelableArray(ViewPicturePage.EXTRA_IMG_ARRAY, changeToArray(mImageList));
		intent.putExtras(bundle);
		intent.putExtra(ViewPicturePage.EXTRA_IMG_SELECTPOSITION, mCurrentIndex);
		startActivity(intent);
		finish();
	}

	private ImageData[] changeToArray(ArrayList<ImageData> mImageList) {
		ImageData[] datas = new ImageData[mImageList.size()];
		for (int i = 0; i < mImageList.size(); i ++) {
			datas[i] = mImageList.get(i);
		}
		return datas;
	}
}
