package com.meslize.fotomaton.model;

import java.io.Serializable;

import com.framework.library.model.IModel;

public class Path implements IModel, Serializable {

	private static final long serialVersionUID = 1L;
	private long _id;
	private String date = "";
	private String path = "";
	private String thumb = "";
	
	public Path() {}
	
	public Path(String date, String path) {
		super();
		this.date = date;
		this.path = path;
	}
	
	public Path(String date, String path, String thumb) {
		super();
		this.date = date;
		this.path = path;
		this.thumb = thumb;
	}

	@Override
	public long getId() {
		return this._id;
	}

	@Override
	public void setId(long id) {
		this._id = id;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
}
