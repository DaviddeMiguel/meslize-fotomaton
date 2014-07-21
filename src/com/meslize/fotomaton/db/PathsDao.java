package com.meslize.fotomaton.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.framework.library.db.IDao;
import com.framework.library.util.Functions;
import com.meslize.fotomaton.model.Path;

public class PathsDao  implements IDao<Path>{
	// Database fields
	private SQLiteDatabase database;
	
	protected String[] allColumns = {IDao._ID,
									 FColumns.DATE,
									 FColumns.PATH,
									 FColumns.THUMB};
		
	public static final int _ID_DATA 		= 0;
	public static final int DATE_DATA 		= 1;
	public static final int PATH_DATA 		= 2;
	public static final int THUMB_DATA 		= 3;
	
	@SuppressWarnings("unused")
	private PathsDao(){}

	protected PathsDao(SQLiteDatabase database){
		this.database = database;
	}

	public SQLiteStatement getBulkInsert(){
        SQLiteStatement insStmt = database.compileStatement("INSERT INTO " + FSQLiteHelper.TABLE_PATHS + 
        		" ( " + FColumns.DATE + ", " +
        		FColumns.PATH + ", " +
        		FColumns.THUMB + ") " +
        		"VALUES (?, ?, ?);");
        
        return insStmt;
	}

	public SQLiteStatement fillBulkInsert(SQLiteStatement insStmt, List<Path> all){
		for(int i=0; i<all.size(); i++){        	
            insStmt.bindString(DATE_DATA, all.get(i).getDate());
            insStmt.bindString(PATH_DATA, all.get(i).getPath());
            insStmt.bindString(THUMB_DATA, all.get(i).getThumb());
            
            all.get(i).setId(insStmt.executeInsert());
        }
        
        return insStmt;
	}

	@Override
	public Path add(Path type) {
		int result = update(type);
		if(result == 0 || result == -1){
			ContentValues values = new ContentValues();
			values.put(FColumns.DATE , type.getDate());
			values.put(FColumns.PATH , type.getPath());
			values.put(FColumns.THUMB , type.getThumb());
			
			long insertId = database.insert(FSQLiteHelper.TABLE_PATHS, null, values);
			type.setId(insertId);
			return type;
		}else{
			return get(type);
		}
	}

	@Override
	public List<Path> addAll(List<Path> all) {
		database.delete(FSQLiteHelper.TABLE_PATHS, null, null);
		
		SQLiteStatement insStmt = getBulkInsert();
		database.beginTransaction();
	
		try{
	        try {
	        	fillBulkInsert(insStmt, all);
	        	insStmt.close();
	        }catch(Exception e){
	        	Functions.log(e);
	        }
			
			database.setTransactionSuccessful();
        } finally {
        	database.endTransaction();    
        }

		return all;
	}

	@Override
	public int delete(long id) {
		return database.delete(FSQLiteHelper.TABLE_PATHS, IDao._ID + " = " + id, null);
	}

	@Override
	public int delete(Path type) {
		return database.delete(FSQLiteHelper.TABLE_PATHS, IDao._ID + " = " + type.getId(), null);
	}

	@Override
	public int update(Path type) {
		ContentValues values = new ContentValues();
		values.put(FColumns.DATE , type.getDate());
		values.put(FColumns.PATH , type.getPath());
		values.put(FColumns.THUMB , type.getThumb());
		
		return database.update(FSQLiteHelper.TABLE_PATHS, values,IDao._ID + " = " + type.getId(), null);
	}

	@Override
	public List<Path> getAll() {
		List<Path> result = new ArrayList<Path>();
		
		Cursor cursor = 
			database.query(FSQLiteHelper.TABLE_PATHS, allColumns, null, null, null, null, IDao._ID + " DESC");
		
		try {
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {					
					result.add(cursorToType(cursor));
				}
			}
		}catch (Exception e){
			Functions.log(e);
		}finally{
			cursor.close();
		}
		
		return result;
	}

	@Override
	public Path get(long id) {
		String[] args = new String[1];
		args[0] = String.valueOf(id);
		
		Cursor cursor = database.rawQuery("SELECT " + getAllColumns(allColumns) + " FROM " + FSQLiteHelper.TABLE_PATHS + " WHERE " + IDao._ID + " = ?" + " ORDER BY " + IDao._ID + " DESC", args);
		
		if(cursor.getCount() > 0){
			cursor.moveToFirst();

			Path result = cursorToType(cursor);
			
			cursor.close();
			
			return result;
		}else{
			return null;
		}
	}

	@Override
	public Path get(Path type) {
		String[] args = new String[1];
		args[0] = String.valueOf(type.getId());
		
		Cursor cursor = database.rawQuery("SELECT " + getAllColumns(allColumns) + " FROM " + FSQLiteHelper.TABLE_PATHS + " WHERE " + IDao._ID + " = ?" + " ORDER BY " + IDao.ID + " DESC", args);
		
		if(cursor.getCount() > 0){
			cursor.moveToFirst();

			Path result = cursorToType(cursor);
			
			cursor.close();
			
			return result;
		}else{
			return null;
		}
	}

	@Override
	public Path get(String id) {
		String[] args = new String[1];
		args[0] = id;
		
		Cursor cursor = database.rawQuery("SELECT " + getAllColumns(allColumns) + " FROM " + FSQLiteHelper.TABLE_PATHS + " WHERE " + IDao._ID + " = ?" + " ORDER BY " + IDao._ID + " DESC", args);
		
		if(cursor.getCount() > 0){
			cursor.moveToFirst();

			Path result = cursorToType(cursor);
			
			cursor.close();
			
			return result;
		}else{
			return null;
		}
	}

	@Override
	public Path cursorToType(Cursor cursor) {
		try{
			Path result = new Path();
			result.setId(cursor.getInt(_ID_DATA));
			result.setDate(cursor.getString(DATE_DATA));
			result.setPath(cursor.getString(PATH_DATA));
			result.setThumb(cursor.getString(THUMB_DATA));
			
			return result;
		}catch(Exception e){
			Functions.log(e);
		}
		
		return null;
	}

	public String getAllColumns(String[]columns){
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<columns.length; i++){
			builder.append(columns[i]);
			
			if(i < columns.length -1){
				builder.append(", ");
			}
		}
		
		return builder.toString();
	}

	@Override
	public long count() {
		return 0;
	}
}
