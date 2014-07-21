package com.meslize.fotomaton.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.framework.library.db.IDao;

public class FSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "fotomaton.db";
	private static final int DATABASE_VERSION = 2;

	public static final String TABLE_PATHS = "Paths";

	private static final String 
	CREATE_TABLE_PATHS = "create table "
		+ TABLE_PATHS + "( " + 
			IDao._ID + " integer primary key autoincrement not null unique, " +
			FColumns.DATE + " text, " +
			FColumns.PATH + " text, " +
			FColumns.THUMB + " text " +
	");";

	public FSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_PATHS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(FSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATHS);
		
		onCreate(db);
	}
}
