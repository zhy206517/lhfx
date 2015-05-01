package com.youa.mobile.theme;

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
import com.youa.mobile.theme.PopThemeListView.PopHolder;
import com.youa.mobile.theme.data.PopThemeInfo;
import com.youa.mobile.location.PopBaseListView;
import com.youa.mobile.ui.base.BaseHolder;
import com.youa.mobile.utils.NetTools;

public class PopThemeListView extends
		PopBaseListView<PopHolder, List<PopThemeInfo>> {
	private LayoutInflater mInflater;
	private Context context;
	private DeleteOnTouchListener deleteOnTouchListener;
	private int width;
	final public static int THEME_ID = 100000001;

	public class PopHolder extends BaseHolder {
		private TextView textView;
		private ImageView deleteView;
	}

	public PopThemeListView(ListView listView, View header, View footer) {
		super(listView, header, footer);
		context = listView.getContext();
		mInflater = LayoutInflater.from(context);
		width=ApplicationManager.getInstance().getWidth() * 2 / 3;
		if(ApplicationManager.getInstance().getDensityDpi()<240){
			width -= 60;
		}else{
			width -= 75;
		}
		// width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		// width, resource.getDisplayMetrics());
		deleteOnTouchListener = new DeleteOnTouchListener();
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
				.findViewById(R.id.pop_theme_delete);
		
		return holder;
	}

	@Override
	protected void setDataWithHolder(PopHolder holder, int position,
			boolean isMoving) {
		PopThemeInfo data = mDataList.get(position);
		holder.textView.setText(data.name);
		if((mDataList.size()-1)==position){
			holder.deleteView.setImageResource(R.drawable.add_content_icon);
			holder.deleteView.setOnTouchListener(null);
		}else{
			holder.deleteView.setImageResource(R.drawable.feed_pop_item_delete);
			holder.deleteView.setOnTouchListener(deleteOnTouchListener);
		}
		holder.deleteView.setTag(position);
		holder.deleteView.setTag(THEME_ID, data.sUid);
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

	@Override
	protected View createTemplateView(int position) {
		if(position==(mDataList.size()-1)){
			return mInflater.inflate(R.layout.feed_pop_theme_item_footer, null);
		}else{			
			return mInflater.inflate(R.layout.feed_pop_theme_item, null);
		}
	}

}
