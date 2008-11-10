package com.pyramidframework.dao;

import java.util.List;
import java.util.Map;

/**
 * 可配置查询的数据访问
 * @author Mikab Peng
 *
 */
public interface ConfigableQueryDAO {

	/**
	 * 根据系统内的查询名称和参数得到查询的结果
	 * @param queryName 查询名称
	 * @param queryParameters 查询的参数
	 * @return
	 * @throws DAOException
	 */
	List query(String queryName,Map queryParameters,int pageSize,int page) throws DAOException;
}
