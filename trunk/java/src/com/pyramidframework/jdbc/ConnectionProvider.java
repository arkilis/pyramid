package com.pyramidframework.jdbc;

import java.sql.Connection;

/**
 * 生成连接的接口
 * @author Mikab Peng
 *
 */
public interface ConnectionProvider {
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	public Connection getConnection();
	
	/**
	 * 关闭连接
	 */
	public void closeConnection(Connection connection);
	
}
