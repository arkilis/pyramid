package com.pyramidframework.dao.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyramidframework.dao.DAOException;

/**
 * JDBC��صĹ���
 * 
 * @author Mikab Peng
 * 
 */
public interface JDBCHandler {

	/**
	 * ����statement �Ĳ���
	 * 
	 * @param statement
	 * @param index
	 * @param paramValue
	 * @throws DAOException
	 */
	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException;

	/**
	 * ��ResultSet�л�ȡѡ��Ľ��
	 * 
	 * @param resultSet
	 * @param index
	 * @return
	 * @throws DAOException
	 */
	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException;

	/**
	 * �õ���JDBC�ӿ��ж�Ӧ������
	 * 
	 * @return
	 */
	public abstract int getJDBCType();
}
