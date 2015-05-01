package com.youa.mobile.common.base;

import java.util.Map;

import android.content.Context;
import com.youa.mobile.common.base.IAction.IResultListener;

public interface IAction<T extends IResultListener> {
	
	 public interface IResultListener {
		 
	 }

	 public interface IFailListener  {
		 public void onFail(int resourceID);
	 }
	 
	 void execute(Context context, Map<String,Object> params, T resultListener);
	 void execute(Context context, Map<String,Object> params, T resultListener, boolean isStartThread);
	 
}
