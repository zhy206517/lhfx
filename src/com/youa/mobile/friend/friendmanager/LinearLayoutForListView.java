package com.youa.mobile.friend.friendmanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class LinearLayoutForListView extends LinearLayout {

    private AdapterForLinearLayout adapter;
    private OnClickListener onClickListener = null;

    public void bindLinearLayout() {
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);

            v.setOnClickListener(this.onClickListener);
            if (i == count - 1) {
            	RelativeLayout ly = (RelativeLayout) v;
                ly.removeViewAt(3);
            }
            addView(v, i);
        }
    }

    public LinearLayoutForListView(Context context) {
        super(context);

    }

    public LinearLayoutForListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public AdapterForLinearLayout getAdpater() {
        return adapter;
    }

    public void setAdapter(AdapterForLinearLayout adpater) {
        this.adapter = adpater;
        bindLinearLayout();
    }

    public OnClickListener getOnclickListner() {
        return onClickListener;
    }

    public void setOnclickLinstener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}



