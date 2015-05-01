//package com.youa.mobile.news.provider;
//
//import java.util.ArrayList;
//import java.util.List;
//import android.content.ContentValues;
//import android.database.Cursor;
//import com.youa.mobile.common.base.BaseProvider;
//import com.youa.mobile.common.db.SayMeTable;
//import com.youa.mobile.news.data.SayMeData;
//import com.youa.mobile.news.exception.DatabaseOperateException;
//
//public class SayMeProvider extends BaseProvider {
//
//	public long insertSayMeData(SayMeData data) throws DatabaseOperateException {
//		long rowID = -1;
//		ContentValues cr = new ContentValues();
//		cr.put(SayMeTable.COLUMN_ARTICLE_ID, data.getArticleId());
//		cr.put(SayMeTable.COLUMN_USER_ID, data.getUserId());
//		cr.put(SayMeTable.COLUMN_USER_NAME, data.getUserName());
//		cr.put(SayMeTable.COLUMN_USER_IMAGE, data.getUserImage());
//		cr.put(SayMeTable.COLUMN_PUBLISH_TIME, data.getPublishTime());
//		cr.put(SayMeTable.COLUMN_ARTICLE_TITLE, data.getArticleTitle());
//		cr.put(SayMeTable.COLUMN_ARTICLE_CONTENT, data.getArticleContent());
//		cr.put(SayMeTable.COLUMN_ARTICLE_CONTENT_IMAGE, data.getArticleContentImage());
//		cr.put(SayMeTable.COLUMN_ARTICLE_REPLY_NAME, data.getArticleReplyName());
//		cr.put(SayMeTable.COLUMN_ARTICLE_REPLY, data.getArticleReply());
//		cr.put(SayMeTable.COLUMN_ARTICLE_REPLY_IMAGE, data.getArticleReplyImage());
//		try {
//			rowID = getWritableDatabase().insert(SayMeTable.TABLE_NAME, null, cr);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return rowID;
//	}
//
//	public int insert(List<SayMeData> dataList) throws DatabaseOperateException {
//		int insertCount = 0;
//		for (SayMeData data : dataList) {
//			long rowID = insertSayMeData(data);
//			if (rowID > 0) {
//				insertCount++;
//			}
//		}
//		return insertCount;
//	}
//
//	public int delete(String whereClause, String [] whereArgs) throws DatabaseOperateException {
//		int deleteCount = 0;
//		try {
//			deleteCount = getWritableDatabase().delete(SayMeTable.TABLE_NAME, whereClause, whereArgs);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return deleteCount;
//	}
//
//	public int deleteAll() throws DatabaseOperateException {
//		int deleteCount = 0;
//		try {
//			deleteCount = getWritableDatabase().delete(SayMeTable.TABLE_NAME, null, null);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return deleteCount;
//	}
//
//	public int update(SayMeData data) throws DatabaseOperateException {
//
//		ContentValues cr = new ContentValues();
//		cr.put(SayMeTable.COLUMN_ARTICLE_ID, data.getArticleId());
//		cr.put(SayMeTable.COLUMN_USER_ID, data.getUserId());
//		cr.put(SayMeTable.COLUMN_USER_NAME, data.getUserName());
//		cr.put(SayMeTable.COLUMN_USER_IMAGE, data.getUserImage());
//		cr.put(SayMeTable.COLUMN_PUBLISH_TIME, data.getPublishTime());
//		cr.put(SayMeTable.COLUMN_ARTICLE_TITLE, data.getArticleTitle());
//		cr.put(SayMeTable.COLUMN_ARTICLE_CONTENT, data.getArticleContent());
//		cr.put(SayMeTable.COLUMN_ARTICLE_CONTENT_IMAGE, data.getArticleContentImage());
//		cr.put(SayMeTable.COLUMN_ARTICLE_REPLY_NAME, data.getArticleReplyName());
//		cr.put(SayMeTable.COLUMN_ARTICLE_REPLY, data.getArticleReply());
//		cr.put(SayMeTable.COLUMN_ARTICLE_REPLY_IMAGE, data.getArticleReplyImage());
//		String whereClause = SayMeTable.COLUMN_ARTICLE_ID + " = ?";
//
//		String [] whereArgs = { String.valueOf(data.getArticleId()) };
//		int updateCount = 0;
//		try {
//			updateCount = getWritableDatabase().update(SayMeTable.TABLE_NAME, cr, whereClause, whereArgs);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return updateCount;
//	}
//	
//	public int update(List<SayMeData> dataList) throws DatabaseOperateException {
//		int updateCount = 0;
//
//		for (SayMeData data : dataList) {
//			updateCount += update(data);
//		}
//		return updateCount;
//	}
//
//	public List<SayMeData> query(String whereClause, String [] whereArgs, String orderBy) throws DatabaseOperateException {
//		List<SayMeData> dataList = new ArrayList<SayMeData>();
//
//		Cursor cursor = null;
//
//		try {
//			cursor = getReadableDatabase().query(SayMeTable.TABLE_NAME, null, whereClause, whereArgs, null, null, orderBy);
//			while (cursor.moveToNext()) {
//				SayMeData data = new SayMeData();
//				data.setArticleId(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_ARTICLE_ID)));
//				data.setUserId(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_USER_ID)));
//				data.setUserName(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_USER_NAME)));
//				data.setUserImage(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_USER_IMAGE)));
//				data.setPublishTime(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_PUBLISH_TIME)));
//				data.setArticleTitle(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_ARTICLE_TITLE)));
//				data.setArticleContent(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_ARTICLE_CONTENT)));
//				data.setArticleContentImage(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_ARTICLE_CONTENT_IMAGE)));
//				data.setArticleReplyName(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_ARTICLE_REPLY_NAME)));
//				data.setArticleReply(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_ARTICLE_REPLY)));
//				data.setArticleReplyImage(cursor.getString(cursor.getColumnIndex(SayMeTable.COLUMN_ARTICLE_REPLY_IMAGE)));
//				dataList.add(data);
//			}
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return dataList;
//	}
//}
