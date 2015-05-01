package com.youa.mobile.common.http.netsynchronized;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.youa.mobile.R;

public abstract class DataUploader extends DataUDObject {
	
	public interface UploadListener
	{
		public static final int FAIL_ERROR = R.string.common_upload_fail;
		public static final int FAIL_CANCEL = R.string.common_upload_cancel;
		public void onFailed(int reason);
	}
	
	public interface FileUploadListener extends UploadListener
	{
		public boolean onUploadStart();
		public void onProgressChanged(long progress, long mFileSize);
		public void onFinished(String imageid);
	}
	
	private final String TAG = "DataUploader";
	private boolean IS_DEBUG = false;
	protected static HashMap<String,DataUploader> uploaderCache = new HashMap<String, DataUploader>();
	private boolean started = false;
	private String uploadresult;

	public DataUploader() {
	}
	public static DataUploader getUploader(String key)
	{
		return uploaderCache.get(key);
	}
	
	protected abstract void onUpload(
			FileUploadListener uploadListener,
			Context appCtx);
	
	
	private void startUpdateUpLoad(
			final FileUploadListener uploadListener,
			final Context appCtx) {
		if(IS_DEBUG) {
			Log.d(TAG, "start update thread");
		}
		boolean isStartAble = uploadListener.onUploadStart();
		if(isStartAble) {
			onUpload(uploadListener, appCtx);
		} else {
			uploadListener.onFailed(R.string.common_upload_cancel_byuser);
		}
		started = false;
	}
	
	public String startUpLoad(Context appCtx) {
		startUpLoad(new FileUploadListener(){
			@Override
			public void onFinished(String imageid) {
				uploadresult = imageid;
			}
			@Override
			public void onProgressChanged(long progress, long fileSize) {				
			}
			@Override
			public boolean onUploadStart() {
				return true;
			}

			@Override
			public void onFailed(int reason) {
				throw new UpdateLoadException(reason);
			}
			
		},
		appCtx, 
		false);
		return uploadresult;
	}
	
	public void startUpLoad(
			final FileUploadListener uploadListener,
			final Context appCtx,
			boolean isCreateThread) {
		if(mFileSize<0)
		{
			uploadListener.onFailed(R.string.common_filezize_zero);
			return;
		}
		started = true;
		uploaderCache.put(tag, this);

		if(isCreateThread) {
			new Thread()
			{//更新上传线程
				public void run() {
					startUpdateUpLoad(uploadListener, appCtx);
				}
			}.start();
			new Thread() {//更新进度线程
				public void run() {
					if(IS_DEBUG) {
						Log.d(TAG, "start ui thread");
					}
					while(started && !canceled && processed < mFileSize){
						if(IS_DEBUG) {
							Log.i(TAG, "update progress " + processed + "/" + mFileSize);
						}
						uploadListener.onProgressChanged(processed, mFileSize);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		} else {
			startUpdateUpLoad(uploadListener, appCtx);
			return ;
		}
	}
	
	class UploadInputStream extends FileInputStream {
		public UploadInputStream(File file) throws FileNotFoundException {
			super(file);
		}
		
		@Override
		public int read() throws IOException {
			int c = super.read();
			if (c != -1) 
				DataUploader.this.processed ++;
			return c;
		}
		
		@Override
		public int read(byte[] buffer) throws IOException {
			return super.read(buffer);
		}
		
		@Override
		public int read(byte[] buffer, int offset, int count)
				throws IOException {
			int n = super.read(buffer, offset, count);
			if (n != -1)
				DataUploader.this.processed += n;
			return n;
		}
	}


	
}
