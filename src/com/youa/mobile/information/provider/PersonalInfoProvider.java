package com.youa.mobile.information.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.youa.mobile.common.base.BaseProvider;
import com.youa.mobile.common.data.BaseUserData;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.db.FriendTable;
import com.youa.mobile.information.data.PersonalInformationData;

public class PersonalInfoProvider extends BaseProvider {
	public static String Lock = "dblock";

	public void replaceInformation(List<UserData> userDataList) {
		for (UserData userData : userDataList) {

			ContentValues contentValues = new ContentValues();
			contentValues.put(FriendTable.COLUMN_USERID, userData.getUserId());
			contentValues.put(FriendTable.COLUMN_USERNAME,
					userData.getUserName());
			contentValues.put(FriendTable.COLUMN_IMAGEID,
					userData.getHeaderImgid());
			contentValues.put(FriendTable.COLUMN_SEX, userData.getSexInt());
			getWritableDatabase().replace(FriendTable.TABLE_NAME, null,
					contentValues);
		}
	}

	public void replaceInformation(UserData userData) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(FriendTable.COLUMN_USERID, userData.getUserId());
		contentValues.put(FriendTable.COLUMN_USERNAME, userData.getUserName());
		contentValues
				.put(FriendTable.COLUMN_IMAGEID, userData.getHeaderImgid());

		getWritableDatabase().replace(FriendTable.TABLE_NAME, null,
				contentValues);
	}

	public void deleteInformation(String uid) {
		getWritableDatabase().delete(FriendTable.TABLE_NAME,
				FriendTable.COLUMN_USERID + " = ?", new String[] { uid });
	}

	public void deleteInformation() {
		getWritableDatabase().delete(FriendTable.TABLE_NAME, null, null);
	}

	public void replaceInformation(
			PersonalInformationData personalInformationData) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(FriendTable.COLUMN_USERID,
				personalInformationData.getUserId());
		contentValues.put(FriendTable.COLUMN_USERNAME,
				personalInformationData.getUserName());
		contentValues.put(FriendTable.COLUMN_SEX,
				personalInformationData.getSexInt());
		contentValues.put(FriendTable.COLUMN_CITY,
				personalInformationData.getCity());
		contentValues.put(FriendTable.COLUMN_BIRTHDAYYEAR,
				personalInformationData.getBirthdayDay());
		contentValues.put(FriendTable.COLUMN_BIRTHDAYMONTH,
				personalInformationData.getBirthdayMonth());
		contentValues.put(FriendTable.COLUMN_BIRTHDAYDAY,
				personalInformationData.getBirthdayDay());
		contentValues.put(FriendTable.COLUMN_INTRODUCE,
				personalInformationData.getIntroduce());

		getWritableDatabase().replace(FriendTable.TABLE_NAME, null,
				contentValues);
	}

	public PersonalInformationData getInformation(String paramuid) {
		PersonalInformationData mPersonalInformationData = null;
		Cursor cursor = getReadableDatabase().query(FriendTable.TABLE_NAME,
				new String[] { "*" }, FriendTable.COLUMN_USERID + "=?",
				new String[] { paramuid }, null, null, null);
		if (cursor.moveToNext()) {
			String uid = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_USERID));
			String name = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_USERNAME));
			String sex = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_SEX));
			String city = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_CITY));
			String year = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_BIRTHDAYYEAR));
			String month = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_BIRTHDAYMONTH));
			String day = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_BIRTHDAYDAY));
			String introuce = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_INTRODUCE));
			mPersonalInformationData = new PersonalInformationData(uid, name,
					sex, city, year, month, day, introuce);

		}
		if (cursor != null) {
			cursor.close();
		}
		return mPersonalInformationData;
	}

	public List<UserData> searchFriendList() {
		BaseUserData baseUserData = null;
		Cursor cursor = null;
		try {
			cursor = getReadableDatabase().query(FriendTable.TABLE_NAME,
					new String[] { "*" }, null, null, null, null, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			cursor = getReadableDatabase().query(FriendTable.TABLE_NAME,
					new String[] { "*" }, null, null, null, null, null);
		}

		List<UserData> userDataList = new ArrayList<UserData>();
		while (cursor.moveToNext()) {
			String uid = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_USERID));
			String name = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_USERNAME));
			String sex = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_SEX));
			String image = cursor.getString(cursor
					.getColumnIndex(FriendTable.COLUMN_IMAGEID));
			baseUserData = new BaseUserData(uid, name, image, sex);
			userDataList.add(baseUserData);
		}
		if (cursor != null) {
			cursor.close();
		}
		return userDataList;
	}
}
