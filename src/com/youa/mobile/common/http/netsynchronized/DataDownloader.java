package com.youa.mobile.common.http.netsynchronized;

import java.util.HashMap;

import com.youa.mobile.R;



public class DataDownloader extends DataUDObject{
	
	protected static HashMap<String,DataDownloader> downloaderCache = new HashMap<String, DataDownloader>();
	
	public static DataDownloader getUploader(String key)
	{
		return downloaderCache.get(key);
	}
	public interface DownloaderListener
	{
		public static final int FAIL_ERROR_SERVER = R.string.common_error_download_fail_server;
		public static final int FAIL_ERROR_LOCAL = R.string.common_error_download_fail_local;
		public static final int FAIL_CANCEL = R.string.common_download_cancel;
		public void onFailed(int reason);
		public void onProgressChanged(long progress, long mFileSize);
		public void onFinished(String file);
	}
};

