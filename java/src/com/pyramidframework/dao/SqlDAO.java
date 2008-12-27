package com.pyramidframework.dao;

import java.sql.ResultSet;
import java.util.List;

import com.pyramidframework.dao.model.DataType;

public interface SqlDAO extends ValueObjectDAO {

	/**
	 * 执行更新类的SQL，返回影响的行数
	 * 
	 * @param sql
	 * @param
	 * @param datatypes
	 *            {@link DataType}的集合，可以为NULL
	 * @return
	 * @throws DAOException
	 */
	public int executeUpdate(String sql, List paramters, List datatypes) throws DAOException;

	/**
	 * 直接返回2维数据的形式，数据都是以getObject的形式返回的
	 * 
	 * @param sql
	 * @return <code>Map&lt;String,OBject&gt;</code>形式的数据
	 * @throws DAOException
	 */
	public List queryData(String sql, List paramters, List datatypes) throws DAOException;

	/**
	 * 查询数据，数据的处理交给handler完成
	 * 
	 * @param sql
	 * @param handler
	 * @throws DAOException
	 * @return ResultSetHandler的返回值
	 */
	public Object queryData(String sql, List paramters, List datatypes, ResultSetHandler handler) throws DAOException;

	/**
	 * 处理结果集的处理类
	 * 
	 * @author Mikab Peng
	 * 
	 */
	public static interface ResultSetHandler {
		/**
		 * 在得到ResultSet之后，用户完全控制数据的访问
		 * 
		 * @param resultSet
		 * @throws DAOException
		 */
		public Object handleResult(ResultSet resultSet) throws DAOException;
	}
}
