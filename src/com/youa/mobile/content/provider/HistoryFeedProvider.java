package com.youa.mobile.content.provider;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.youa.mobile.common.base.BaseProvider;
import com.youa.mobile.common.db.ContentTable;
import com.youa.mobile.common.db.ContentTable.ContentColumns;
import com.youa.mobile.common.db.HistoryTable;
import com.youa.mobile.common.db.HistoryTable.HistoryColumns;
import com.youa.mobile.common.db.ImageTable;
import com.youa.mobile.common.db.ImageTable.ImageColumns;
import com.youa.mobile.common.params.PageSize;
import com.youa.mobile.friend.data.ContentImg;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.parser.ContentData;
import com.youa.mobile.utils.LogUtil;

public class HistoryFeedProvider extends BaseProvider {

	private final static int MAX_RECORD_NUM = 1000;

	private SQLiteDatabase mdb = null;

	public void insertHistory(HomeData datas) {
		if (datas == null || (datas.PublicUser == null && datas.originUser == null && datas.transPondUser == null)) {
			return;
		}

		// deleteStaleHistoryByTime();
		boolean isToUpdate = false;

		if (datas.originUser != null) {
			LogUtil.d(TAG, "insertHistory. save orign user.");
			if (!TextUtils.isEmpty(datas.originUser.postId)) {
				isToUpdate = deleteStaleHistoryByCount(datas.originUser.postId);
			} else {
				return;
			}
			saveUserValues(datas.originUser, null, 1, isToUpdate);
		} else if (datas.PublicUser != null) {
			LogUtil.d(TAG, "insertHistory. save public user.");
			if (!TextUtils.isEmpty(datas.PublicUser.postId)) {
				isToUpdate = deleteStaleHistoryByCount(datas.PublicUser.postId);
			} else {
				return;
			}
			saveUserValues(datas.PublicUser, datas.originUser == null ? null : datas.originUser.postId, 0, isToUpdate);
		}
		// if (datas.transPondUser != null) {
		// LogUtil.d(TAG, "insertHistory. save transpond user.");
		// saveUserValues(datas.transPondUser, null, 2);
		// }
	}

	/**
	 * @param currPage
	 *            current page index, starts with 0, pass a int values <0 to get all records.
	 * @return
	 */
	public ArrayList<HomeData> getHistory(int currPage) {
		ArrayList<HomeData> list = new ArrayList<HomeData>();
		Cursor cursor = null;
		int end = 0;

		openDb();
		try {
			cursor = mdb.query(HistoryTable.TABLE_NAME, null, HistoryColumns.USER_TYPE + "=?", new String[] { "0" }, null, null,
					HistoryColumns.LOCAL_ID + " desc");
			if (cursor == null || cursor.getCount() <= 0) {
				return list;
			}
			end = cursor.getCount();
			cursor.moveToFirst();
			if (currPage >= 0) {
				if (currPage * PageSize.INFO_FEED_PAGESIZE + 1 <= cursor.getCount()) {
					cursor.moveToPosition(PageSize.HISTORY_FEED_PAGESIZE * currPage);
					end = PageSize.HISTORY_FEED_PAGESIZE * (currPage + 1);
				} else {
					return list;
				}
			}
			LogUtil.d(TAG, "getHistory. history count = " + cursor.getCount());
			// int userType = 0;
			do {
				HomeData data = new HomeData();
				User user = new User();
				setUserData(cursor, user);
				addContents(user);
				addImages(user);
				data.PublicUser = user;

				String originPostId = cursor.getString(cursor.getColumnIndex(HistoryColumns.ORIGIN_POST_ID));
				LogUtil.d(TAG, "getHistory. originPostId = " + originPostId);
				if (!TextUtils.isEmpty(originPostId)) {
					Cursor cursor2 = mdb.query(HistoryTable.TABLE_NAME, null, HistoryColumns.POST_ID + "=?", new String[] { originPostId }, null,
							null, null);
					if (cursor2 != null && cursor2.moveToFirst()) {
						User user2 = new User();
						setUserData(cursor2, user2);
						addContents(user2);
						addImages(user2);
						data.originUser = user2;
					}
					if (cursor2 != null && !cursor2.isClosed()) {
						cursor2.close();
						cursor2 = null;
					}
				}
				list.add(data);
			} while (cursor.moveToNext() && cursor.getPosition() < end);
		} catch (SQLiteException e) {
			LogUtil.e(TAG, "query history records. ", e);
		} finally {
			closeDb(cursor);
		}
		return list;
	}

	public ArrayList<HomeData> getHistory() {
		ArrayList<HomeData> list = new ArrayList<HomeData>();
		Cursor cursor = null;
		openDb();
		try {
			cursor = mdb.query(HistoryTable.TABLE_NAME, null, null, null, null, null, HistoryColumns.LOCAL_ID + " desc");
			if (cursor == null || cursor.getCount() <= 0) {
				return list;
			}
			LogUtil.d(TAG, "getHistory. history count = " + cursor.getCount());
			// int userType = 0;
			cursor.moveToFirst();
			do {
				HomeData data = new HomeData();
				User user = new User();
				setUserData(cursor, user);
				addContents(user);
				addImages(user);
				data.PublicUser = user;
				list.add(data);
			} while (cursor.moveToNext());
		} catch (SQLiteException e) {
			LogUtil.e(TAG, "query history records. ", e);
		} finally {
			closeDb(cursor);
		}
		return list;
	}

	/**
	 * true - to update the record<br>
	 * false - to insert the record
	 * 
	 * @return
	 */
	private boolean deleteStaleHistoryByCount(String postId) {
		Cursor cursor = null;
		try {
			openDb();
			cursor = mdb.query(HistoryTable.TABLE_NAME, new String[] { HistoryColumns.POST_ID }, null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					if (postId.equals(cursor.getString(0))) {
						return true;
					}
				} while (cursor.moveToNext());
				// if (cursor.getInt(0) > MAX_RECORD_NUM) {
				// String postId = cursor.getString(1);
				// mdb.delete(HistoryTable.TABLE_NAME, HistoryColumns.POST_ID + "in(?)", new String[] { postId });
				// mdb.delete(ContentTable.TABLE_NAME, HistoryColumns.POST_ID + "in(?)", new String[] { postId });
				// mdb.delete(ImageTable.TABLE_NAME, HistoryColumns.POST_ID + "in(?)", new String[] { postId });
				// LogUtil.d(TAG, "deleteStaleHistoryByCount. delete one stale data.");
				// }
				if (cursor.getCount() > MAX_RECORD_NUM) {
					cursor.moveToLast();
					String id = cursor.getString(0);
					mdb.delete(HistoryTable.TABLE_NAME, HistoryColumns.POST_ID + " in(?)", new String[] { id });
					mdb.delete(ContentTable.TABLE_NAME, HistoryColumns.POST_ID + " in(?)", new String[] { id });
					mdb.delete(ImageTable.TABLE_NAME, HistoryColumns.POST_ID + " in(?)", new String[] { id });
					LogUtil.d(TAG, "deleteStaleHistoryByCount. delete one stale data.");
				}
			}
		} catch (SQLiteException e) {
			LogUtil.e(TAG, "deleteStaleHistoryByCount", e);
		} finally {
			closeDb(cursor);
		}
		return false;
	}

	public void clearHistory() {
		openDb();
		mdb.delete(HistoryTable.TABLE_NAME, null, null);
		mdb.delete(ContentTable.TABLE_NAME, null, null);
		mdb.delete(ImageTable.TABLE_NAME, null, null);
		closeDb();
	}

	private void setUserData(Cursor cursor, User user) {
		user.postId = cursor.getString(cursor.getColumnIndex(HistoryColumns.POST_ID));
		user.uId = cursor.getString(cursor.getColumnIndex(HistoryColumns.U_ID));
		user.sex = cursor.getInt(cursor.getColumnIndex(HistoryColumns.SEX));
		user.type = cursor.getInt(cursor.getColumnIndex(HistoryColumns.TYPE));
		user.feedType = cursor.getString(cursor.getColumnIndex(HistoryColumns.FEED_TYPE));
		user.img_head_id = cursor.getString(cursor.getColumnIndex(HistoryColumns.IMG_HEAD_ID));
		user.name = cursor.getString(cursor.getColumnIndex(HistoryColumns.NAME));
		user.time = cursor.getString(cursor.getColumnIndex(HistoryColumns.TIME));
		user.timeLine = cursor.getString(cursor.getColumnIndex(HistoryColumns.TIMELINE));
		user.place = cursor.getString(cursor.getColumnIndex(HistoryColumns.PLACE));
		user.price = cursor.getString(cursor.getColumnIndex(HistoryColumns.PRICE));
		user.fromWhere = cursor.getString(cursor.getColumnIndex(HistoryColumns.FROMWHERE));
		user.like_num = cursor.getString(cursor.getColumnIndex(HistoryColumns.LIKE_NUM));
		user.comment_num = cursor.getString(cursor.getColumnIndex(HistoryColumns.COMMENT_NUM));
		user.transpond_num = cursor.getString(cursor.getColumnIndex(HistoryColumns.TRANSPOND_NUM));
		user.nameCharSequence = cursor.getString(cursor.getColumnIndex(HistoryColumns.NAMECHARSEQUENCE));
		user.charSequence = cursor.getString(cursor.getColumnIndex(HistoryColumns.CHARSEQUENCE));
	}

	private void addImages(User user) {
		Cursor c2 = mdb.query(ImageTable.TABLE_NAME, new String[] { "*" }, ImageColumns.POST_ID + "=?", new String[] { user.postId }, null, null,
				null);
		if (c2 != null && c2.getCount() > 0) {
			ContentImg[] datas = new ContentImg[c2.getCount()];
			for (int i = 0; i < datas.length; i++) {
				datas[i] = new ContentImg();
			}
			int index = 0;
			c2.moveToFirst();
			do {
				datas[index].img_content_id = c2.getString(c2.getColumnIndex(ImageColumns.IMG_CONTENT_ID));
				datas[index].img_desc = c2.getString(c2.getColumnIndex(ImageColumns.IMG_DESC));
				datas[index].height = c2.getInt(c2.getColumnIndex(ImageColumns.HEIGHT));
				datas[index].width = c2.getInt(c2.getColumnIndex(ImageColumns.WIDTH));
				index++;
			} while (c2.moveToNext());
			user.contentImg = datas;
		}
		if (c2 != null && !c2.isClosed()) {
			c2.close();
			c2 = null;
		}
	}

	private void addContents(User user) {
		Cursor c = mdb.query(ContentTable.TABLE_NAME, new String[] { "*" }, ContentColumns.POST_ID + "=?", new String[] { user.postId }, null, null,
				null);
		if (c != null && c.getCount() > 0) {
			ContentData[] datas = new ContentData[c.getCount()];
			for (int i = 0; i < datas.length; i++) {
				datas[i] = new ContentData();
			}
			int index = 0;
			c.moveToFirst();
			int i_href = c.getColumnIndex(ContentColumns.HREF);
			int i_str = c.getColumnIndex(ContentColumns.STR);
			int i_type = c.getColumnIndex(ContentColumns.TYPE);
			LogUtil.d(TAG, "getHistory. href index = " + i_href + ", str index = " + i_str + ", type index = " + i_type);
			do {
				LogUtil.d(TAG, "getHistory. row " + index + " : ");
				datas[index].href = c.getString(i_href);
				datas[index].str = c.getString(i_str);
				datas[index].type = c.getInt(i_type);
				LogUtil.d(TAG, "href = " + datas[index].href + ", str = " + datas[index].str + ", type = " + datas[index].type);
				index++;
			} while (c.moveToNext());
			user.contents = datas;
		}
		if (c != null && !c.isClosed()) {
			c.close();
			c = null;
		}
	}

	private void saveUserValues(User data, String orignPostId, int userType, boolean isToUpdate) {
		LogUtil.d(TAG, "saveUserValues. orignPostId = " + orignPostId + ", userType = " + userType + ", isToUpdate = " + isToUpdate);
		ContentValues values = new ContentValues();
		values.put(HistoryColumns.CREATE_TIME, System.currentTimeMillis());
		values.put(HistoryColumns.POST_ID, data.postId);
		values.put(HistoryColumns.U_ID, data.uId);
		values.put(HistoryColumns.SEX, data.sex);
		values.put(HistoryColumns.TYPE, data.type);
		values.put(HistoryColumns.FEED_TYPE, data.feedType);
		values.put(HistoryColumns.IMG_HEAD_ID, data.img_head_id);
		values.put(HistoryColumns.NAME, data.name);
		values.put(HistoryColumns.TIME, data.time);
		values.put(HistoryColumns.TIMELINE, data.timeLine);
		values.put(HistoryColumns.PLACE, data.place);
		values.put(HistoryColumns.PRICE, data.price);
		values.put(HistoryColumns.FROMWHERE, data.fromWhere);
		values.put(HistoryColumns.LIKE_NUM, data.like_num);
		values.put(HistoryColumns.COMMENT_NUM, data.comment_num);
		values.put(HistoryColumns.TRANSPOND_NUM, data.transpond_num);
		if (!TextUtils.isEmpty(data.nameCharSequence)) {
			values.put(HistoryColumns.NAMECHARSEQUENCE, data.nameCharSequence.toString());
		}
		if (!TextUtils.isEmpty(data.charSequence)) {
			values.put(HistoryColumns.CHARSEQUENCE, data.charSequence.toString());
		}
		if (!TextUtils.isEmpty(orignPostId)) {
			values.put(HistoryColumns.ORIGIN_POST_ID, orignPostId);
		}
		values.put(HistoryColumns.USER_TYPE, userType);
		try {
			openDb();
			if (isToUpdate) {
				mdb.update(HistoryTable.TABLE_NAME, values, HistoryColumns.POST_ID + "=?", new String[] { data.postId });
			} else {
				mdb.insert(HistoryTable.TABLE_NAME, null, values);
				if (data.contents != null && data.contents.length > 0) {
					for (ContentData cData : data.contents) {
						values.clear();
						values.put(ContentColumns.POST_ID, data.postId);
						values.put(ContentColumns.HREF, cData.href);
						values.put(ContentColumns.STR, cData.str);
						values.put(ContentColumns.TYPE, cData.type);
						mdb.insert(ContentTable.TABLE_NAME, null, values);
					}
				}
				if (data.contentImg != null && data.contentImg.length > 0) {
					for (ContentImg cData : data.contentImg) {
						values.clear();
						values.put(ImageColumns.POST_ID, data.postId);
						values.put(ImageColumns.IMG_CONTENT_ID, cData.img_content_id);
						values.put(ImageColumns.IMG_DESC, cData.img_desc);
						values.put(ImageColumns.HEIGHT, cData.height);
						values.put(ImageColumns.WIDTH, cData.width);
						if (isToUpdate) {
							mdb.update(ImageTable.TABLE_NAME, values, ImageColumns.POST_ID + "=?", new String[] { data.postId });
						} else {
							mdb.insert(ImageTable.TABLE_NAME, null, values);
						}
					}
				}
			}
		} catch (SQLiteException e) {
			LogUtil.e(TAG, "insert history record. ", e);
		} finally {
			closeDb();
		}
	}

	private void openDb() {
		mdb = getWritableDatabase();
	}

	private void closeDb() {
		if (mdb != null) {
			mdb = null;
		}
	}

	private void closeDb(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
		closeDb();
	}
}

// private void deleteStaleHistoryByTime() {
// long deltaTime = System.currentTimeMillis() - 1000 * 3600 * 24 * 7;
// Cursor cursor = null;
// try {
// openDb();
// cursor = mdb.query(HistoryTable.TABLE_NAME, new String[] { HistoryColumns.POST_ID }, HistoryColumns.CREATE_TIME + "<" + deltaTime, null,
// null, null, null);
// if (cursor != null && cursor.getCount() > 0) {
// StringBuffer sBuffer = new StringBuffer();
// cursor.moveToFirst();
// do {
// sBuffer.append(cursor.getString(0));
// sBuffer.append(",");
// } while (cursor.moveToNext());
// if (sBuffer.length() > 0) {
// String ids = sBuffer.substring(0, sBuffer.length() - 1);
// mdb.delete(HistoryTable.TABLE_NAME, HistoryColumns.POST_ID + "in(?)", new String[] { ids });
// mdb.delete(ContentTable.TABLE_NAME, ContentColumns.POST_ID + "in(?)", new String[] { ids });
// mdb.delete(ImageTable.TABLE_NAME, ImageColumns.POST_ID + "in(?)", new String[] { ids });
// }
// }
// } catch (SQLiteException e) {
// LogUtil.e(TAG, "deleteStaleHistory", e);
// } finally {
// closeDb(cursor);
// }
// }