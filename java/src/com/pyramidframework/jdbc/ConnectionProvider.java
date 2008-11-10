package com.pyramidframework.jdbc;

import java.sql.Connection;

/**
 * �������ӵĽӿ�
 * @author Mikab Peng
 *
 */
public interface ConnectionProvider {
	
	/**
	 * ��ȡ���ݿ�����
	 * @return
	 */
	public Connection getConnection();
	
	/**
	 * �ر�����
	 */
	public void closeConnection(Connection connection);
	
}
