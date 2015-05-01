package com.youa.mobile.common.base;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.youa.mobile.DebugMode;
import com.youa.mobile.R;
import com.youa.mobile.SystemConfig;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.db.ConnectManager;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.netsynchronized.UpdateLoadException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.friend.FriendFeedActivity;
import com.youa.mobile.friend.store.FriendFileStore;
import com.youa.mobile.login.LoginPage;

public abstract class BaseAction<T extends IResultListener> implements IAction<IResultListener> {
	public static final String TAG = "BaseAction";
	
	public static int THREAD_COUNT = 10;
	
	private static ThreadPoolExecutor mThreadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_COUNT);
	
	static {
		mThreadPoolExecutor.setThreadFactory(new ThreadFactory(){
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}
		});		
	}
	
	@Override
	public void execute(Context context, Map<String, Object> params, IResultListener resultListener) {
		executePri(context, params, resultListener, false);
	}
	
	@Override
	public void execute(final Context context, final Map<String, Object> params, final IResultListener resultListener, boolean isStartThread) {
		if (isStartThread) {
			mThreadPoolExecutor.execute(new Runnable() {
				@Override
				public void run() {
					executePri(context, params, resultListener, true);
				}				
			});
		} else {
			executePri(context, params, resultListener, false);
		}		
	}

	@SuppressWarnings("unchecked")
	public void executePri(final Context context, Map<String, Object> params, IResultListener resultListener, boolean isInThread) {
		try {
			if(DebugMode.debug) {
				Log.d(TAG, "onExecute begin:" + this.getClass().getName());
			}
			ConnectManager.initDBConnector(context);
			if(DebugMode.debug) {
				if(params != null) {
					Log.d(TAG, "onExecute-params:" + params.toString());
				}
			}
			onExecute(context, params, (T)resultListener);
			
			ConnectManager.closeDB();
			if(DebugMode.debug) {
				Log.d(TAG, "onExecute end");
			}
		} catch(Exception e) {
			if(e instanceof UpdateLoadException) {
				e.printStackTrace();
				((IFailListener)resultListener).onFail(R.string.common_error_unknow);
			} else if (e instanceof MessageException) {
				e.printStackTrace();
				if (resultListener != null && resultListener instanceof IFailListener) {
					int resID = ((MessageException)e).getResID();
					String errorCode = ((MessageException)e).getErrCode();
					if (resID != -1) {
						((IFailListener)resultListener).onFail(resID);
					} else if(errorCode.equals(CommonParam.VALUE_ERROR_NOT_FOUND)){
						((IFailListener)resultListener).onFail(R.string.common_error_unknow);
					} else if(errorCode.equals(CommonParam.VALUE_ERROR_PARAM_ERROR)){
						((IFailListener)resultListener).onFail(R.string.common_error_unknow);
					} else if(errorCode.equals(CommonParam.VALUE_ERROR_ANTISPAM)){
						((IFailListener)resultListener).onFail(R.string.common_error_antispam);
					} else if(errorCode.equals(CommonParam.VALUE_ERROR_NOT_POWER)){
						((IFailListener)resultListener).onFail(R.string.common_error_power);
					} else if(errorCode.equals(CommonParam.VALUE_ERROR_BFILTER)){
						((IFailListener)resultListener).onFail(R.string.common_error_bfilter);
					} else if(errorCode.equals(CommonParam.VALUE_ERROR_DENY)){
						((IFailListener)resultListener).onFail(R.string.common_error_deny);
					} else if(errorCode.equals(CommonParam.VALUE_ERROR_NETWORK_ERROR)){
						((IFailListener)resultListener).onFail(R.string.common_network_error);
					} else if(errorCode.toLowerCase().equals(CommonParam.VALUE_ERROR_OLD_VERSION)){
						((IFailListener)resultListener).onFail(R.string.common_error_oldversion);
					} else if(errorCode.toLowerCase().equals(CommonParam.VALUE_ERROR_NOT_LOGIN)) {
						context.startActivity(new Intent(context, LoginPage.class));
					} else if(errorCode.toLowerCase().equals(CommonParam.VALUE_ERROR_AUTOLOGIN_FAIL)){
						SharedPreferences sharedPreferences = context
								.getSharedPreferences(
										SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
						Editor editor = sharedPreferences.edit();
						Map keyMap = sharedPreferences.getAll();
						if (keyMap != null) {
							Set keySet = keyMap.keySet();
							for (Iterator iter = keySet.iterator(); iter
									.hasNext();) {
								String _key = (String) iter.next();
								editor.remove(_key);
							}
						}
						editor.commit();
						FriendFileStore.getInstance().clearData(context);
						if(BasePage.activityList!=null&&BasePage.activityList.size()>0){
							for (int i = 0; i < BasePage.activityList.size(); i++) {
								if (null != BasePage.activityList.get(i)) {
									BasePage.activityList.get(i).finish();
								}
							}
							BasePage.activityList.clear();
						}
//						FriendFeedActivity.saveHomeDataList = null;
						Intent i = new Intent();
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.setClass(context, LoginPage.class);
						context.startActivity(i);
					}else {
						((IFailListener)resultListener).onFail(R.string.common_error_unknow);
					}
					
					//TODO go home
//					if (resID == R.string.error_password_error_gotologin) {
//						SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConfig.XML_FILE_SYSTEM_CONFIG, 0);
//						
//						Editor editor = sharedPreferences.edit();
//						editor.clear();
//						editor.commit();
//						
//						Intent intent = new Intent(context, HomePage.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						context.startActivity(intent);
//					}
				} else {
					e.printStackTrace();
				}
			} else {
				e.printStackTrace();
				ConnectManager.reset();
				throw new RuntimeException(e);
			}
		}
	}
	
	protected abstract void onExecute(Context context, Map<String,Object> params, T resultListener) throws Exception ;
	
	protected void assertLogin() throws MessageException {
		if (!ApplicationManager.getInstance().isLogin()) {
			throw new MessageException(CommonParam.VALUE_ERROR_NOT_LOGIN);
		}
	}

}
