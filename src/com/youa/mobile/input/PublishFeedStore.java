package com.youa.mobile.input;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.youa.mobile.friend.store.FriendFileStore;
import com.youa.mobile.input.data.PublishData;

public class PublishFeedStore {
	final private String store_publish = "draft_publish";
	private static PublishFeedStore fileStore;

	public static PublishFeedStore getInstance() {
		if (fileStore == null) {
			fileStore = new PublishFeedStore();
		}
		return fileStore;
	}

	public void savePublish(List<PublishData> dataList, Context context) {
		if (dataList == null || dataList.size() == 0 || context == null) {
			return;
		}
		// ------------------------------------------------------
		ObjectOutputStream objectOutStream = null;
		FileOutputStream outPutStream = null;
		List<PublishData> list = new ArrayList<PublishData>();
		list.addAll(dataList);
		try {
			outPutStream = context.openFileOutput(store_publish,
					context.MODE_PRIVATE);
			objectOutStream = new ObjectOutputStream(outPutStream);
			objectOutStream.writeObject(list);
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

	public void delPublish(PublishData data, Context context) {
		if (data == null || context == null) {
			return;
		}
		List<PublishData> saveList = loadPublish(context);
		if (saveList == null) {
			return;
		}
		saveList.remove(data);
		// ------------------------------------------------------
		ObjectOutputStream objectOutStream = null;
		FileOutputStream outPutStream = null;
		try {
			outPutStream = context.openFileOutput(store_publish,
					context.MODE_PRIVATE);
			objectOutStream = new ObjectOutputStream(outPutStream);
			objectOutStream.writeObject(saveList);
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

	public List<PublishData> loadPublish(Context context) {
		if (context == null) {
			return null;
		}
		ObjectInputStream objectInputStream = null;
		List<PublishData> data = Collections
				.synchronizedList(new ArrayList<PublishData>());
		try {
			FileInputStream fileInputStream = context
					.openFileInput(store_publish);
			if (fileInputStream == null) {
				return null;
			}
			objectInputStream = new ObjectInputStream(fileInputStream);
			if (objectInputStream.readObject() != null) {
				data = (ArrayList<PublishData>) objectInputStream.readObject();
			}
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

	public boolean clearData(Context context) {
		return context.deleteFile(store_publish);
	}

}
