package com.youa.mobile.information.data;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.input.data.PublishData;

public class DraftAdapter extends BaseAdapter {

	private class DraftHolder {
		private ImageView draftPic;
		private TextView draftPicNum;
		private TextView publishState;
		private ProgressBar mProgressBar;
		private ImageView delButton;
		private ImageView tryButton;
	}

	private Context context;
	private List<PublishData> list;
	private LayoutInflater mInflater;

	public DraftAdapter(Context c, List<PublishData> list) {
		super();
		this.context = c;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int index) {
		return null;

	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		DraftHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.input_draft_layout, null);
			holder = new DraftHolder();
			holder.draftPic = (ImageView) convertView
					.findViewById(R.id.draft_pic);
			holder.draftPicNum = (TextView) convertView
					.findViewById(R.id.draft_pic_num);
			holder.publishState = (TextView) convertView
					.findViewById(R.id.publish_state);
			holder.mProgressBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar_send);
			holder.delButton = (ImageView) convertView
					.findViewById(R.id.draft_del);
			holder.tryButton = (ImageView) convertView
					.findViewById(R.id.draft_try);
			convertView.setTag(holder);
		} else {
			holder = (DraftHolder) convertView.getTag();

		}
		if (list.size() == 0 || index >= list.size()) {
			return null;
		}
		final PublishData publishData = list.get(index);
		holder.draftPicNum.setVisibility(View.GONE);
		if (publishData != null && publishData.getContentImage().size() > 0) {
			if (!TextUtils
					.isEmpty(publishData.getContentImage().get(0).imagePath)) {
				holder.draftPic.setImageURI(Uri.parse(publishData
						.getContentImage().get(0).imagePath));
			}
			int size = publishData.getContentImage().size();
			
			if (size > 1) {
				holder.draftPicNum.setText(String.valueOf(size));
				holder.draftPicNum.setVisibility(View.VISIBLE);
			}
		}
		holder.delButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ApplicationManager.getInstance().publishDataList
						.remove(publishData);
				context.sendBroadcast(new Intent(
						LehuoIntent.ACTION_FEED_PUBLISH_REFRESH));
			}
		});
		holder.tryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				publishData.setPublishState(true);
				ApplicationManager.getInstance().send(publishData);
			}
		});
		if (publishData.getPublishState()) {
			holder.mProgressBar.setVisibility(View.VISIBLE);
			holder.delButton.setVisibility(View.GONE);
			holder.tryButton.setVisibility(View.GONE);
			holder.publishState.setText(R.string.publish_sending_lable);
		} else {
			holder.publishState.setText(R.string.publish_send_fail);
			holder.mProgressBar.setVisibility(View.GONE);
			holder.delButton.setVisibility(View.VISIBLE);
			holder.tryButton.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
}
