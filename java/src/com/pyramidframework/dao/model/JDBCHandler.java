package com.pyramidframework.dao.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyramidframework.dao.DAOException;

/**
 * JDBC相关的功能
 * 
 * @author Mikab Peng
 * 
 */
public interface JDBCHandler {

	/**
	 * 设置statement 的参数
	 * 
	 * @param statement
	 * @param index
	 * @param paramValue
	 * @throws DAOException
	 */
	public void setParameter(PreparedStatement statement, int index, Object paramValue) throws SQLException;

	/**
	 * 从ResultSet中获取选择的结果
	 * 
	 * @param resultSet
	 * @param index
	 * @return
	 * @throws DAOException
	 */
	public Object getValueFromResultSet(ResultSet resultSet, int index) throws SQLException;

	/**
	 * 得到在JDBC接口中对应的类型
	 * 
	 * @return
	 */
	public abstract int getJDBCType();
}
