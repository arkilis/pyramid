package com.pyramidframework.dao;

import java.sql.ResultSet;

public interface SqlDAO extends ValueObjectDAO,ConfigableQueryDAO {

	/**
	 * ֱ�ӷ���2ά���ݵ���ʽ�����ݶ�����getObject����ʽ���ص�
	 * 
	 * @param sql
	 * @param pageSize
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public Object[][] queryData(String sql, int pageSize, int page) throws DAOException;
	
	
	/**
	 * ��ѯ���ݣ����ݵĴ�����handler���
	 * @param sql
	 * @param pageSize
	 * @param page
	 * @param handler
	 * @throws DAOException
	 */
	public void queryData(String sql, int pageSize, int page, ResultSetHandler handler) throws DAOException;

	public static interface ResultSetHandler {
		/**
		 * �ڵõ�ResultSet֮���û���ȫ�������ݵķ���
		 * @param resultSet
		 * @throws DAOException
		 */
		public void handleResult(ResultSet resultSet) throws DAOException;
	}
}
