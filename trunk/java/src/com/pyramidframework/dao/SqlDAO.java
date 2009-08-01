package com.pyramidframework.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.pyramidframework.dao.model.datatype.DataType;

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
	 * 和{@link ValueObjectDAO#query(String, Map, String, int, int)}一样，但是使用handler返回结果
	 * @param modelName
	 * @param queryValues
	 * @param orderBy
	 * @param pageSize
	 * @param page
	 * @param handler
	 * @return
	 * @throws DAOException
	 */
	public Object query(String modelName, Map queryValues, String orderBy, int pageSize, int page,ResultSetHandler handler) throws DAOException;
	

	/**
	 * 
	 * @param sql
	 * @param queryValues
	 * @param pageSize
	 * @param page
	 * @return
	 * @throws DAOException
	 */
	public PaginatedResult queryData(String sql, List paramters, List datatypes,int pageSize, int page) throws DAOException;
	
	
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
