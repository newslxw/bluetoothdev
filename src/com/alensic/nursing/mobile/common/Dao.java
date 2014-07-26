package com.alensic.nursing.mobile.common;

import java.util.List;

import android.database.Cursor;

public interface Dao<T> {

	T getById(Integer id);
	List<T> query(T bean);

	void insert(T bean);

	void update(T bean);

	void delete(Integer id);
	
	
}
