package com.youa.mobile.common.widget;

import java.util.List;

import android.widget.BaseAdapter;

public abstract class FlingAdapter<T> extends BaseAdapter {
	private List<T> mDataList;
	public void setData(List<T> dataList) {
		mDataList = dataList;
	}
	public List<T> getDataList() {
		return mDataList;
	}
}
