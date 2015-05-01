package com.youa.mobile.store;

import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class YouAProvider extends ContentProvider {
	private DatabaseHelper databaseHelper;
	private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		uriMatcher.addURI(DataPackage.AUTHORITY, DataPackage.URL_ACCOUNT,
				DataPackage.CODE_ACCOUNT);
		uriMatcher.addURI(DataPackage.AUTHORITY, DataPackage.URL_PICTURE,
				DataPackage.CODE_PICTURE);
		uriMatcher.addURI(DataPackage.AUTHORITY,
				DataPackage.URL_PICTURE + "/#", DataPackage.CODE_PICTURE_ITEM);
		uriMatcher.addURI(DataPackage.AUTHORITY, DataPackage.URL_FEED,
				DataPackage.CODE_FEED);
		uriMatcher.addURI(DataPackage.AUTHORITY, DataPackage.URL_FEED + "/#",
				DataPackage.CODE_FEED_ITEM);
		uriMatcher.addURI(DataPackage.AUTHORITY, DataPackage.URL_INFORMATION,
				DataPackage.CODE_INFROMATION);
		uriMatcher.addURI(DataPackage.AUTHORITY, DataPackage.URL_INFORMATION
				+ "/#", DataPackage.CODE_INFROMATION_ITEM);
		uriMatcher.addURI(DataPackage.AUTHORITY, DataPackage.URL_PROFILE,
				DataPackage.CODE_PROFILE);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(this.getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String database_name = null;
		switch (uriMatcher.match(uri)) {
		case DataPackage.CODE_ACCOUNT:
			database_name = DataPackage.TB_ACCOUNT_NAME;
			break;
		case DataPackage.CODE_PICTURE:
			database_name = DataPackage.TB_PICTURE_NAME;
			break;
		case DataPackage.CODE_FEED:
			database_name = DataPackage.TB_FEED_NAME;
			break;
		case DataPackage.CODE_INFROMATION:
			database_name = DataPackage.TB_INFORMATION_NAME;
			break;
		case DataPackage.CODE_PROFILE:
			database_name = DataPackage.TB_PROFILE_NAME;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		long rowId = db.insert(database_name, null, values);
		if (rowId > 0) {
			Uri ur = ContentUris.withAppendedId(uri, rowId);
			getContext().getContentResolver().notifyChange(ur, null);
			return ur;
		}
		return null;
	}

	// 根据实际情况，确定uri是否带id，非id，则所有idurl可以删掉
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String database_name = null;
		switch (uriMatcher.match(uri)) {
		case DataPackage.CODE_ACCOUNT:
			database_name = DataPackage.TB_ACCOUNT_NAME;
			break;
		case DataPackage.CODE_PICTURE:
			database_name = DataPackage.TB_PICTURE_NAME;
			break;
		case DataPackage.CODE_PICTURE_ITEM:
			database_name = DataPackage.TB_PICTURE_NAME;
			long id = ContentUris.parseId(uri);
			selection = DataPackage.Picture._ID + "=" + id;
			if (selection != null && !selection.equals("")) {
				selection = selection + " and " + selection;
			}
			break;
		case DataPackage.CODE_FEED:
			database_name = DataPackage.TB_FEED_NAME;
			break;
		case DataPackage.CODE_FEED_ITEM:
			database_name = DataPackage.TB_FEED_NAME;
			break;
		case DataPackage.CODE_INFROMATION:
			database_name = DataPackage.TB_INFORMATION_NAME;
			break;
		case DataPackage.CODE_INFROMATION_ITEM:
			database_name = DataPackage.TB_INFORMATION_NAME;
			break;
		case DataPackage.CODE_PROFILE:
			database_name = DataPackage.TB_PROFILE_NAME;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(database_name, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String database_name = null;
		switch (uriMatcher.match(uri)) {
		case DataPackage.CODE_ACCOUNT:
			database_name = DataPackage.TB_ACCOUNT_NAME;
			break;
		case DataPackage.CODE_PICTURE:
			database_name = DataPackage.TB_PICTURE_NAME;
			break;
		case DataPackage.CODE_PICTURE_ITEM:
			database_name = DataPackage.TB_PICTURE_NAME;
			long id = ContentUris.parseId(uri);
			selection = DataPackage.Picture._ID + "=" + id;
			if (selection != null && !selection.equals("")) {
				selection = selection + " and " + selection;
			}
			break;
		case DataPackage.CODE_FEED:
			database_name = DataPackage.TB_FEED_NAME;
			break;
		case DataPackage.CODE_FEED_ITEM:
			database_name = DataPackage.TB_FEED_NAME;
			break;
		case DataPackage.CODE_INFROMATION:
			database_name = DataPackage.TB_INFORMATION_NAME;
			break;
		case DataPackage.CODE_INFROMATION_ITEM:
			database_name = DataPackage.TB_INFORMATION_NAME;
			break;
		case DataPackage.CODE_PROFILE:
			database_name = DataPackage.TB_PROFILE_NAME;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int count = db.update(database_name, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String database_name = null;
		switch (uriMatcher.match(uri)) {
		case DataPackage.CODE_ACCOUNT:
			database_name = DataPackage.TB_ACCOUNT_NAME;
			break;
		case DataPackage.CODE_PICTURE:
			database_name = DataPackage.TB_PICTURE_NAME;
			break;
		case DataPackage.CODE_PICTURE_ITEM:
			database_name = DataPackage.TB_PICTURE_NAME;
			long id = ContentUris.parseId(uri);
			selection = DataPackage.Picture._ID + "=" + id;
			if (selection != null && !selection.equals("")) {
				selection = selection + " and " + selection;
			}
			break;
		case DataPackage.CODE_FEED:
			database_name = DataPackage.TB_FEED_NAME;
			break;
		case DataPackage.CODE_FEED_ITEM:
			database_name = DataPackage.TB_FEED_NAME;
			break;
		case DataPackage.CODE_INFROMATION:
			database_name = DataPackage.TB_INFORMATION_NAME;
			break;
		case DataPackage.CODE_INFROMATION_ITEM:
			database_name = DataPackage.TB_INFORMATION_NAME;
			break;
		case DataPackage.CODE_PROFILE:
			database_name = DataPackage.TB_PROFILE_NAME;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int count = db.delete(database_name, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		if (uriMatcher.match(uri) != DataPackage.CODE_PICTURE_ITEM) {
			throw new IllegalArgumentException(
					"openFile not supported for directories");
		}
		return this.openFileHelper(uri, mode);
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case DataPackage.CODE_ACCOUNT:
			return DataPackage.Account.ACCOUNT_TYPE;
		case DataPackage.CODE_PICTURE:
			return DataPackage.Picture.PICTURE_TYPE;
		case DataPackage.CODE_PICTURE_ITEM:
			return DataPackage.Picture.PICTURE_ITEM_TYPE;
		case DataPackage.CODE_FEED:
			return DataPackage.Feed.FEED_TYPE;
		case DataPackage.CODE_FEED_ITEM:
			return DataPackage.Feed.FEED_ITEM_TYPE;
		case DataPackage.CODE_INFROMATION:
			return DataPackage.Information.INFORMATION__TYPE;
		case DataPackage.CODE_INFROMATION_ITEM:
			return DataPackage.Information.INFORMATION_ITEM_TYPE;
		case DataPackage.CODE_PROFILE:
			return DataPackage.Profile.PROFILE_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

}
