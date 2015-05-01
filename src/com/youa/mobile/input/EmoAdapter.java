package com.youa.mobile.input;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.youa.mobile.R;
import com.youa.mobile.common.util.EmotionHelper;

public class EmoAdapter extends BaseAdapter {
	LayoutInflater inflater;
	private static final int layoutR = R.layout.input_emo_item;
	public EmoAdapter(Context appCtx) {
		super();
		inflater = LayoutInflater.from(appCtx);
	}
	@Override
	public int getCount() {
		return EmotionHelper.getEmoCount();
	}

	@Override
	public Object getItem(int arg0) {
		return EmotionHelper.getEmoChar(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = arg1;
		if(v==null)
		{
			v = inflater.inflate(layoutR, null);
		}
		((ImageView)v.findViewById(R.id.emotion)).setBackgroundResource(EmotionHelper.getEmoImg(arg0));
		return v;
	}

}
