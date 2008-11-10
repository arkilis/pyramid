package com.pyramidframework.jdbc;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * 使用datasource获得连接
 * 
 * @author Mikab Peng
 * 
 */
public class DataSourceConnectionProvider implements ConnectionProvider {

	DataSource dataSource = null;

	/**
	 * 注入的dataSource获取连接
	 */
	public Connection getConnection() {

		try {
			return dataSource.getConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 直接关闭数据库连接
	 */
	public void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			// DO Nothing
		}

	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
