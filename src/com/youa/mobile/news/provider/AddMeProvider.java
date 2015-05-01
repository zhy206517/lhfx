//package com.youa.mobile.news.provider;
//
//import java.util.ArrayList;
//import java.util.List;
//import android.content.ContentValues;
//import android.database.Cursor;
//import com.youa.mobile.common.base.BaseProvider;
//import com.youa.mobile.common.db.AddMeTable;
//import com.youa.mobile.news.data.AddMeData;
//import com.youa.mobile.news.exception.DatabaseOperateException;
//
//public class AddMeProvider extends BaseProvider {
//
//	public long insertAddMeData(AddMeData data) throws DatabaseOperateException {
//		long rowID = -1;
//		ContentValues cr = new ContentValues();
//		cr.put(AddMeTable.COLUMN_ARTICLE_ID, data.getArticleId());
//		cr.put(AddMeTable.COLUMN_USER_ID, data.getUserId());
//		cr.put(AddMeTable.COLUMN_USER_NAME, data.getUserName());
//		cr.put(AddMeTable.COLUMN_USER_IMAGE, data.getUserImage());
//		cr.put(AddMeTable.COLUMN_PUBLISH_TIME, data.getPublishTime());
//		cr.put(AddMeTable.COLUMN_ARTICLE_TITLE, data.getArticleTitle());
//		cr.put(AddMeTable.COLUMN_ARTICLE_CONTENT, data.getArticleContent());
//		cr.put(AddMeTable.COLUMN_ARTICLE_CONTENT_IMAGE, data.getArticleContentImage());
//		cr.put(AddMeTable.COLUMN_ARTICLE_REPLY_NAME, data.getArticleReplyName());
//		cr.put(AddMeTable.COLUMN_ARTICLE_REPLY, data.getArticleReply());
//		cr.put(AddMeTable.COLUMN_ARTICLE_REPLY_IMAGE, data.getArticleReplyImage());
//		cr.put(AddMeTable.COLUMN_ARTICLE_LIKE_COUNT, data.getArticleLikeCount());
//		cr.put(AddMeTable.COLUMN_ARTICLE_COMMENT_COUNT, data.getArticleCommentCount());
//		cr.put(AddMeTable.COLUMN_ARTICLE_FORWARD_COUNT, data.getArticleForwardCount());
//		try {
//			rowID = getWritableDatabase().insert(AddMeTable.TABLE_NAME, null, cr);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return rowID;
//	}
//
//	public int insert(List<AddMeData> dataList) throws DatabaseOperateException {
//		int insertCount = 0;
//		for (AddMeData data : dataList) {
//			long rowID = insertAddMeData(data);
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
//			deleteCount = getWritableDatabase().delete(AddMeTable.TABLE_NAME, whereClause, whereArgs);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return deleteCount;
//	}
//
//	public int deleteAll() throws DatabaseOperateException {
//		int deleteCount = 0;
//		try {
//			deleteCount = getWritableDatabase().delete(AddMeTable.TABLE_NAME, null, null);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return deleteCount;
//	}
//
//	public int update(AddMeData data) throws DatabaseOperateException {
//
//		ContentValues cr = new ContentValues();
//		cr.put(AddMeTable.COLUMN_ARTICLE_ID, data.getArticleId());
//		cr.put(AddMeTable.COLUMN_USER_ID, data.getUserId());
//		cr.put(AddMeTable.COLUMN_USER_NAME, data.getUserName());
//		cr.put(AddMeTable.COLUMN_USER_IMAGE, data.getUserImage());
//		cr.put(AddMeTable.COLUMN_PUBLISH_TIME, data.getPublishTime());
//		cr.put(AddMeTable.COLUMN_ARTICLE_TITLE, data.getArticleTitle());
//		cr.put(AddMeTable.COLUMN_ARTICLE_CONTENT, data.getArticleContent());
//		cr.put(AddMeTable.COLUMN_ARTICLE_CONTENT_IMAGE, data.getArticleContentImage());
//		cr.put(AddMeTable.COLUMN_ARTICLE_REPLY_NAME, data.getArticleReplyName());
//		cr.put(AddMeTable.COLUMN_ARTICLE_REPLY, data.getArticleReply());
//		cr.put(AddMeTable.COLUMN_ARTICLE_REPLY_IMAGE, data.getArticleReplyImage());
//		cr.put(AddMeTable.COLUMN_ARTICLE_LIKE_COUNT, data.getArticleLikeCount());
//		cr.put(AddMeTable.COLUMN_ARTICLE_COMMENT_COUNT, data.getArticleCommentCount());
//		cr.put(AddMeTable.COLUMN_ARTICLE_FORWARD_COUNT, data.getArticleForwardCount());
//		String whereClause = AddMeTable.COLUMN_ARTICLE_ID + " = ?";
//
//		String [] whereArgs = { String.valueOf(data.getArticleId()) };
//		int updateCount = 0;
//		try {
//			updateCount = getWritableDatabase().update(AddMeTable.TABLE_NAME, cr, whereClause, whereArgs);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return updateCount;
//	}
//
//	public int update(List<AddMeData> dataList) throws DatabaseOperateException {
//		int updateCount = 0;
//
//		for (AddMeData data : dataList) {
//			updateCount += update(data);
//		}
//		return updateCount;
//	}
//
//	public List<AddMeData> query(String whereClause, String [] whereArgs, String orderBy) throws DatabaseOperateException {
//		List<AddMeData> dataList = new ArrayList<AddMeData>();
//		
//		Cursor cursor = null;
//		try {
//			cursor = getReadableDatabase().query(AddMeTable.TABLE_NAME, null, whereClause, whereArgs, null, null, orderBy);
//			while (cursor.moveToNext()) {
//				AddMeData data = new AddMeData();
//				data.setArticleId(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_ID)));
//				data.setUserId(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_USER_ID)));
//				data.setUserName(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_USER_NAME)));
//				data.setUserImage(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_USER_IMAGE)));
//				data.setPublishTime(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_PUBLISH_TIME)));
//				data.setArticleTitle(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_TITLE)));
//				data.setArticleContent(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_CONTENT)));
//				data.setArticleContentImage(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_CONTENT_IMAGE)));
//				data.setArticleReplyName(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_REPLY_NAME)));
//				data.setArticleReply(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_REPLY)));
//				data.setArticleReplyImage(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_REPLY_IMAGE)));
//				data.setArticleLikeCount(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_LIKE_COUNT)));
//				data.setArticleCommentCount(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_COMMENT_COUNT)));
//				data.setArticleForwardCount(cursor.getString(cursor.getColumnIndex(AddMeTable.COLUMN_ARTICLE_FORWARD_COUNT)));
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
