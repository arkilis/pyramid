package com.pyramidframework.dao;

import java.sql.ResultSet;
import java.util.List;

import com.pyramidframework.dao.model.DataType;

public interface SqlDAO extends ValueObjectDAO {

	/**
	 * ִ�и������SQL������Ӱ�������
	 * 
	 * @param sql
	 * @param
	 * @param datatypes
	 *            {@link DataType}�ļ��ϣ�����ΪNULL
	 * @return
	 * @throws DAOException
	 */
	public int executeUpdate(String sql, List paramters, List datatypes) throws DAOException;

	/**
	 * ֱ�ӷ���2ά���ݵ���ʽ�����ݶ�����getObject����ʽ���ص�
	 * 
	 * @param sql
	 * @return <code>Map&lt;String,OBject&gt;</code>��ʽ������
	 * @throws DAOException
	 */
	public List queryData(String sql, List paramters, List datatypes) throws DAOException;

	/**
	 * ��ѯ���ݣ����ݵĴ�����handler���
	 * 
	 * @param sql
	 * @param handler
	 * @throws DAOException
	 * @return ResultSetHandler�ķ���ֵ
	 */
	public Object queryData(String sql, List paramters, List datatypes, ResultSetHandler handler) throws DAOException;

	/**
	 * ���������Ĵ�����
	 * 
	 * @author Mikab Peng
	 * 
	 */
	public static interface ResultSetHandler {
		/**
		 * �ڵõ�ResultSet֮���û���ȫ�������ݵķ���
		 * 
		 * @param resultSet
		 * @throws DAOException
		 */
		public Object handleResult(ResultSet resultSet) throws DAOException;
	}
}
