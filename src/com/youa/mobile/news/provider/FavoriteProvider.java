//package com.youa.mobile.news.provider;
//
//import java.util.ArrayList;
//import java.util.List;
//import android.content.ContentValues;
//import android.database.Cursor;
//import com.youa.mobile.common.base.BaseProvider;
//import com.youa.mobile.common.db.AddMeTable;
//import com.youa.mobile.common.db.FavoriteTable;
//import com.youa.mobile.news.data.FavoriteData;
//import com.youa.mobile.news.exception.DatabaseOperateException;
//
//public class FavoriteProvider extends BaseProvider {
//
//	public long insertFavoriteData(FavoriteData data) throws DatabaseOperateException {
//		long rowID = -1;
//		ContentValues cr = new ContentValues();
//		cr.put(AddMeTable.COLUMN_ARTICLE_ID, data.getArticleId());
//		cr.put(FavoriteTable.COLUMN_USER_ID, data.getUserId());
//		cr.put(FavoriteTable.COLUMN_LAST_LIKE_NAME, data.getLastLikeName());
//		cr.put(FavoriteTable.COLUMN_LAST_LIKE_TIME, data.getLastLikeTime());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_TITLE, data.getArticleTitle());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_CONTENT, data.getArticleContent());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_CONTENT_IMAGE, data.getArticleContentImage());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_REPLY_NAME, data.getArticleReplyName());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_REPLY, data.getArticleReply());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_REPLY_IMAGE, data.getArticleReplyImage());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_LIKE_COUNT, data.getArticleLikeCount());
//		try {
//			rowID = getWritableDatabase().insert(FavoriteTable.TABLE_NAME, null, cr);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return rowID;
//	}
//
//	public int insert(List<FavoriteData> dataList) throws DatabaseOperateException {
//		int insertCount = 0;
//		for (FavoriteData data : dataList) {
//			long rowID = insertFavoriteData(data);
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
//			deleteCount = getWritableDatabase().delete(FavoriteTable.TABLE_NAME, whereClause, whereArgs);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return deleteCount;
//	}
//
//	public int deleteAll() throws DatabaseOperateException {
//		int deleteCount = 0;
//		try {
//			deleteCount = getWritableDatabase().delete(FavoriteTable.TABLE_NAME, null, null);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return deleteCount;
//	}
//
//	public int update(FavoriteData data) throws DatabaseOperateException {
//
//		ContentValues cr = new ContentValues();
//		cr.put(FavoriteTable.COLUMN_ARTICLE_ID, data.getArticleId());
//		cr.put(FavoriteTable.COLUMN_USER_ID, data.getUserId());
//		cr.put(FavoriteTable.COLUMN_LAST_LIKE_NAME, data.getLastLikeName());
//		cr.put(FavoriteTable.COLUMN_LAST_LIKE_TIME, data.getLastLikeTime());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_TITLE, data.getArticleTitle());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_CONTENT, data.getArticleContent());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_CONTENT_IMAGE, data.getArticleContentImage());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_REPLY_NAME, data.getArticleReplyName());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_REPLY, data.getArticleReply());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_REPLY_IMAGE, data.getArticleReplyImage());
//		cr.put(FavoriteTable.COLUMN_ARTICLE_LIKE_COUNT, data.getArticleLikeCount());
//		String whereClause = FavoriteTable.COLUMN_ARTICLE_ID + " = ?";
//
//		String [] whereArgs = { String.valueOf(data.getArticleId()) };
//		int updateCount = 0;
//		try {
//			updateCount = getWritableDatabase().update(FavoriteTable.TABLE_NAME, cr, whereClause, whereArgs);
//		} catch (Exception e) {
//			throw new DatabaseOperateException("", e);
//		}
//		return updateCount;
//	}
//
//	public int update(List<FavoriteData> dataList) throws DatabaseOperateException {
//		int updateCount = 0;
//		for (FavoriteData data : dataList) {
//			updateCount += update(data);
//		}
//		return updateCount;
//	}
//
//	public List<FavoriteData> query(String whereClause, String [] whereArgs, String orderBy) throws DatabaseOperateException {
//		List<FavoriteData> dataList = new ArrayList<FavoriteData>();
//
//		Cursor cursor = null;
//
//		try {
//			cursor = getReadableDatabase().query(FavoriteTable.TABLE_NAME, null, whereClause, whereArgs, null, null, orderBy);
//			while (cursor.moveToNext()) {
//				FavoriteData data = new FavoriteData();
//				data.setArticleId(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_ID)));
////				data.setUserId(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_USER_ID)));
//				data.setLastLikeName(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_LAST_LIKE_NAME)));
//				data.setLastLikeTime(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_LAST_LIKE_TIME)));
//				data.setArticleTitle(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_TITLE)));
//				data.setArticleContent(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_CONTENT)));
//				data.setArticleContentImage(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_CONTENT_IMAGE)));
//				data.setArticleReplyName(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_REPLY_NAME)));
//				data.setArticleReply(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_REPLY)));
//				data.setArticleReplyImage(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_REPLY_IMAGE)));
//				data.setArticleLikeCount(cursor.getString(cursor.getColumnIndex(FavoriteTable.COLUMN_ARTICLE_LIKE_COUNT)));
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
//
//}
