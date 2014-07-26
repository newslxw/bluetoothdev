package com.alensic.nursing.mobile.dao;

import com.alensic.nursing.mobile.common.Dao;
import com.alensic.nursing.mobile.model.Users;

public interface UsersDao  extends Dao<Users>{

	/**
	 * 用户登录
	 * 1、更新session
	 * 2、preferences,
	 * 3、检查是否新用户，是就保存到用户表中
	 * @param user
	 */
	public void login(Users user);
	
	/**
	 * 获取上次登录的用户名和病区
	 * 先从session中取，没有在从preferences中取
	 * @return
	 */
	public Users getLastUser();
	
	
}
