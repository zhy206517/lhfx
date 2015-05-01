package com.youa.mobile.location.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.youa.mobile.common.base.BaseProvider;
import com.youa.mobile.common.db.LocationTable;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.location.data.LocationData;

public class SuggestPlaceProvider extends BaseProvider {
	private static final LocationTable mLocationTable = new LocationTable();

	public void replaceInformation(List<LocationData> locDataList) {
		for (LocationData locData : locDataList) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(LocationTable.COLUMN_USER_ID, ApplicationManager
					.getInstance().getUserId());
			contentValues.put(LocationTable.COLUMN_PLACE_ID, locData.sPid);
			contentValues.put(LocationTable.COLUMN_PLACE_NAME, locData.locName);
			contentValues.put(LocationTable.COLUMN_PLACE_X, locData.latitude);
			contentValues.put(LocationTable.COLUMN_PLACE_Y, locData.longitude);
			contentValues.put(LocationTable.COLUMN_PLACE_ADDR, locData.addName);
			contentValues.put(LocationTable.COLUMN_PLACE_TYPE, locData.type);
			contentValues.put(LocationTable.COLUMN_TIME_STAMP,
					System.currentTimeMillis());
			try {
				getWritableDatabase().replace(LocationTable.TABLE_NAME, null,
						contentValues);
			} catch (SQLiteException e) {
				mLocationTable.createIfNoExists(getWritableDatabase());
				getWritableDatabase().replace(LocationTable.TABLE_NAME, null,
						contentValues);
			} catch (IllegalStateException e) {
				mLocationTable.addColumn(getReadableDatabase(),
						mLocationTable.COLUMN_PLACE_TYPE, "TEXT");
				getWritableDatabase().replace(LocationTable.TABLE_NAME, null,
						contentValues);
			}
		}
	}

	public void replaceInformation(LocationData locData) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(LocationTable.COLUMN_USER_ID, ApplicationManager
				.getInstance().getUserId());
		contentValues.put(LocationTable.COLUMN_PLACE_ID, locData.sPid);
		contentValues.put(LocationTable.COLUMN_PLACE_NAME, locData.locName);
		contentValues.put(LocationTable.COLUMN_PLACE_X, locData.latitude);
		contentValues.put(LocationTable.COLUMN_PLACE_Y, locData.longitude);
		contentValues.put(LocationTable.COLUMN_PLACE_ADDR, locData.addName);
		contentValues.put(LocationTable.COLUMN_PLACE_TYPE, locData.type);
		contentValues.put(LocationTable.COLUMN_TIME_STAMP,
				System.currentTimeMillis());
		LocationData locationData = getInformation(locData.locName);
		try {
			if (locationData != null) {
				contentValues = new ContentValues();
				contentValues.put(LocationTable.COLUMN_TIME_STAMP,
						System.currentTimeMillis());
				try {
					getWritableDatabase().update(LocationTable.TABLE_NAME,
							contentValues, "id=? ",
							new String[] { String.valueOf(locationData.id) });
				} catch (SQLiteException e) {
					mLocationTable.createIfNoExists(getWritableDatabase());
					getWritableDatabase().update(LocationTable.TABLE_NAME,
							contentValues, "id=? ",
							new String[] { String.valueOf(locationData.id) });
				}

			} else {
				try {
					getWritableDatabase().replace(LocationTable.TABLE_NAME,
							null, contentValues);
				} catch (SQLiteException e) {
					mLocationTable.createIfNoExists(getWritableDatabase());
					getWritableDatabase().replace(LocationTable.TABLE_NAME,
							null, contentValues);
				}

			}
		} catch (IllegalStateException e) {
			mLocationTable.addColumn(getReadableDatabase(),
					mLocationTable.COLUMN_PLACE_TYPE, "TEXT");
		}

	}

	public void deleteInformation(String uid) {
		getWritableDatabase().delete(LocationTable.TABLE_NAME,
				LocationTable.COLUMN_USER_ID + " = ?", new String[] { uid });
	}

	public LocationData getInformation(String paramname) {
		LocationData mLocationData = null;
		Cursor cursor = null;
		try {
			cursor = getReadableDatabase().query(LocationTable.TABLE_NAME,
					new String[] { "*" },
					LocationTable.COLUMN_PLACE_NAME + "=?",
					new String[] { paramname }, null, null, null);
			if (cursor.moveToNext()) {
				int id = cursor.getInt(cursor
						.getColumnIndex(LocationTable.COLUMN_ID));
				int x = cursor.getInt(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_X));
				int y = cursor.getInt(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_Y));
				String name = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_NAME));
				String pid = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_ID));
				String address = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_ADDR));
				String type = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_TYPE));
				mLocationData = new LocationData(id, x, y, name, pid, address,
						type);
			}
		} catch (SQLiteException e) {
			mLocationTable.createIfNoExists(getWritableDatabase());
			cursor = getReadableDatabase().query(LocationTable.TABLE_NAME,
					new String[] { "*" },
					LocationTable.COLUMN_PLACE_NAME + "=?",
					new String[] { paramname }, null, null, null);
		} catch (IllegalStateException e) {
			mLocationTable.addColumn(getReadableDatabase(),
					mLocationTable.COLUMN_PLACE_TYPE, "TEXT");
			cursor = getReadableDatabase().query(LocationTable.TABLE_NAME,
					new String[] { "*" }, null, null, null, null,
					"timestamp DESC limit 5");
		}
		if (cursor != null) {
			cursor.close();
		}
		return mLocationData;
	}

	public List<LocationData> searchLocList() {
		LocationData baseUserData = null;
		Cursor cursor = null;
		List<LocationData> locDataList = new ArrayList<LocationData>();
		try {
			cursor = getReadableDatabase().query(LocationTable.TABLE_NAME,
					new String[] { "*" }, null, null, null, null,
					"timestamp DESC limit 5");
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_NAME));
				String pid = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_ID));
				String address = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_ADDR));
				String type = cursor.getString(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_TYPE));
				int x = cursor.getInt(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_X));
				int y = cursor.getInt(cursor
						.getColumnIndex(LocationTable.COLUMN_PLACE_Y));
				baseUserData = new LocationData(x, y, name, pid, address, type);
				locDataList.add(baseUserData);
			}
		} catch (SQLiteException e) {
			mLocationTable.createIfNoExists(getWritableDatabase());
			cursor = getReadableDatabase().query(LocationTable.TABLE_NAME,
					new String[] { "*" }, null, null, null, null,
					"timestamp DESC limit 5");
		} catch (IllegalStateException e) {
			mLocationTable.addColumn(getReadableDatabase(),
					mLocationTable.COLUMN_PLACE_TYPE, "TEXT");
			cursor = getReadableDatabase().query(LocationTable.TABLE_NAME,
					new String[] { "*" }, null, null, null, null,
					"timestamp DESC limit 5");
		}
		if (cursor != null) {
			cursor.close();
		}
		return locDataList;
	}
}
