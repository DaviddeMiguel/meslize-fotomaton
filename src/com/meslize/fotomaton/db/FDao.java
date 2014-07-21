package com.meslize.fotomaton.db;

import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.meslize.fotomaton.model.Path;

public class FDao {
	// Database fields
	private SQLiteDatabase database;
	private FSQLiteHelper dbHelper;
	
	private PathsDao pathsDao;
	
	@SuppressWarnings("unused")
	private FDao(){}
	
	public FDao(Context context) {
		dbHelper = new FSQLiteHelper(context);
		open();
	}
	
	public FDao(SQLiteDatabase database) {
		this.database = database;
	}

	public void open() throws SQLException {
		if(database == null || database.isOpen() == false){
			database = dbHelper.getWritableDatabase();
		}
	}

	public void close() {
		dbHelper.close();
	}
	
	private PathsDao getPathsDao(){
		if(pathsDao == null){
			pathsDao = new PathsDao(database);
		}
		
		return pathsDao;
	}
	
	/*
	 * Paths methods
	 */
	public Path addPath(Path path){
		return getPathsDao().add(path);
	}
	
	public List<Path> addPaths(List<Path> paths){
		return getPathsDao().addAll(paths);
	}
	
	public int deletePath(long id) {
		return getPathsDao().delete(id);
	}
	
	public int deletePath(Path path) {
		return getPathsDao().delete(path);
	}
	
	public int updatePath(Path path) {
		return getPathsDao().update(path);
	}
	
	public List<Path> getPaths() {
		return getPathsDao().getAll();
	}
	
	public Path getPath(long id) {
		return getPathsDao().get(id);
	}
	
	public Path getPath(Path path) {
		return getPathsDao().get(path);
	}
	
	public Path getPath(String id) {
		return getPathsDao().get(id);
	}
	
	/*
	 * End paths methods
	 */
}
