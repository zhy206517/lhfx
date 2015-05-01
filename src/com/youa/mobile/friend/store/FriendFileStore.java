package com.youa.mobile.friend.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

import com.youa.mobile.friend.data.HomeData;

public class FriendFileStore {
	final private String store_friend = "LEHOFriend";
	final private String store_friend_dir = "LeHuoStore";
	private static FriendFileStore fileStore;

	public static FriendFileStore getInstance() {
		if (fileStore == null) {
			fileStore = new FriendFileStore();
		}
		return fileStore;
	}

	public void saveFriend(ArrayList<HomeData> data, Context context) {
		if (data == null || context == null) {
			return;
		}
		// ------------------------------------------------------
		ArrayList<HomeData> saveList = new ArrayList<HomeData>();
		for (int i = 0; i < data.size(); i++) {
			if (i >= 50) {
				break;
			}
			saveList.add(data.get(i));
		}
		data = saveList;
		// ------------------------------------------------------
		ObjectOutputStream objectOutStream = null;
		FileOutputStream outPutStream = null;
		try {
			outPutStream = openFileOutStream(store_friend, context);
			objectOutStream = new ObjectOutputStream(outPutStream);
			objectOutStream.writeObject(data);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (outPutStream != null) {
				try {
					outPutStream.close();
				} catch (IOException e) {
					outPutStream = null;
				}
				outPutStream = null;
			}
			if (objectOutStream != null) {
				try {
					objectOutStream.close();
				} catch (IOException e) {
					objectOutStream = null;
				}
				objectOutStream = null;
			}
		}
	}

	public List<HomeData> loadFriend(Context context) {
		if (context == null) {
			return null;
		}
		ObjectInputStream objectInputStream = null;
		ArrayList<HomeData> data = null;
		try {
			FileInputStream fileInputStream = openFileInputStream(store_friend,
					context);
			if (fileInputStream == null) {
				return null;
			}
			objectInputStream = new ObjectInputStream(fileInputStream);
			data = (ArrayList<HomeData>) objectInputStream.readObject();
		} catch (FileNotFoundException e) {
		} catch (StreamCorruptedException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
					objectInputStream = null;
				} catch (IOException e) {
					objectInputStream = null;
				}
			}
		}
		return data;
	}

	public void clearData(Context context) {
		try {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				context.deleteFile(store_friend);
				return;
			}
			File sdFile = Environment.getExternalStorageDirectory();
			if (file == null) {
				File dir = new File(sdFile.getAbsolutePath() +File.separator+ store_friend_dir);
				file = new File( dir, store_friend);
			}
			File oldFile = new File(sdFile, store_friend);
			if(oldFile!=null&&oldFile.exists()){
				oldFile.delete();
			}
			if (file.exists()) {
				file.delete();
			}
			file = null;
		} catch (Exception e) {

		}
	}

	private FileInputStream openFileInputStream(String fileName, Context context)
			throws FileNotFoundException {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return context.openFileInput(fileName);
		}
		if (file == null) {
			File sdFile = Environment.getExternalStorageDirectory();
			File dir = new File(sdFile.getAbsolutePath() +File.separator+ store_friend_dir);
			file = new File(dir, fileName);
		}
		if (!file.exists()) {
			return null;
		}
		return new FileInputStream(file);
	}

	private File file;

	private FileOutputStream openFileOutStream(String fileName, Context context)
			throws FileNotFoundException {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return context.openFileOutput(fileName, Context.MODE_PRIVATE);
		}
		if (file == null) {
			File sdFile = Environment.getExternalStorageDirectory();
			File dir = new File(sdFile.getAbsolutePath() +File.separator+ store_friend_dir);
			file = new File(dir, fileName);
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		return new FileOutputStream(file);
	}
	// <!-- 在SDCard中创建与删除文件权限 -->
	//
	// <uses-permission
	// android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
	//
	// <!-- 往SDCard写入数据权限 -->
	//
	// <uses-permission
	// android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

}