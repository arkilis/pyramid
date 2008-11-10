package com.pyramidframework.dao;

import java.util.Map;

/**
 * 针对专门的值对象的DAO接口，所有的操作都是VO
 * 
 * @author Mikab Peng
 * 
 */
public interface ValueObjectDAO {
	/**
	 * 添加数据
	 * 
	 * @param dataObject
	 * @return Object 执行添加后的数据，一般和dataObject是同一个对象
	 */
	public Object add(Object dataObject) throws DAOException;

	/**
	 * 修改数据
	 * 
	 * @param dataObject
	 * @return Object 执行修改后的数据，一般和dataObject是同一个对象
	 */
	public Object modify(Object dataObject) throws DAOException;

	/**
	 * 删除数据
	 * 
	 * @param dataObject
	 * @return Object 执行添加后的数据，一般和dataObject是同一个对象
	 */
	public Object remove(Object dataObject) throws DAOException;

	/**
	 * 根据指定的查询条件获取一个
	 * 
	 * @param modelName
	 *            查询的数据类型
	 * @param primaryKeyValues
	 *            主键的值的集合
	 * @return
	 * @throws DAOException
	 */
	public Object retrieveData(String modelName, Map primaryKeyValues) throws DAOException;

}
