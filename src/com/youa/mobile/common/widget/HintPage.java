package com.youa.mobile.common.widget;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BasePage;

public class HintPage extends BasePage implements OnClickListener{
	public int backID = -1;
	public static final String KEY_BACKGROUNDID = "backgroundid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backID = getIntent().getIntExtra(KEY_BACKGROUNDID, -1);
		if(backID == -1) {
			throw new IllegalArgumentException("please set BasePage.KEY_BACKGROUNDID");
		}
		setContentView(R.layout.hint_page);
		ImageView view = (ImageView)findViewById(R.id.imageview);
		view.setBackgroundResource(backID);
//		setContentView(view,new ViewGroup.LayoutParams(
//				ViewGroup.LayoutParams.FILL_PARENT,
//				ViewGroup.LayoutParams.FILL_PARENT));
		view.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		finish();
	}
	
	
}
