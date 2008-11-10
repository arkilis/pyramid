package com.pyramidframework.jdbc;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * ʹ��datasource�������
 * 
 * @author Mikab Peng
 * 
 */
public class DataSourceConnectionProvider implements ConnectionProvider {

	DataSource dataSource = null;

	/**
	 * ע���dataSource��ȡ����
	 */
	public Connection getConnection() {

		try {
			return dataSource.getConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * ֱ�ӹر����ݿ�����
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
