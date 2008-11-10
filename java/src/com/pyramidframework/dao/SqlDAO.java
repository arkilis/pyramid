package com.pyramidframework.dao;

import java.sql.ResultSet;

public interface SqlDAO extends ValueObjectDAO,ConfigableQueryDAO {

	/**
	 * 直接返回2维数据的形式，数据都是以getObject的形式返回的
	 * 
	 * @param sql
	 * @param pageSize
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public Object[][] queryData(String sql, int pageSize, int page) throws DAOException;
	
	
	/**
	 * 查询数据，数据的处理交给handler完成
	 * @param sql
	 * @param pageSize
	 * @param page
	 * @param handler
	 * @throws DAOException
	 */
	public void queryData(String sql, int pageSize, int page, ResultSetHandler handler) throws DAOException;

	public static interface ResultSetHandler {
		/**
		 * 在得到ResultSet之后，用户完全控制数据的访问
		 * @param resultSet
		 * @throws DAOException
		 */
		public void handleResult(ResultSet resultSet) throws DAOException;
	}
}
