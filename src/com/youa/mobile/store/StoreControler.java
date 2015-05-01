package com.youa.mobile.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.youa.mobile.utils.NetTools;
import com.youa.mobile.utils.Tools;

public class StoreControler {
	final private boolean isDebug = true;
	final public static int STORE_SAVE = 1, STORE_LOAD = 2, STORE_CLEAR=3,STORE_IMAGE = 4;
	private static StoreControler storeControler;
	private ArrayList<StoreRequest> storeList = new ArrayList<StoreRequest>();
	private StoreThread storeThread;

	private boolean isStop;

	public static StoreControler getInstance() {
		if (storeControler == null) {
			storeControler = new StoreControler();
		}
		return storeControler;
	}

	public void addStoreQueue(StoreRequest request) {
		if (storeThread == null) {
			storeThread = new StoreThread();
			storeThread.start();
		}
		synchronized (storeList) {
			storeList.add(request);
			storeList.notify();
		}
	}

	private class StoreThread extends Thread {
		@Override
		public void run() {
			while (!isStop) {
				StoreRequest currentRequest = null;
				synchronized (storeList) {
					if (storeList != null && storeList.size() <= 0) {
						try {
							storeList.wait();
						} catch (InterruptedException e) {
						}
						continue;
					} else if (storeList != null) {
						// list.get(0);
						currentRequest = storeList.remove(0);
					}
					if(currentRequest==null){
						continue;
					}
					switch (currentRequest.type) {
					case STORE_SAVE | STORE_IMAGE:
						saveImage(currentRequest.data, currentRequest.url);
						break;
					case STORE_LOAD | STORE_IMAGE:
						byte[] data=getImage(currentRequest.url);
						break;
					case STORE_CLEAR | STORE_IMAGE:
						clearImage(0, 0, true);
						break;
					}
				}
			}
		}
	}

	// -----------------------------------------------------------------------

	private boolean saveAccount() {
		return true;
	}

	private boolean saveImage(byte[] data, String url) {
		if (data == null || url == null || Tools.context == null) {
			return false;
		}
		if (isDebug) {
			System.out.println("<StoreControler> <saveImage> saveImage 开始了");
		}
		String urlHash = String.valueOf(url.hashCode());
		String fileName = GetFileName("/LeHuoStore/", urlHash);
		if (null == fileName) {
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(DataPackage.Picture.URL, urlHash);
		values.put(DataPackage.Picture.FILE_NAME, fileName);
		values.put(DataPackage.Picture.FILE_LENGTH, data.length);
		ContentResolver resolver = Tools.context.getContentResolver();
		synchronized (resolver) {
			Uri uri = resolver.insert(DataPackage.Picture.URI, values);
			if (uri == null) {
				return false;
			}
			if (isDebug) {
				System.out.println(data.length + "//" + uri.toString());
			}
			OutputStream os = null;
			try {
				os = resolver.openOutputStream(uri, "rwt");
				os.write(data);
				return true;
			} catch (Exception e) {
				if (isDebug) {
					System.out
							.println("<StoreControler> <saveImage> exception:"
									+ e.toString());
				}
				return false;
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						os = null;
					}
				}
				if (isDebug) {
					System.out
							.println("<StoreControler> <saveImage> saveImage 结束了");
				}
			}
		}
	}

	private byte[] getImage(String url) {
		if (Tools.context == null) {
			return null;
		}
		String[] projection = new String[] { DataPackage.Picture.URL,
				DataPackage.Picture.FILE_LENGTH, DataPackage.Picture._ID };
		Cursor cursor = Tools.context.getContentResolver().query(
				DataPackage.Picture.URI, projection,
				DataPackage.Picture.URL + " = '" + url.hashCode() + "'", null,
				"_id ASC");
		try {
			if (cursor.moveToFirst()) {
				int idColumn = cursor.getColumnIndex(DataPackage.Picture._ID);
				int id = cursor.getInt(idColumn);
				InputStream is = Tools.context.getContentResolver()
						.openInputStream(
								Uri.withAppendedPath(DataPackage.Picture.URI,
										String.valueOf(id)));
				byte[] data = NetTools.toByteArray(is);
				return data;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.toString());
			return null;
		} finally {
			cursor.close();
		}
	}

	private boolean clearImage(long limitSize, int rowNums, boolean isClearAll) {
		if (Tools.context == null) {
			return false;
		}
		if (isDebug) {
			System.out.println("<StoreControler> <clearImage> clearImage 开始了");
		}
		ContentResolver resolver = Tools.context.getContentResolver();
		synchronized (resolver) {

			Cursor cursor = resolver
					.query(DataPackage.Picture.URI, new String[] {
							DataPackage.Picture.FILE_NAME,
							DataPackage.Picture.FILE_LENGTH }, null, null, null);
			if (isDebug) {
				System.out
						.println("<StoreControler> <clearImage> cursor totalNum:"
								+ cursor.getCount());
			}
			int lengthIndex = cursor
					.getColumnIndexOrThrow(DataPackage.Picture.FILE_LENGTH);
			long totalNum = 0;
			while (cursor.moveToNext()) {
				totalNum += cursor.getLong(lengthIndex);
			}
			if (totalNum < limitSize && !isClearAll) {
				cursor.close();
				return false;
			}
			rowNums = isClearAll ? cursor.getCount() : rowNums;
			cursor.moveToPosition(-1);
			int nameIdex = cursor
					.getColumnIndexOrThrow(DataPackage.Picture.FILE_NAME);
			int count = 0;
			String url = null;
			while (cursor.moveToNext()) {
				url = cursor.getString(nameIdex);
				if(url==null){
					return false;
				}
				resolver.delete(DataPackage.Picture.URI,
						DataPackage.Picture.FILE_NAME + "=?",
						new String[] { url });
				new File(url).delete();
				count++;
				if (count >= rowNums) {
					break;
				}
			}
			if(url==null){
				return false;
			}
			cursor.close();
			int index = url.lastIndexOf('/');
			new File(url.substring(0, index)).delete();
			// resolver.delete(DataPackage.Picture.URI, null, null);
		}
		if (isDebug) {
			System.out.println("<StoreControler> <clearImage> clearImage 结束了");
		}
		return true;
	}

	private String GetFileName(String fileUri, String urlHash) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return Tools.context.getFilesDir().getAbsolutePath() + "/"
					+ urlHash;
		}
		String sdFilePath = Environment.getExternalStorageDirectory() + fileUri;
		File filePath = new File(sdFilePath);
		if (!filePath.exists()) {
			filePath.mkdir();
		}
		String fileName = sdFilePath + urlHash;
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		return fileName;
	}
}
