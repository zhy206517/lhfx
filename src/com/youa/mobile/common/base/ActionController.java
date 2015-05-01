package com.youa.mobile.common.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.utils.LogUtil;

public class ActionController {

	private static Map<String, IAction> actionMap = new HashMap<String, IAction>();

	public static <T extends IResultListener> IAction post(Context context, Class<? extends IAction<T>> clazz,
			Map<String, Object> params, T listener) {
		return post(context, clazz, params, listener, false);
	}

	public static <T extends IResultListener> IAction post(Context context, Class<? extends IAction<T>> clazz,
			Map<String, Object> params, T listener, boolean isStartThread) {

		if (params != null) {
			LogUtil.d("ActionController", "request params :");
			Set<String> keys = params.keySet();
			for (String str : keys) {
				LogUtil.d("ActionController", str + " = " + params.get(str));
			}
		}

		String name = clazz.getName();

		IAction<T> action = actionMap.get(name);

		if (action == null) {
			try {
				action = clazz.newInstance();
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			}

			actionMap.put(name, action);
		}

		action.execute(context, params, listener, isStartThread);

		return action;
	}

}
