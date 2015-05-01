package com.youa.mobile.more.action;
import java.io.File;
import java.util.Map;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.manager.SavePathManager;

import com.youa.mobile.more.action.MoreDeleteCacheAction.IMoreDeleteCacheResultListener;

public class MoreDeleteCacheAction extends BaseAction<IMoreDeleteCacheResultListener> {

	public interface IMoreDeleteCacheResultListener extends IResultListener,IFailListener {
		public void onStart();
		public void onFinish();
		public void onFail(int resourceID);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			IMoreDeleteCacheResultListener callback) throws Exception {
		callback.onStart();
		String path = SavePathManager.getImagePath();
		try {
			delFolder(path);
			delUpgradeApk();
		} catch (Exception e) {
			e.printStackTrace();
			callback.onFail(R.string.clean_cache_fail);
		}
		callback.onFinish();
	}

	private void delUpgradeApk() throws Exception {
		String sdFile =SavePathManager.getSDPath();
		String[] tempList = new File(sdFile).list();
		if (tempList == null || tempList.length == 0) {
			return;
		}
		File temp = null;
		String fileName;
		for (int i = 0; i < tempList.length; i++) {
			temp = new File(sdFile, tempList[i]);
			if (temp.isFile()) {
				fileName = temp.getName();
				if (!TextUtils.isEmpty(fileName)
						&& (fileName.startsWith("乐活") || fileName
								.startsWith("leho"))
						&& fileName.endsWith(".apk")) {
					temp.delete();
				}
			}
		}
	}
	private boolean delAllFile(String path) throws Exception{
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	private void delFolder(String folderPath) throws Exception{
		delAllFile(folderPath); //删除完里面所有内容
		String filePath = folderPath;
		filePath = filePath.toString();
		java.io.File myFilePath = new java.io.File(filePath);
		myFilePath.delete(); //删除空文件夹
	}

}
