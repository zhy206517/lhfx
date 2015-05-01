package com.youa.mobile.common.http.netsynchronized;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.youa.mobile.DebugMode;
import com.youa.mobile.common.manager.SavePathManager;

public class FileDownloader extends DataDownloader
{ 
	public static final boolean IS_DEBUG = DebugMode.debug;	
	public static final String TAG = "FileDownloader";
	
	public static int BUFF_SIZE = 1024;
	private String mTempFileName;
	boolean started = false;
	private DownloaderListener mDownloaderListener = null;
	private boolean mIsDeleteWhenExits = false;
	private static final String TMP_SURFIX = "_temp";
	private String mTargetFileName;
	
	public FileDownloader(String url)
	{
		this(url,
				SavePathManager.changeURLToPath(url));
	}
	
	public FileDownloader(String url, String targetFileName)
	{
		this.tag = url;
		this.mTempFileName = SavePathManager.changeURLToPath(url);
		mTempFileName += TMP_SURFIX;
		this.mFileSize = 1;//初期话非0值
		mTargetFileName = targetFileName;
	}

	private void startUIThread() {
		new Thread()
		{
			public void run()
			{
				while (started)
				{
					if(mDownloaderListener != null) {
						if(IS_DEBUG) {
							Log.d(TAG, "tag:" + tag + "processed:" + processed + " mFileSize:" + mFileSize);
						}
						mDownloaderListener.onProgressChanged(processed, mFileSize);
					}
					try
					{
						Thread.sleep(500);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	public void startDownload()
	{
		if(IS_DEBUG) {
			Log.d(TAG, "startDownload:" + tag);
		}
		synchronized(downloaderCache) {
			if(downloaderCache.containsKey(tag)) {//如果发现已经有此文件下载，怎把下载线程的回调listener替换以下，此线程中止
				Log.d(TAG, "已经有此下载:" + tag);
				((FileDownloader)downloaderCache.get(tag)).mDownloaderListener = mDownloaderListener;
				return;
			} else {
				if(mIsDeleteWhenExits) {
					String fileName = SavePathManager.changeURLToPath(tag);
					File file = new File(fileName);
					if(file.exists()) {
						
						file.deleteOnExit();
					}
				} 
				downloaderCache.put(tag, this);
			}
		}
		started = true;
		HttpURLConnection urlConnection = null;
		try
		{
			startUIThread();
			URL contentUrl = new URL(tag);
			urlConnection = (HttpURLConnection) contentUrl
					.openConnection();
			mFileSize = urlConnection.getContentLength();			
			InputStream in = urlConnection.getInputStream();
			if(in==null)
			{
				started = false;
				downloaderCache.remove(tag);
				if(mDownloaderListener != null) {
					mDownloaderListener.onFailed(DownloaderListener.FAIL_ERROR_SERVER);
				}
				return;
			}
			FileOutputStream out = new FileOutputStream(new File(mTempFileName));

			byte[] buff = new byte[BUFF_SIZE];
			int readSize = 0;
			while ((readSize = in.read(buff)) != -1)
			{
				out.write(buff, 0, readSize);
				processed += readSize;
				if (canceled) // stop by force
				{			
					break;
				}
			}
			try{ in.close();  }catch(IOException e){e.printStackTrace();}
			try{ out.close(); }catch(IOException e){e.printStackTrace();}
			
			if (canceled)
			{
				new File(mTempFileName).deleteOnExit();
				synchronized(downloaderCache) {
					downloaderCache.remove(tag);
				}
				if(mDownloaderListener != null) {
					mDownloaderListener.onFailed(DownloaderListener.FAIL_CANCEL);
				}				
			} else {				
				File fileToDownload = new File(mTempFileName);
				fileToDownload.renameTo(new File(mTargetFileName));
				synchronized(downloaderCache) {
					downloaderCache.remove(tag);
					if(mDownloaderListener != null) {
						if(IS_DEBUG) {
							Log.d(TAG, "onFinished1:" + mTargetFileName);
						}
						mDownloaderListener.onFinished(mTargetFileName);
					}
				}
			}
		} catch (IOException e1)
		{
			e1.printStackTrace();
			synchronized(downloaderCache) {
				downloaderCache.remove(tag);
			}
			if(mDownloaderListener != null) {
				mDownloaderListener.onFailed(DownloaderListener.FAIL_ERROR_LOCAL);
			}
		} finally {
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
			started = false;

		}
	}
	public void startDownload(final DownloaderListener downloaderListener)
	{
		startDownload(downloaderListener, false);
	}
	
	public void startDownload(final DownloaderListener downloaderListener, boolean isDeleteWhenExits)
	{
		mDownloaderListener = downloaderListener;
		mIsDeleteWhenExits = isDeleteWhenExits;
		new Thread() {
			public void run() {
				startDownload();
			}
		}.start();
	}
}
