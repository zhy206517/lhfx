package com.youa.mobile.circum;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.circum.PopCircumListView.PopHolder;
import com.youa.mobile.circum.data.PopCircumData;
import com.youa.mobile.location.PopBaseListView;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.NetTools;

public class PopCircumListView extends
		PopBaseListView<PopHolder, List<PopCircumData>> {
	private LayoutInflater mInflater;
	private Context context;
	private int width;
	private DeleteOnTouchListener deleteOnTouchListener;
	final public static int CIRCUM_ID = 100000002;

	public class PopHolder extends BaseHolder {
		private TextView textView;
		private ImageView deleteView;
	}

	public PopCircumListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		context = listView.getContext();
		mInflater = LayoutInflater.from(context);
		width = ApplicationManager.getInstance().getWidth() * 2 / 3 - 30;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {
			width -= 60;
		} else {
			width -= 75;
		}
		deleteOnTouchListener = new DeleteOnTouchListener();
	}

	@Override
	protected View createTemplateView(int position) {
		if(position==0){
			return mInflater.inflate(R.layout.feed_pop_circum_item_header, null);
		}else if(position==(mDataList.size()-1)){
			return mInflater.inflate(R.layout.feed_pop_circum_item_footer, null);
		}else{
			return mInflater.inflate(R.layout.feed_pop_circum_item, null);
		}
		
		
	}

	@Override
	protected PopHolder getHolder(View convertView) {
		PopHolder holder = new PopHolder();
		holder.textView = (TextView) convertView.findViewById(R.id.pop_text);
		android.view.ViewGroup.LayoutParams lp = holder.textView
				.getLayoutParams();
		if (lp != null) {
			lp.width = width;
		}
		holder.deleteView = (ImageView) convertView
				.findViewById(R.id.pop_circum_delete);
		holder.deleteView.setOnTouchListener(deleteOnTouchListener);
		return holder;
	}

	@Override
	protected void setDataWithHolder(PopHolder holder, int position,
			boolean isMoving) {
		PopCircumData data = mDataList.get(position);
		if(position==0||position==(mDataList.size()-1)){
			if(position==0){
				holder.deleteView.setImageResource(R.drawable.cur_plcae_icon);
			} else if(position==(mDataList.size()-1)){
				holder.deleteView.setImageResource(R.drawable.add_content_icon);
			}
			holder.deleteView.setOnTouchListener(null);
		} else{
			holder.deleteView.setImageResource(R.drawable.feed_pop_item_delete);
		}
		holder.textView.setText(data.place_name);
		holder.deleteView.setTag(position);
		holder.deleteView.setTag(CIRCUM_ID, data.pLid);

	}

	@Override
	protected void treateStopEvent(PopHolder holder, int position) {

	}

	private class DeleteOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (NetTools.checkNet(context) == NetTools.NET_NO) {
					return false;
				}
				if (mOnListItemListener != null) {
					mOnListItemListener.onUpEvent(v);
				}
				break;
			}
			return true;
		}
	}
}
