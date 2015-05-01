package com.youa.mobile.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, DataPackage.DB_NAME, null, DataPackage.DB_VERSION);
	}

	final private String CREATE_ACCOUNT = "create table if not exists "
			+ DataPackage.TB_ACCOUNT_NAME + " (" + DataPackage.Account._ID
			+ " integer primary key autoincrement," + DataPackage.Account.USER_ID
			+ " integer," + DataPackage.Account.USER_NAME
			+ " text unique on conflict replace,"
			+ DataPackage.Account.USER_PASSWORD + " text,"
			+ DataPackage.Account.USER_SESSION + " text,"
			+ DataPackage.Account.USER_DENTITY + " text)";
	final public String CREATE_PICTURE = "create table if not exists "
			+ DataPackage.TB_PICTURE_NAME + " (" + DataPackage.Picture._ID
			+ " integer primary key autoincrement," + DataPackage.Picture.URL
			+ " text unique on conflict replace,"
			+ DataPackage.Picture.FILE_NAME + " text,"
			+ DataPackage.Picture.FILE_LENGTH + " text)";//UNIQUE ON CONFLICT REPLACE

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ACCOUNT);
		db.execSQL(CREATE_PICTURE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DataPackage.TB_ACCOUNT_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DataPackage.TB_PICTURE_NAME);
		onCreate(db);
		// db.execSQL("ALTER TABLE " + NOTES_TABLE_NAME + " ADD COLUMN "
		// + Notes.TAGS + " TEXT;");
		// db.execSQL("ALTER TABLE " + NOTES_TABLE_NAME + " ADD COLUMN "
		// + Notes.ENCRYPTED + " INTEGER;");
		// alter table appstore_souapp_app_androidmarket
		// drop column getPriceCurrency
	}

}
